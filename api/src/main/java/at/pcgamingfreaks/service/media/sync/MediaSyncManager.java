package at.pcgamingfreaks.service.media.sync;

import at.pcgamingfreaks.model.db.SyncJob;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.exceptions.MediaSourceNotConnectedException;
import at.pcgamingfreaks.exceptions.MediaSyncAlreadyQueued;
import at.pcgamingfreaks.model.repo.SyncJobRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static at.pcgamingfreaks.model.enums.SyncStatus.IN_PROGRESS;
import static at.pcgamingfreaks.model.enums.SyncStatus.PENDING;


@Slf4j
@Component
@RequiredArgsConstructor
public class MediaSyncManager {

	private final SyncJobRepository syncJobRepository;
	private final MediaSyncProcessor mediaSyncProcessor;
	private final ExecutorService syncExecutorService;

	@PostConstruct
	public void restartExistingSyncs() {
		List<SyncJob> existingJobs = syncJobRepository.findAllByStatus(List.of(PENDING, IN_PROGRESS));
		existingJobs.forEach((job) -> {
			syncExecutorService.submit(new MediaSyncJob(job, mediaSyncProcessor, syncJobRepository));
			log.debug("Resubmitted sync job (id: {}) for {} {} {}", job.getId(), job.getUser().getUsername(), job.getMediaSource(), job.getMediaType());
		});
	}

	public void enqueueSync(User user, MediaSource source, MediaType type) {
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
		job.setStatus(PENDING);
		syncJobRepository.saveAndFlush(job);

		syncExecutorService.submit(new MediaSyncJob(job, mediaSyncProcessor, syncJobRepository));
		log.debug("Submitted sync job (id: {}) for {} {} {}", job.getId(), user.getUsername(), source, type);
	}

	public Optional<SyncJob> getStatus(User user, MediaSource source, MediaType type) {
		return syncJobRepository.findFirstByUserAndMediaSourceAndMediaTypeAndStatusIn(user, source, type, List.of(IN_PROGRESS, PENDING));
	}
}
