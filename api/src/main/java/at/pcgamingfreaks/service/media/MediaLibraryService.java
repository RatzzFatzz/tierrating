package at.pcgamingfreaks.service.media;

import at.pcgamingfreaks.exceptions.UnknownMediaEntryException;
import at.pcgamingfreaks.mapper.mediaentry.MediaEntryMapper;
import at.pcgamingfreaks.mapper.mediaentry.MediaEntryMapperRegistry;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.db.media.MediaEntry;
import at.pcgamingfreaks.model.db.media.UserMediaEntryState;
import at.pcgamingfreaks.model.dto.MediaEntryDTO;
import at.pcgamingfreaks.model.dto.UpdateMediaEntryDTO;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.enums.SyncType;
import at.pcgamingfreaks.model.repo.MediaEntryRepository;
import at.pcgamingfreaks.model.repo.UserMediaEntryStateRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.media.remote.RemoteClientRegistry;
import at.pcgamingfreaks.service.media.sync.MediaSyncManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaLibraryService {

	private final UserRepository userRepository;
	private final UserMediaEntryStateRepository userMediaEntryStateRepository;
	private final MediaEntryRepositoryRegistry mediaEntryRepositoryRegistry;
	private final MediaEntryMapperRegistry mediaEntryMapperRegistry;
	private final MediaSyncManager mediaSyncManager;

	public <E extends MediaEntry> List<MediaEntryDTO> fetchLocal(String username, MediaSource source, MediaType type) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

		// TODO: restrict lookup to specific states
		Map<Long, UserMediaEntryState> userStatesByMediaEntryId = userMediaEntryStateRepository.findAllByUserAndSource(user, source)
				.stream()
				.collect(Collectors.toMap(UserMediaEntryState::getEntryId, Function.identity()));

		MediaEntryRepository<E> mediaEntryRepository = mediaEntryRepositoryRegistry.getRepository(source);
		List<E> mediaEntries = mediaEntryRepository.findAllByIdInAndType(userStatesByMediaEntryId.keySet(), type);

		MediaEntryMapper<E> mapper = mediaEntryMapperRegistry.getMapper(source);
		return mediaEntries.stream()
				.map(entry -> mapper.toDTO(entry, userStatesByMediaEntryId.get(entry.getId())))
				.sorted(Comparator.comparing(MediaEntryDTO::getScore).reversed())
				.toList();
	}

	@Transactional
	public void updateLocal(String username, MediaSource source, MediaType type, UpdateMediaEntryDTO request) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		Optional<UserMediaEntryState> existingUserState = userMediaEntryStateRepository.findByUserAndSourceAndEntryId(user, source, request.getId());
		UserMediaEntryState userState = existingUserState.orElse(new UserMediaEntryState());
		if (existingUserState.isEmpty()) {
			userState.setUser(user);
			userState.setSource(source);
			mediaEntryRepositoryRegistry.getRepository(source).findById(request.getId())
					.orElseThrow(() -> new UnknownMediaEntryException(request.getId()));
			userState.setEntryId(request.getId());
		}
		if (Objects.equals(userState.getScore(), request.getScore())
				&& Objects.equals(userState.getState(), request.getState())) return;

		userState.setScore(request.getScore());
		userState.setState(request.getState());
		userState.setDirty(true);
		userMediaEntryStateRepository.save(userState);

		if (user.getConnections().containsKey(source)
				&& user.getConnections().get(source).getMediaTypeSettings().containsKey(type)
				&& user.getConnections().get(source).getMediaTypeSettings().get(type).isAutoPush()) {
			mediaSyncManager.enqueueSync(user.getId(), source, type, SyncType.PUSH, 5);
		}
	}
}
