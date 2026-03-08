package at.pcgamingfreaks.service.thirdparty.data.trakt;

import at.pcgamingfreaks.config.ClientConfig;
import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.exceptions.ThirdPartySyncException;
import at.pcgamingfreaks.model.repo.TraktEntryRepository;
import at.pcgamingfreaks.model.repo.TraktEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import at.pcgamingfreaks.service.TmdbCoverFinder;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.*;
import com.uwetrottmann.trakt5.enums.Rating;
import com.uwetrottmann.trakt5.services.Sync;
import com.uwetrottmann.trakt5.services.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraktTvShowData Tests")
class TraktTvShowDataTest {

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

    private TraktTvShowData showData;
    private User testUser;
    private ServiceConfig validTraktConfig;

    @BeforeEach
    void setUp() {
        showData = new TraktTvShowData(
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

    private RatedShow buildRatedShow(int traktId, int tmdbId, String title, Rating rating) {
        RatedShow ratedShow = new RatedShow();
        ratedShow.show = new Show();
        ratedShow.show.ids = new ShowIds();
        ratedShow.show.ids.trakt = traktId;
        ratedShow.show.ids.tmdb = tmdbId;
        ratedShow.show.title = title;
        ratedShow.rating = rating;
        return ratedShow;
    }

    private BaseShow buildBaseShow(int traktId, int tmdbId, String title) {
        BaseShow baseShow = new BaseShow();
        baseShow.show = new Show();
        baseShow.show.ids = new ShowIds();
        baseShow.show.ids.trakt = traktId;
        baseShow.show.ids.tmdb = tmdbId;
        baseShow.show.title = title;
        return baseShow;
    }

    @Test
    @DisplayName("pullRated() should return rated shows via TraktV2")
    @SuppressWarnings("unchecked")
    void pullRated_shouldReturnRatedShows() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        RatedShow ratedShow = buildRatedShow(100, 200, "Breaking Bad", Rating.TOTALLYNINJA);

        Response<List<RatedShow>> mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(List.of(ratedShow));

        Users mockUsers = mock(Users.class);
        Call<List<RatedShow>> mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockUsers.ratingsShows(any(), any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            List<RatedShow> result = showData.pullRated(testUser);
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Breaking Bad", result.get(0).show.title);
        }
    }

    @Test
    @DisplayName("pullRated() should throw ThirdPartySyncException on unsuccessful response")
    @SuppressWarnings("unchecked")
    void pullRated_shouldThrowOnUnsuccessfulResponse() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        Response<List<RatedShow>> mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);

