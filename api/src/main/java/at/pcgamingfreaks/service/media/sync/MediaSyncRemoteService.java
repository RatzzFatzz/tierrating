package at.pcgamingfreaks.service.media.sync;

import at.pcgamingfreaks.model.RemoteUpdateEntry;
import at.pcgamingfreaks.model.db.MediaSourceConnection;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.db.media.MediaEntry;
import at.pcgamingfreaks.model.db.media.UserMediaEntryState;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.repo.MediaEntryRepository;
import at.pcgamingfreaks.model.repo.UserMediaEntryStateRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.media.MediaEntryRepositoryRegistry;
import at.pcgamingfreaks.service.media.remote.RemoteMediaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaSyncRemoteService {
	private final UserRepository userRepository;
	private final UserMediaEntryStateRepository userMediaEntryStateRepository;
	private final MediaEntryRepositoryRegistry mediaEntryRepositoryRegistry;

	public <E extends MediaEntry> void pushToRemote(Long userId, MediaSource source, MediaType type,
	                                                MediaSourceConnection connection, RemoteMediaClient<E> client) {
		User userProxy = userRepository.getReferenceById(userId);

		MediaEntryRepository<E> mediaEntryRepository = mediaEntryRepositoryRegistry.getRepository(source);

		Map<Long, UserMediaEntryState> dirtyStatesById = userMediaEntryStateRepository.findAllByUserAndSourceAndDirty(userProxy, source, true)
				.stream()
				.collect(Collectors.toMap(UserMediaEntryState::getEntryId, Function.identity()));

		Map<Long, E> localEntries = mediaEntryRepository.findAllByIdInAndType(dirtyStatesById.keySet(), type)
				.stream()
				.collect(Collectors.toMap(MediaEntry::getId, Function.identity()));

		List<UserMediaEntryState> dirtyStates = dirtyStatesById.values().stream()
				.filter(e -> localEntries.containsKey(e.getEntryId()))
				.toList();

		client.pushRemote(connection, dirtyStates.stream()
				.map(e -> new RemoteUpdateEntry(e.getEntryId(), e.getScore(), e.getState()))
				.toList());

		dirtyStates.forEach((state) -> state.setDirty(false));
		userMediaEntryStateRepository.saveAll(dirtyStates);

	}
}
