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
import com.uwetrottmann.trakt5.services.Users;
import com.uwetrottmann.trakt5.services.Sync;
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
@DisplayName("TraktMovieData Tests")
class TraktMovieDataTest {

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

    private TraktMovieData movieData;
    private User testUser;
    private ServiceConfig validTraktConfig;

    @BeforeEach
    void setUp() {
        movieData = new TraktMovieData(
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
    @DisplayName("pullRated() should return rated movies via TraktV2")
    void pullRated_shouldReturnRatedMovies() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        RatedMovie ratedMovie = new RatedMovie();
        ratedMovie.movie = new Movie();
        ratedMovie.movie.ids = new MovieIds();
        ratedMovie.movie.ids.trakt = 100;
        ratedMovie.movie.ids.tmdb = 200;
        ratedMovie.movie.title = "Test Movie";
        ratedMovie.rating = Rating.TOTALLYNINJA;

        @SuppressWarnings("unchecked")
        Response<List<RatedMovie>> mockResponse = (Response<List<RatedMovie>>) mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(List.of(ratedMovie));

        Users mockUsers = mock(Users.class);
        retrofit2.Call<List<RatedMovie>> mockCall = mock(retrofit2.Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockUsers.ratingsMovies(any(), any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            List<RatedMovie> result = movieData.pullRated(testUser);
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Test Movie", result.get(0).movie.title);
        }
    }

    @Test
    @DisplayName("pullRated() should throw ThirdPartySyncException on unsuccessful response")
    void pullRated_shouldThrowOnUnsuccessfulResponse() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        @SuppressWarnings("unchecked")
        Response<List<RatedMovie>> mockResponse = (Response<List<RatedMovie>>) mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);

        Users mockUsers = mock(Users.class);
        retrofit2.Call<List<RatedMovie>> mockCall = mock(retrofit2.Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockUsers.ratingsMovies(any(), any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(ThirdPartySyncException.class, () -> movieData.pullRated(testUser));
        }
    }

    @Test
    @DisplayName("pullRated() should throw ThirdPartySyncException on IOException")
    void pullRated_shouldThrowOnIOException() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        Users mockUsers = mock(Users.class);
        retrofit2.Call<List<RatedMovie>> mockCall = mock(retrofit2.Call.class);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));
        when(mockUsers.ratingsMovies(any(), any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(ThirdPartySyncException.class, () -> movieData.pullRated(testUser));
        }
    }

    @Test
    @DisplayName("pullWatched() should return watched movies via TraktV2")
    void pullWatched_shouldReturnWatchedMovies() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        BaseMovie baseMovie = new BaseMovie();
        baseMovie.movie = new Movie();
        baseMovie.movie.ids = new MovieIds();
        baseMovie.movie.ids.trakt = 300;
        baseMovie.movie.ids.tmdb = 400;
        baseMovie.movie.title = "Watched Movie";

        @SuppressWarnings("unchecked")
        Response<List<BaseMovie>> mockResponse = (Response<List<BaseMovie>>) mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(List.of(baseMovie));

        Users mockUsers = mock(Users.class);
        retrofit2.Call<List<BaseMovie>> mockCall = mock(retrofit2.Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockUsers.watchedMovies(any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            List<BaseMovie> result = movieData.pullWatched(testUser);
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Test
    @DisplayName("pullWatched() should throw ThirdPartySyncException on unsuccessful response")
    void pullWatched_shouldThrowOnUnsuccessfulResponse() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        @SuppressWarnings("unchecked")
        Response<List<BaseMovie>> mockResponse = (Response<List<BaseMovie>>) mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);

        Users mockUsers = mock(Users.class);
        retrofit2.Call<List<BaseMovie>> mockCall = mock(retrofit2.Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockUsers.watchedMovies(any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(ThirdPartySyncException.class, () -> movieData.pullWatched(testUser));
        }
    }

    @Test
    @DisplayName("pullWatched() should throw ThirdPartySyncException on IOException")
    void pullWatched_shouldThrowOnIOException() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        Users mockUsers = mock(Users.class);
        retrofit2.Call<List<BaseMovie>> mockCall = mock(retrofit2.Call.class);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));
        when(mockUsers.watchedMovies(any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(ThirdPartySyncException.class, () -> movieData.pullWatched(testUser));
        }
    }

    @Test
    @DisplayName("pull(User) should aggregate rated and watched, rated takes precedence for same id")
    void pull_shouldAggregateRatedAndWatchedWithRatedTakingPrecedence() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
        when(coverFinder.findMovie(anyLong())).thenReturn(null);

        RatedMovie ratedMovie = new RatedMovie();
        ratedMovie.movie = new Movie();
        ratedMovie.movie.ids = new MovieIds();
        ratedMovie.movie.ids.trakt = 100;
        ratedMovie.movie.ids.tmdb = 200;
        ratedMovie.movie.title = "Rated Movie";
        ratedMovie.rating = Rating.GREAT;

        BaseMovie watchedSameId = new BaseMovie();
        watchedSameId.movie = new Movie();
        watchedSameId.movie.ids = new MovieIds();
        watchedSameId.movie.ids.trakt = 100;
        watchedSameId.movie.ids.tmdb = 200;
        watchedSameId.movie.title = "Rated Movie";

        BaseMovie watchedOther = new BaseMovie();
        watchedOther.movie = new Movie();
        watchedOther.movie.ids = new MovieIds();
        watchedOther.movie.ids.trakt = 999;
        watchedOther.movie.ids.tmdb = 888;
        watchedOther.movie.title = "Only Watched";

        @SuppressWarnings("unchecked")
        Response<List<RatedMovie>> ratedResponse = (Response<List<RatedMovie>>) mock(Response.class);
        when(ratedResponse.isSuccessful()).thenReturn(true);
        when(ratedResponse.body()).thenReturn(List.of(ratedMovie));

        @SuppressWarnings("unchecked")
        Response<List<BaseMovie>> watchedResponse = (Response<List<BaseMovie>>) mock(Response.class);
        when(watchedResponse.isSuccessful()).thenReturn(true);
        when(watchedResponse.body()).thenReturn(List.of(watchedSameId, watchedOther));

        Users mockUsers = mock(Users.class);

        retrofit2.Call<List<RatedMovie>> ratedCall = mock(retrofit2.Call.class);
        when(ratedCall.execute()).thenReturn(ratedResponse);
        when(mockUsers.ratingsMovies(any(), any(), any())).thenReturn(ratedCall);

        retrofit2.Call<List<BaseMovie>> watchedCall = mock(retrofit2.Call.class);
        when(watchedCall.execute()).thenReturn(watchedResponse);
        when(mockUsers.watchedMovies(any(), any())).thenReturn(watchedCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            List<TraktEntryScore> result = movieData.pull(testUser);
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
    void pushSingleChange_shouldExecute() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        com.uwetrottmann.trakt5.services.Sync mockSync = mock(com.uwetrottmann.trakt5.services.Sync.class);
        retrofit2.Call<SyncResponse> mockSyncCall = mock(retrofit2.Call.class);
        when(mockSync.addRatings(any())).thenReturn(mockSyncCall);
        when(mockSyncCall.execute()).thenReturn(mock(Response.class));

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.accessToken(any())).thenReturn(mock);
            when(mock.sync()).thenReturn(mockSync);
        })) {
            assertDoesNotThrow(() -> movieData.pushSingleChange(1L, 8.0f, testUser));
        }
    }

    @Test
    @DisplayName("pushSingleChange() should throw ThirdPartySyncException on IOException")
    void pushSingleChange_shouldThrowOnIOException() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        com.uwetrottmann.trakt5.services.Sync mockSync = mock(com.uwetrottmann.trakt5.services.Sync.class);
        retrofit2.Call<SyncResponse> mockSyncCall = mock(retrofit2.Call.class);
        when(mockSync.addRatings(any())).thenReturn(mockSyncCall);
        when(mockSyncCall.execute()).thenThrow(new IOException("Network error"));

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.accessToken(any())).thenReturn(mock);
            when(mock.sync()).thenReturn(mockSync);
        })) {
            assertThrows(ThirdPartySyncException.class,
                    () -> movieData.pushSingleChange(1L, 8.0f, testUser));
        }
    }
}
