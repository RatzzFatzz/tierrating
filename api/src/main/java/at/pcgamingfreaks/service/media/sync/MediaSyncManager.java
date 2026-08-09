package at.pcgamingfreaks.service.media.sync;

import at.pcgamingfreaks.model.db.SyncJob;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.exceptions.MediaSourceNotConnectedException;
import at.pcgamingfreaks.exceptions.MediaSyncAlreadyQueued;
import at.pcgamingfreaks.model.enums.SyncType;
import at.pcgamingfreaks.model.repo.SyncJobRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static at.pcgamingfreaks.model.enums.SyncStatus.IN_PROGRESS;
import static at.pcgamingfreaks.model.enums.SyncStatus.PENDING;


@Slf4j
@Component
@RequiredArgsConstructor
public class MediaSyncManager {

	private final SyncJobRepository syncJobRepository;
	private final MediaSyncProcessor mediaSyncProcessor;
	private final ScheduledExecutorService syncExecutorService;

	@PostConstruct
	public void restartExistingSyncs() {
		List<SyncJob> existingJobs = syncJobRepository.findAllByStatus(List.of(PENDING, IN_PROGRESS));
		existingJobs.forEach((job) -> {
			syncExecutorService.submit(new MediaSyncJob(job, mediaSyncProcessor, syncJobRepository));
			log.debug("Resubmitted sync job (id: {}) for {} {} {}", job.getId(), job.getUser().getUsername(), job.getMediaSource(), job.getMediaType());
		});
	}

	public void enqueueSync(User user, MediaSource source, MediaType type, SyncType syncType) {
		enqueueSync(user, source, type, syncType, 0);
	}

	public void enqueueSync(User user, MediaSource source, MediaType type, SyncType syncType, long delay) {
		Optional<SyncJob> runningJob = syncJobRepository.findActiveSyncByUserAndSourceAndTypeAndStatus(user, source, type, List.of(IN_PROGRESS, PENDING));
		if (runningJob.isPresent()) {
			log.debug("Tried to enqueue sync for {} {} {}, but sync already queued or in progress", user.getUsername(), source, type);
			throw new MediaSyncAlreadyQueued(user.getUsername(), source, type);
		}

		if (!user.getConnections().containsKey(source)) {
			throw new MediaSourceNotConnectedException(user.getUsername(), source);
		}

		SyncJob job = new SyncJob();
		job.setUser(user);
		job.setMediaSource(source);
		job.setMediaType(type);
		job.setType(syncType);
		job.setStatus(PENDING);
		syncJobRepository.saveAndFlush(job);

		syncExecutorService.schedule(new MediaSyncJob(job, mediaSyncProcessor, syncJobRepository), delay, TimeUnit.SECONDS);
		log.debug("Submitted sync job (id: {}) for {} {} {}", job.getId(), user.getUsername(), source, type);
	}

	public Optional<SyncJob> getStatus(User user, MediaSource source, MediaType mediaType, SyncType type) {
		return syncJobRepository.findFirstByUserAndMediaSourceAndMediaTypeAndTypeAndStatusIn(user, source, mediaType, type, List.of(IN_PROGRESS, PENDING));
	}
}
