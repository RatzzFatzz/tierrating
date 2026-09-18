package at.pcgamingfreaks.service.media;

import at.pcgamingfreaks.mapper.SyncStatusDtoMapper;
import at.pcgamingfreaks.model.UserPrincipal;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.dto.sync.SyncStatusResponseDTO;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.enums.SyncType;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.media.sync.MediaSyncManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaSyncService {
	private final UserRepository userRepository;
	private final MediaSyncManager syncManager;
	private final SyncStatusDtoMapper mapper;

	@Transactional
	public SyncStatusResponseDTO status(String username, MediaSource source, MediaType type) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

		SyncStatusResponseDTO response = new SyncStatusResponseDTO();
		syncManager.getStatus(user, source, type).ifPresent(v -> response.setCurrent(mapper.map(v)));
		syncManager.getStatus(user, source, type, SyncType.PULL).ifPresent(v -> response.setLastPull(mapper.map(v)));
		syncManager.getStatus(user, source, type, SyncType.PUSH).ifPresent(v -> response.setLastPush(mapper.map(v)));

		return response;
	}

	public void enqueue(UserPrincipal userPrincipal, MediaSource source, MediaType type, SyncType syncType) {
		syncManager.enqueueSync(userPrincipal.getId(), source, type, syncType);
	}
}
