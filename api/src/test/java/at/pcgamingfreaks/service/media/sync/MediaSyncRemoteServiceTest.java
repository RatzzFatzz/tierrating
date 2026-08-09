package at.pcgamingfreaks.service.media.sync;

import at.pcgamingfreaks.model.RemoteUpdateEntry;
import at.pcgamingfreaks.model.db.MediaSourceConnection;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.db.media.AniListMediaEntry;
import at.pcgamingfreaks.model.db.media.UserMediaEntryState;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaState;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.repo.AnilistMediaEntryRepository;
import at.pcgamingfreaks.model.repo.UserMediaEntryStateRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.media.MediaEntryRepositoryRegistry;
import at.pcgamingfreaks.service.media.remote.AniListAnimeClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaSyncRemoteServiceTest {

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
	private MediaSyncRemoteService underTest;

	private static Stream<Arguments> pushToRemote() {
		Long userId = 1L;
		User user = new User();
		user.setId(userId);
		return Stream.of(
				Arguments.of(
						"No updates / No dirty states",
						userId, user,
						List.of(),
						List.of(),
						List.of()
				),
				Arguments.of(
						"Exactly one update",
						userId, user,
						List.of(localStateEntry(1L, user, 1L, 10, MediaState.COMPLETED, true)),
						List.of(mediaEntry(1L)),
						List.of(new RemoteUpdateEntry(1L, 10, MediaState.COMPLETED))
				),
				Arguments.of(
						"No updates for type",
						userId, user,
						List.of(localStateEntry(1L, user, 1L, 10, MediaState.COMPLETED, true)),
						List.of(),
						List.of()
				),
				Arguments.of(
						"Exactly one update (mixed media types)",
						userId, user,
						List.of(
								localStateEntry(1L, user, 1L, 10, MediaState.COMPLETED, true),
								localStateEntry(2L, user, 2L, 9, MediaState.IN_PROGRESS, true)
						),
						List.of(mediaEntry(1L)),
						List.of(new RemoteUpdateEntry(1L, 10, MediaState.COMPLETED))
				)
		);
	}

	@MethodSource("pushToRemote")
	@ParameterizedTest(name = "{0}")
	void pushToRemote(String description, Long userId, User user,
	                  List<UserMediaEntryState> dirtyStates, List<AniListMediaEntry> localEntries,
	                  List<RemoteUpdateEntry> expectedRemoteUpdateEntries) {
		MediaSource source = MediaSource.ANILIST;

		when(userRepository.getReferenceById(userId)).thenReturn(user);
		doReturn(anilistMediaEntryRepository).when(mediaEntryRepositoryRegistry).getRepository(source);
		when(userMediaEntryStateRepository.findAllByUserAndSourceAndDirty(eq(user), eq(source), eq(true)))
				.thenReturn(dirtyStates);
		when(anilistMediaEntryRepository.findAllByIdInAndType(anyCollection(), any(MediaType.class)))
				.thenReturn(localEntries);

		underTest.pushToRemote(userId, source, MediaType.MANGA, new MediaSourceConnection(), aniListAnimeClient);

		verify(userRepository, times(1)).getReferenceById(userId);
		verify(mediaEntryRepositoryRegistry, times(1)).getRepository(source);
		verify(userMediaEntryStateRepository, times(1)).findAllByUserAndSourceAndDirty(eq(user), eq(source), eq(true));

		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<RemoteUpdateEntry>> pushCaptor = ArgumentCaptor.forClass(List.class);
		verify(aniListAnimeClient, times(1)).pushRemote(any(), pushCaptor.capture());
		assertThat(pushCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedRemoteUpdateEntries);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<UserMediaEntryState>> updateCaptor = ArgumentCaptor.forClass(List.class);
		verify(userMediaEntryStateRepository, times(1)).saveAll(updateCaptor.capture());
		assertThat(updateCaptor.getValue())
				.hasSize(expectedRemoteUpdateEntries.size())
				.extracting(UserMediaEntryState::isDirty)
				.allMatch(dirty -> !dirty);
	}

	private static AniListMediaEntry mediaEntry(Long entryId) {
		AniListMediaEntry mediaEntry = new AniListMediaEntry();
		mediaEntry.setId(entryId);
		return mediaEntry;
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