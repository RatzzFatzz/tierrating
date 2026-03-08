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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraktTvShowSeasonsData Tests")
class TraktTvShowSeasonsDataTest {

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

    private TraktTvShowSeasonsData seasonsData;
    private User testUser;
    private ServiceConfig validTraktConfig;

    @BeforeEach
    void setUp() {
        seasonsData = new TraktTvShowSeasonsData(
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

    private RatedSeason buildRatedSeason(int seasonTraktId, int showTmdbId, int seasonNumber, String showTitle, Rating rating) {
        RatedSeason ratedSeason = new RatedSeason();
        ratedSeason.show = new Show();
        ratedSeason.show.ids = new ShowIds();
        ratedSeason.show.ids.tmdb = showTmdbId;
        ratedSeason.show.title = showTitle;
        ratedSeason.season = new Season();
        ratedSeason.season.ids = new SeasonIds();
        ratedSeason.season.ids.trakt = seasonTraktId;
        ratedSeason.season.number = seasonNumber;
        ratedSeason.rating = rating;
        return ratedSeason;
    }

    @Test
    @DisplayName("pullRated() should return rated seasons via TraktV2")
    @SuppressWarnings("unchecked")
    void pullRated_shouldReturnRatedSeasons() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        RatedSeason ratedSeason = buildRatedSeason(500, 200, 1, "Breaking Bad", Rating.TOTALLYNINJA);

        Response<List<RatedSeason>> mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(List.of(ratedSeason));

        Users mockUsers = mock(Users.class);
        Call<List<RatedSeason>> mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockUsers.ratingsSeasons(any(), any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            List<RatedSeason> result = seasonsData.pullRated(testUser);
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

        Response<List<RatedSeason>> mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);

        Users mockUsers = mock(Users.class);
        Call<List<RatedSeason>> mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockUsers.ratingsSeasons(any(), any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(ThirdPartySyncException.class, () -> seasonsData.pullRated(testUser));
        }
    }

    @Test
    @DisplayName("pullRated() should throw ThirdPartySyncException on IOException")
    @SuppressWarnings("unchecked")
    void pullRated_shouldThrowOnIOException() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        Users mockUsers = mock(Users.class);
        Call<List<RatedSeason>> mockCall = mock(Call.class);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));
        when(mockUsers.ratingsSeasons(any(), any(), any())).thenReturn(mockCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(ThirdPartySyncException.class, () -> seasonsData.pullRated(testUser));
        }
    }

    @Test
    @DisplayName("pull(User) should map rated seasons with correct title format")
    @SuppressWarnings("unchecked")
    void pull_shouldMapRatedSeasonsCorrectly() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
        when(coverFinder.findSeason(anyLong(), anyLong())).thenReturn(null);

        RatedSeason ratedSeason = buildRatedSeason(500, 200, 1, "Breaking Bad", Rating.SUPERB);

        Response<List<RatedSeason>> ratedResponse = mock(Response.class);
        when(ratedResponse.isSuccessful()).thenReturn(true);
        when(ratedResponse.body()).thenReturn(List.of(ratedSeason));

        Users mockUsers = mock(Users.class);
        Call<List<RatedSeason>> ratedCall = mock(Call.class);
        when(ratedCall.execute()).thenReturn(ratedResponse);
        when(mockUsers.ratingsSeasons(any(), any(), any())).thenReturn(ratedCall);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.users()).thenReturn(mockUsers);
        })) {
            List<TraktEntryScore> result = seasonsData.pull(testUser);
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Breaking Bad Season 1", result.get(0).getEntry().getTitle());
            assertEquals(9, result.get(0).getScore());
        }
    }

    @Test
    @DisplayName("pushSingleChange() should execute via RestClient")
    void pushSingleChange_shouldExecuteViaRestClient() {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        RestClient.Builder mockBuilder = mock(RestClient.Builder.class);
        RestClient mockRestClient = mock(RestClient.class);
        RestClient.RequestBodyUriSpec mockUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec mockBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(mockBuilder.baseUrl(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.defaultHeader(anyString(), anyString())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockRestClient);
        when(mockRestClient.post()).thenReturn(mockUriSpec);
        when(mockUriSpec.uri(anyString())).thenReturn(mockBodySpec);
        when(mockBodySpec.contentType(any())).thenReturn(mockBodySpec);
        when(mockBodySpec.header(anyString(), anyString())).thenReturn(mockBodySpec);
        when(mockBodySpec.body(anyString())).thenReturn(mockBodySpec);
        when(mockBodySpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(String.class)).thenReturn("{}");

        try (MockedStatic<RestClient> mockedRestClient = mockStatic(RestClient.class)) {
            mockedRestClient.when(RestClient::builder).thenReturn(mockBuilder);
            assertDoesNotThrow(() -> seasonsData.pushSingleChange(123L, 8.0f, testUser));
        }
    }
}
