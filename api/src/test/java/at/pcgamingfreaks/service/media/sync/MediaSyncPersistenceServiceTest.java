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
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
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

	@Test
	void reconcile() {
		Long userId = 1L;
		User user = new User();
		user.setId(userId);

		MediaSource source = MediaSource.ANILIST;

		List<AniListMediaEntry> localEntries = List.of(
				mediaEntry(1L), mediaEntry(2L), mediaEntry(3L)
		);

		List<UserMediaEntryState> localStates = List.of(
				localStateEntry(1L, user,1L, 10, MediaState.COMPLETED),
				localStateEntry(2L, user,2L, 8, MediaState.IN_PROGRESS),
				localStateEntry(3L, user,3L, 6, MediaState.IN_PROGRESS)
		);

		List<RemoteSyncResult<AniListMediaEntry>> remoteEntries = List.of(
				remoteEntry(1L, 9, MediaState.COMPLETED),
				remoteEntry(2L, 8, MediaState.COMPLETED),
				remoteEntry(3L, 7, MediaState.COMPLETED),
				remoteEntry(4L, 5, MediaState.IN_PROGRESS)
		);

		List<AniListMediaEntry> expectedEntries = List.of(
				mediaEntry(4L)
		);

		List<UserMediaEntryState> expectedUpdatedEntryStates = List.of(
				localStateEntry(1L, user,1L, 9, MediaState.COMPLETED),
				localStateEntry(2L, user,2L, 8, MediaState.COMPLETED),
				localStateEntry(3L, user,3L, 7, MediaState.COMPLETED),
				localStateEntry(null, user,4L, 5, MediaState.IN_PROGRESS)
		);

		when(userRepository.getReferenceById(userId)).thenReturn(user);
		doReturn(anilistMediaEntryRepository).when(mediaEntryRepositoryRegistry).getRepository(source);
		when(anilistMediaEntryRepository.findAllByIdIn(anyCollection())).thenReturn(localEntries);
		when(userMediaEntryStateRepository.findAllByUserAndSource(eq(user), eq(source))).thenReturn(localStates);

		underTest.reconcile(userId, source, aniListAnimeClient, remoteEntries);

		verify(userRepository, times(1)).getReferenceById(userId);
		verify(mediaEntryRepositoryRegistry, times(1)).getRepository(source);
		verify(userMediaEntryStateRepository, times(1)).findAllByUserAndSource(user, source);
		verify(anilistMediaEntryRepository, times(1)).findAllByIdIn(Set.of(1L, 2L, 3L, 4L));

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

	private static RemoteSyncResult<AniListMediaEntry> remoteEntry(Long entryId, float score, MediaState state) {
		AniListMediaEntry mediaEntry = new AniListMediaEntry();
		mediaEntry.setId(entryId);
		return new RemoteSyncResult<>(mediaEntry, score, state);
	}

	private static UserMediaEntryState localStateEntry(Long id, User user, Long entryId, float score, MediaState state) {
		UserMediaEntryState entryState = new UserMediaEntryState();
		entryState.setId(id);
		entryState.setUser(user);
		entryState.setEntryId(entryId);
		entryState.setSource(MediaSource.ANILIST);
		entryState.setScore(score);
		entryState.setState(state);
		return entryState;
	}
}