        Users mockUsers = mock(Users.class);
        Call<List<RatedShow>> mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockUsers.ratingsShows(any(), any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(ThirdPartySyncException.class, () -> showData.pullRated(testUser));
        }
    }

    @Test
    @DisplayName("pullRated() should throw ThirdPartySyncException on IOException")
    @SuppressWarnings("unchecked")
    void pullRated_shouldThrowOnIOException() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        Users mockUsers = mock(Users.class);
        Call<List<RatedShow>> mockCall = mock(Call.class);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));
        when(mockUsers.ratingsShows(any(), any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(ThirdPartySyncException.class, () -> showData.pullRated(testUser));
        }
    }

    @Test
    @DisplayName("pullWatched() should return watched shows via TraktV2")
    @SuppressWarnings("unchecked")
    void pullWatched_shouldReturnWatchedShows() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        BaseShow baseShow = buildBaseShow(300, 400, "The Wire");

        Response<List<BaseShow>> mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(List.of(baseShow));

        Users mockUsers = mock(Users.class);
        Call<List<BaseShow>> mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockUsers.watchedShows(any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            List<BaseShow> result = showData.pullWatched(testUser);
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Test
    @DisplayName("pullWatched() should throw ThirdPartySyncException on unsuccessful response")
    @SuppressWarnings("unchecked")
    void pullWatched_shouldThrowOnUnsuccessfulResponse() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        Response<List<BaseShow>> mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);

        Users mockUsers = mock(Users.class);
        Call<List<BaseShow>> mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockUsers.watchedShows(any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(ThirdPartySyncException.class, () -> showData.pullWatched(testUser));
        }
    }

    @Test
    @DisplayName("pullWatched() should throw ThirdPartySyncException on IOException")
    @SuppressWarnings("unchecked")
    void pullWatched_shouldThrowOnIOException() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        Users mockUsers = mock(Users.class);
        Call<List<BaseShow>> mockCall = mock(Call.class);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));
        when(mockUsers.watchedShows(any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(ThirdPartySyncException.class, () -> showData.pullWatched(testUser));
        }
    }

    @Test
    @DisplayName("pull(User) should aggregate rated and watched shows; rated takes precedence for same id")
    @SuppressWarnings("unchecked")
    void pull_shouldAggregateRatedAndWatched() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
        when(coverFinder.findShow(anyLong())).thenReturn(null);

        RatedShow rated = buildRatedShow(100, 200, "The Office", Rating.GREAT);
        BaseShow watchedSameId = buildBaseShow(100, 200, "The Office");
        BaseShow watchedOther = buildBaseShow(999, 888, "Only Watched Show");

        Response<List<RatedShow>> ratedResponse = mock(Response.class);
        when(ratedResponse.isSuccessful()).thenReturn(true);
        when(ratedResponse.body()).thenReturn(List.of(rated));

        Response<List<BaseShow>> watchedResponse = mock(Response.class);
        when(watchedResponse.isSuccessful()).thenReturn(true);
        when(watchedResponse.body()).thenReturn(List.of(watchedSameId, watchedOther));

        Users mockUsers = mock(Users.class);

        Call<List<RatedShow>> ratedCall = mock(Call.class);
        when(ratedCall.execute()).thenReturn(ratedResponse);
        when(mockUsers.ratingsShows(any(), any(), any())).thenReturn(ratedCall);

        Call<List<BaseShow>> watchedCall = mock(Call.class);
        when(watchedCall.execute()).thenReturn(watchedResponse);
        when(mockUsers.watchedShows(any(), any())).thenReturn(watchedCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            List<TraktEntryScore> result = showData.pull(testUser);
            assertNotNull(result);
            assertEquals(2, result.size());
            TraktEntryScore ratedResult = result.stream()
                    .filter(s -> s.getEntry().getId() == 100L)
                    .findFirst().orElse(null);
            assertNotNull(ratedResult);
            assertEquals(8, ratedResult.getScore());
        }
    }

    @Test
    @DisplayName("pushSingleChange() should execute via TraktV2")
    @SuppressWarnings("unchecked")
    void pushSingleChange_shouldExecute() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        Sync mockSync = mock(Sync.class);
        Call<SyncResponse> mockSyncCall = mock(Call.class);
        when(mockSync.addRatings(any())).thenReturn(mockSyncCall);
        when(mockSyncCall.execute()).thenReturn(mock(Response.class));

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.accessToken(any())).thenReturn(mock);
            when(mock.sync()).thenReturn(mockSync);
        })) {
            assertDoesNotThrow(() -> showData.pushSingleChange(1L, 8.0f, testUser));
        }
    }

    @Test
    @DisplayName("pushSingleChange() should throw ThirdPartySyncException on IOException")
    @SuppressWarnings("unchecked")
    void pushSingleChange_shouldThrowOnIOException() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        Sync mockSync = mock(Sync.class);
        Call<SyncResponse> mockSyncCall = mock(Call.class);
        when(mockSync.addRatings(any())).thenReturn(mockSyncCall);
        when(mockSyncCall.execute()).thenThrow(new IOException("Network error"));

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.accessToken(any())).thenReturn(mock);
            when(mock.sync()).thenReturn(mockSync);
        })) {
            assertThrows(ThirdPartySyncException.class,
                    () -> showData.pushSingleChange(1L, 8.0f, testUser));
        }
    }
}
