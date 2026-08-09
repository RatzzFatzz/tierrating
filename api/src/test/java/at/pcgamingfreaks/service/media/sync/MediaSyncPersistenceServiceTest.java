package at.pcgamingfreaks.service.media.sync;

import at.pcgamingfreaks.model.RemoteSyncResult;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.db.media.AniListMediaEntry;
import at.pcgamingfreaks.model.db.media.MediaEntry;
import at.pcgamingfreaks.model.db.media.UserMediaEntryState;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaState;
import at.pcgamingfreaks.model.repo.AnilistMediaEntryRepository;
import at.pcgamingfreaks.model.repo.MediaEntryRepository;
import at.pcgamingfreaks.model.repo.UserMediaEntryStateRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.media.MediaEntryRepositoryRegistry;
import at.pcgamingfreaks.service.media.remote.AniListAnimeClient;
import at.pcgamingfreaks.service.media.remote.RemoteMediaClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaSyncPersistenceServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMediaEntryStateRepository userMediaEntryStateRepository;

	@Mock
	private MediaEntryRepositoryRegistry mediaEntryRepositoryRegistry;

	@Mock
	private AnilistMediaEntryRepository anilistMediaEntryRepository;

	@Mock
	private AniListAnimeClient aniListAnimeClient;

	@InjectMocks
	private MediaSyncPersistenceService underTest;

	private static Stream<Arguments> reconcile() {
		Long userId = 1L;
		User user = new User();
		user.setId(userId);

		return Stream.of(
				Arguments.of(
						"Update nothing",
						userId, user,
						List.of(mediaEntry(1L)),
						List.of(localStateEntry(1L, user,1L, 10, MediaState.COMPLETED)),
						List.of(remoteEntry(1L, 10, MediaState.COMPLETED)),
						List.of(),
						List.of()
				),
				Arguments.of(
						"Update score",
						userId, user,
						List.of(mediaEntry(1L)),
						List.of(localStateEntry(1L, user,1L, 10, MediaState.COMPLETED)),
						List.of(remoteEntry(1L, 9, MediaState.COMPLETED)),
						List.of(),
						List.of(localStateEntry(1L, user,1L, 9, MediaState.COMPLETED))
				),
				Arguments.of(
						"Update state",
						userId, user,
						List.of(mediaEntry(1L)),
						List.of(localStateEntry(1L, user,1L, 10, MediaState.IN_PROGRESS)),
						List.of(remoteEntry(1L, 10, MediaState.COMPLETED)),
						List.of(),
						List.of(localStateEntry(1L, user,1L, 10, MediaState.COMPLETED))
				),
				Arguments.of(
						"Update score and state",
						userId, user,
						List.of(mediaEntry(1L)),
						List.of(localStateEntry(1L, user,1L, 10, MediaState.IN_PROGRESS)),
						List.of(remoteEntry(1L, 9, MediaState.COMPLETED)),
						List.of(),
						List.of(localStateEntry(1L, user,1L, 9, MediaState.COMPLETED))
				),
				Arguments.of(
						"Set dirty = false when score and state match",
						userId, user,
						List.of(mediaEntry(1L)),
						List.of(localStateEntry(1L, user,1L, 10, MediaState.COMPLETED, true)),
						List.of(remoteEntry(1L, 10, MediaState.COMPLETED)),
						List.of(),
						List.of(localStateEntry(1L, user,1L, 10, MediaState.COMPLETED, false))
				),
				Arguments.of(
						"Add new state",
						userId, user,
						List.of(mediaEntry(1L)),
						List.of(),
						List.of(remoteEntry(1L, 9, MediaState.COMPLETED)),
						List.of(),
						List.of(localStateEntry(null, user,1L, 9, MediaState.COMPLETED))
				),
				Arguments.of(
						"Add new entry and state",
						userId, user,
						List.of(),
						List.of(),
						List.of(remoteEntry(1L, 9, MediaState.COMPLETED)),
						List.of(mediaEntry(1L)),
						List.of(localStateEntry(null, user,1L, 9, MediaState.COMPLETED))
				),
				Arguments.of(
						"Update entry",
						userId, user,
						List.of(mediaEntry(1L, "old title")),
						List.of(localStateEntry(1L, user,1L, 9, MediaState.COMPLETED)),
						List.of(remoteEntry(1L, 9, MediaState.COMPLETED, "new title")),
						List.of(mediaEntry(1L, "new title")),
						List.of()
				)
		);
	}

	@MethodSource("reconcile")
	@ParameterizedTest(name = "{0}")
	void reconcile(String description, Long userId, User user, List<AniListMediaEntry> localEntries,
				   List<UserMediaEntryState> localStates, List<RemoteSyncResult<AniListMediaEntry>> remoteEntries,
	               List<AniListMediaEntry> expectedEntries, List<UserMediaEntryState> expectedUpdatedEntryStates) {
		MediaSource source = MediaSource.ANILIST;

		when(userRepository.getReferenceById(userId)).thenReturn(user);
		doReturn(anilistMediaEntryRepository).when(mediaEntryRepositoryRegistry).getRepository(source);
		when(anilistMediaEntryRepository.findAllByIdIn(anyCollection())).thenReturn(localEntries);
		when(userMediaEntryStateRepository.findAllByUserAndSource(eq(user), eq(source))).thenReturn(localStates);
		lenient().when(aniListAnimeClient.shouldOverwriteLocal(anyFloat(), anyFloat())).thenCallRealMethod();

		underTest.reconcile(userId, source, aniListAnimeClient, remoteEntries);

		verify(userRepository, times(1)).getReferenceById(userId);
		verify(mediaEntryRepositoryRegistry, times(1)).getRepository(source);
		verify(userMediaEntryStateRepository, times(1)).findAllByUserAndSource(user, source);
		verify(anilistMediaEntryRepository, times(1)).findAllByIdIn(remoteEntries.stream()
				.map(remoteEntry -> remoteEntry.entry().getId())
				.collect(Collectors.toSet()));

		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<AniListMediaEntry>> entryCaptor = ArgumentCaptor.forClass(List.class);
		verify(anilistMediaEntryRepository, times(1)).saveAll(entryCaptor.capture());
		List<AniListMediaEntry> actualEntries = entryCaptor.getValue();
		assertThat(actualEntries).usingRecursiveComparison().isEqualTo(expectedEntries);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<UserMediaEntryState>> stateCaptor = ArgumentCaptor.forClass(List.class);
		verify(userMediaEntryStateRepository, times(1)).saveAll(stateCaptor.capture());
		List<UserMediaEntryState> actualStates = stateCaptor.getValue();
		assertThat(actualStates).usingRecursiveComparison().isEqualTo(expectedUpdatedEntryStates);
	}

	private static AniListMediaEntry mediaEntry(Long entryId) {
		AniListMediaEntry mediaEntry = new AniListMediaEntry();
		mediaEntry.setId(entryId);
		return mediaEntry;
	}

	private static AniListMediaEntry mediaEntry(Long entryId, String title) {
		AniListMediaEntry mediaEntry = mediaEntry(entryId);
		mediaEntry.setTitle(title);
		return mediaEntry;
	}

	private static RemoteSyncResult<AniListMediaEntry> remoteEntry(Long entryId, float score, MediaState state) {
		AniListMediaEntry mediaEntry = new AniListMediaEntry();
		mediaEntry.setId(entryId);
		return new RemoteSyncResult<>(mediaEntry, score, state);
	}

	private static RemoteSyncResult<AniListMediaEntry> remoteEntry(Long entryId, float score, MediaState state, String title) {
		AniListMediaEntry mediaEntry = new AniListMediaEntry();
		mediaEntry.setId(entryId);
		mediaEntry.setTitle(title);
		return new RemoteSyncResult<>(mediaEntry, score, state);
	}

	private static UserMediaEntryState localStateEntry(Long id, User user, Long entryId, float score, MediaState state) {
		return localStateEntry(id, user, entryId, score, state, false);
	}

	private static UserMediaEntryState localStateEntry(Long id, User user, Long entryId, float score, MediaState state, boolean isDirty) {
		UserMediaEntryState entryState = new UserMediaEntryState();
		entryState.setId(id);
		entryState.setUser(user);
		entryState.setEntryId(entryId);
		entryState.setSource(MediaSource.ANILIST);
		entryState.setScore(score);
		entryState.setState(state);
		entryState.setDirty(isDirty);
		return entryState;
	}
}