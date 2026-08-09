package at.pcgamingfreaks.service.media;

import at.pcgamingfreaks.model.UserPrincipal;
import at.pcgamingfreaks.model.db.SyncJob;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.dto.sync.SyncStatusDTO;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.enums.SyncType;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.media.sync.MediaSyncManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaSyncService {
	private final UserRepository userRepository;
	private final MediaSyncManager syncManager;

	@Transactional
	public SyncStatusDTO status(String username, MediaSource source, MediaType type, SyncType syncType) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		Optional<SyncJob> job = syncManager.getStatus(user, source, type, syncType);
		// TODO: how should the response look when there is no running sync?
		// there should always be at least one sync (initial sync) and returned a complete status sounds fine
		return job.map(syncJob -> new SyncStatusDTO(syncJob.getMediaSource(), syncJob.getMediaType(), syncJob.getStatus(), syncJob.getStartedAt()))
				.orElseGet(SyncStatusDTO::new);
	}

	public void enqueue(UserPrincipal userPrincipal, MediaSource source, MediaType type, SyncType syncType) {
		User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new UsernameNotFoundException(userPrincipal.getUsername()));
		syncManager.enqueueSync(user, source, type, syncType);
	}
}
