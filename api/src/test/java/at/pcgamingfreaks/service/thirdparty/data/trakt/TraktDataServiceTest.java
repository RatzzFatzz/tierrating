package at.pcgamingfreaks.service.thirdparty.data.trakt;

import at.pcgamingfreaks.config.ClientConfig;
import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.model.exceptions.EntryNotFoundException;
import at.pcgamingfreaks.model.exceptions.ThirdPartySyncException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.TraktEntryRepository;
import at.pcgamingfreaks.model.repo.TraktEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntry;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import at.pcgamingfreaks.service.TmdbCoverFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraktDataService Tests")
class TraktDataServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TraktEntryScoreRepository entryScoreRepository;

    @Mock
    private TraktEntryRepository entryRepository;

    @Mock
    private TmdbCoverFinder coverFinder;

    @Mock
    private ThirdPartyConfig thirdPartyConfig;

    @Mock
    private ListEntryDtoMapper listEntryDtoMapper;

    private TraktMovieData movieService;
    private TraktTvShowData showService;
    private TraktTvShowSeasonsData seasonsService;

    private User testUser;
    private ServiceConfig validTraktConfig;

    @BeforeEach
    void setUp() {
        movieService = new TraktMovieData(
                userRepository, entryScoreRepository, entryRepository,
                coverFinder, thirdPartyConfig, listEntryDtoMapper);
        showService = new TraktTvShowData(
                userRepository, entryScoreRepository, entryRepository,
                coverFinder, thirdPartyConfig, listEntryDtoMapper);
        seasonsService = new TraktTvShowSeasonsData(
                userRepository, entryScoreRepository, entryRepository,
                coverFinder, thirdPartyConfig, listEntryDtoMapper);

        testUser = new User();
        testUser.setUsername("testuser");
        ThirdPartyConnection connection = new ThirdPartyConnection();
        connection.setService(ThirdPartyService.TRAKT);
        connection.setAccessToken("access-token");
        connection.setThirdPartyUserId("traktuser");
        connection.setAutoUpdateSync(true);
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.TRAKT, connection);
        testUser.setConnections(connections);

        validTraktConfig = new ServiceConfig();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setKey("client-key");
        clientConfig.setSecret("client-secret");
        validTraktConfig.setClient(clientConfig);
        validTraktConfig.setUrl("https://redirect.example.com");
    }

    @Test
    @DisplayName("getService() should return TRAKT")
    void getService_shouldReturnTrakt() {
        assertEquals(ThirdPartyService.TRAKT, movieService.getService());
        assertEquals(ThirdPartyService.TRAKT, showService.getService());
        assertEquals(ThirdPartyService.TRAKT, seasonsService.getService());
    }

    @Test
    @DisplayName("getContentType() should return correct type for each service")
    void getContentType_shouldReturnCorrectType() {
        assertEquals(ContentType.MOVIES, movieService.getContentType());
        assertEquals(ContentType.TVSHOWS, showService.getContentType());
        assertEquals(ContentType.TVSHOWS_SEASONS, seasonsService.getContentType());
    }

    @Test
    @DisplayName("fetch() should return existing scores when non-empty")
    void fetch_shouldReturnExistingScores() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        TraktEntry entry = new TraktEntry(1L, ContentType.MOVIES, null, "Movie", null);
        TraktEntryScore score = new TraktEntryScore();
        score.setEntry(entry);
        score.setScore(8);

        ListEntryDTO dto = mock(ListEntryDTO.class);
        when(entryScoreRepository.findAllByUserAndEntry_TypeOrderByScoreDesc(testUser, ContentType.MOVIES))
                .thenReturn(Set.of(score));
        when(listEntryDtoMapper.map(score)).thenReturn(dto);

        List<ListEntryDTO> result = movieService.fetch("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("fetch() should throw UsernameNotFoundException when user not found")
    void fetch_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> movieService.fetch("unknown"));
    }

    private ServiceConfig buildInvalidConfig() {
        ServiceConfig config = new ServiceConfig();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setKey("");
        clientConfig.setSecret("");
        config.setClient(clientConfig);
        return config;
    }

    @Test
    @DisplayName("pull() should throw ThirdPartySyncException when config is invalid")
    void pull_shouldThrowWhenConfigInvalid() {
        when(thirdPartyConfig.getTrakt()).thenReturn(buildInvalidConfig());

        assertThrows(ThirdPartySyncException.class, () -> movieService.pull("testuser"));
    }

    @Test
    @DisplayName("pull() should throw UsernameNotFoundException when user not found")
    void pull_shouldThrowWhenUserNotFound() {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> movieService.pull("unknown"));
    }

    @Test
    @DisplayName("update() should throw ThirdPartyUnconfiguredException when config is invalid")
    void update_shouldThrowWhenConfigInvalid() {
        when(thirdPartyConfig.getTrakt()).thenReturn(buildInvalidConfig());

        assertThrows(ThirdPartyUnconfiguredException.class,
                () -> movieService.update(1L, 8.0f, testUser));
    }

    @Test
    @DisplayName("update() should throw EntryNotFoundException when entry not found")
    void update_shouldThrowWhenEntryNotFound() {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
        when(entryScoreRepository.findByUserAndEntry_Id(testUser, 1L))
                .thenReturn(Optional.empty());

        assertThrows(EntryNotFoundException.class,
                () -> movieService.update(1L, 8.0f, testUser));
    }

    @Test
    @DisplayName("update() should save score and skip push when autoUpdateSync is false")
    void update_shouldSaveScoreAndSkipPushWhenAutoUpdateSyncFalse() {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
        testUser.getConnections().get(ThirdPartyService.TRAKT).setAutoUpdateSync(false);

        TraktEntry entry = new TraktEntry(1L, ContentType.MOVIES, null, "Movie", null);
        TraktEntryScore score = new TraktEntryScore();
        score.setEntry(entry);
        score.setScore(7);
        score.setUser(testUser);

        when(entryScoreRepository.findByUserAndEntry_Id(testUser, 1L))
                .thenReturn(Optional.of(score));
        when(entryScoreRepository.save(any())).thenReturn(score);

        movieService.update(1L, 9.0f, testUser);

        verify(entryScoreRepository).save(score);
        assertEquals(9, score.getScore());
    }

    @Test
    @DisplayName("push() should do nothing")
    void push_shouldDoNothing() {
        assertDoesNotThrow(() -> movieService.push("testuser"));
        verifyNoInteractions(userRepository);
    }

    @Nested
    @DisplayName("TraktMovieData spy tests - pull(User)")
    class TraktMovieDataSpyTests {

        @Test
        @DisplayName("pull(String) should aggregate rated and watched movies")
        void pull_shouldAggregateRatedAndWatchedMovies() {
            when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            TraktMovieData spyMovieService = spy(movieService);

            TraktEntry ratedEntry = new TraktEntry(100L, ContentType.MOVIES, null, "Rated Movie", null);
            TraktEntryScore ratedScore = new TraktEntryScore();
            ratedScore.setEntry(ratedEntry);
            ratedScore.setScore(8);
            ratedScore.setUser(testUser);

            TraktEntry watchedEntry = new TraktEntry(200L, ContentType.MOVIES, null, "Watched Movie", null);
            TraktEntryScore watchedScore = new TraktEntryScore();
            watchedScore.setEntry(watchedEntry);
            watchedScore.setScore(0);
            watchedScore.setUser(testUser);

            doReturn(List.of(ratedScore, watchedScore)).when(spyMovieService).pull(testUser);

            when(entryRepository.findAllByIdIn(any())).thenReturn(List.of());
            when(entryScoreRepository.findAllByUserAndEntryIdIn(any(), any())).thenReturn(List.of());
            when(entryScoreRepository.saveAll(any())).thenReturn(List.of());

            spyMovieService.pull("testuser");

            verify(entryScoreRepository).saveAll(any());
        }
    }

    @Nested
    @DisplayName("TraktTvShowData spy tests")
    class TraktTvShowDataSpyTests {

        @Test
        @DisplayName("pull(String) should sync TV shows")
        void pull_shouldSyncTvShows() {
            when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            TraktTvShowData spyShowService = spy(showService);

            TraktEntry showEntry = new TraktEntry(300L, ContentType.TVSHOWS, null, "Breaking Bad", null);
            TraktEntryScore showScore = new TraktEntryScore();
            showScore.setEntry(showEntry);
            showScore.setScore(10);
            showScore.setUser(testUser);

            doReturn(List.of(showScore)).when(spyShowService).pull(testUser);

            when(entryRepository.findAllByIdIn(any())).thenReturn(List.of());
            when(entryScoreRepository.findAllByUserAndEntryIdIn(any(), any())).thenReturn(List.of());
            when(entryScoreRepository.saveAll(any())).thenReturn(List.of());

            spyShowService.pull("testuser");

            verify(entryScoreRepository).saveAll(any());
        }
    }

    @Nested
    @DisplayName("TraktTvShowSeasonsData spy tests")
    class TraktTvShowSeasonsDataSpyTests {

        @Test
        @DisplayName("pull(String) should sync TV show seasons")
        void pull_shouldSyncTvShowSeasons() {
            when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            TraktTvShowSeasonsData spySeasonsService = spy(seasonsService);

            TraktEntry seasonEntry = new TraktEntry(400L, ContentType.TVSHOWS_SEASONS, 1, "Show Season 1", null);
            TraktEntryScore seasonScore = new TraktEntryScore();
            seasonScore.setEntry(seasonEntry);
            seasonScore.setScore(9);
            seasonScore.setUser(testUser);

            doReturn(List.of(seasonScore)).when(spySeasonsService).pull(testUser);

            when(entryRepository.findAllByIdIn(any())).thenReturn(List.of());
            when(entryScoreRepository.findAllByUserAndEntryIdIn(any(), any())).thenReturn(List.of());
            when(entryScoreRepository.saveAll(any())).thenReturn(List.of());

            spySeasonsService.pull("testuser");

            verify(entryScoreRepository).saveAll(any());
        }

        @Test
        @DisplayName("pullWatched() should return empty list")
        void pullWatched_shouldReturnEmptyList() {
            TraktTvShowSeasonsData spySeasonsService = spy(seasonsService);
            List<?> result = spySeasonsService.pullWatched(testUser);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Pull merge logic tests")
    class PullMergeLogicTests {

        @Test
        @DisplayName("pull(String) should merge with existing entries")
        void pull_shouldMergeWithExistingEntries() {
            when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            TraktMovieData spyMovieService = spy(movieService);

            TraktEntry remoteEntry = new TraktEntry(500L, ContentType.MOVIES, null, "Remote Movie", "cover.jpg");
            TraktEntryScore remoteScore = new TraktEntryScore();
            remoteScore.setEntry(remoteEntry);
            remoteScore.setScore(7);
            remoteScore.setUser(testUser);

            TraktEntry existingEntry = new TraktEntry(500L, ContentType.MOVIES, null, "Old Title", "old_cover.jpg");
            TraktEntryScore existingScore = new TraktEntryScore();
            existingScore.setEntry(existingEntry);
            existingScore.setScore(5);
            existingScore.setUser(testUser);

            doReturn(List.of(remoteScore)).when(spyMovieService).pull(testUser);

            when(entryRepository.findAllByIdIn(any())).thenReturn(List.of(existingEntry));
            when(entryScoreRepository.findAllByUserAndEntryIdIn(any(), any())).thenReturn(List.of(existingScore));
            when(entryScoreRepository.saveAll(any())).thenReturn(List.of());

            spyMovieService.pull("testuser");

            assertEquals("Remote Movie", existingEntry.getTitle());
            assertEquals(7, existingScore.getScore());
        }
    }
}
