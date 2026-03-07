package at.pcgamingfreaks.service.tokenfreshing;

import at.pcgamingfreaks.config.ClientConfig;
import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.exceptions.ThirdPartyAuthenticationException;
import at.pcgamingfreaks.model.repo.UserRepository;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraktTokenRefresher Expiry Tests")
class TraktTokenRefresherExpiryTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ThirdPartyConfig thirdPartyConfig;

    private TraktTokenRefresher traktTokenRefresher;

    private User testUser;
    private ThirdPartyConnection connection;

    @BeforeEach
    void setUp() {
        traktTokenRefresher = new TraktTokenRefresher(userRepository, thirdPartyConfig);

        testUser = new User();
        testUser.setUsername("testuser");
        connection = new ThirdPartyConnection();
        connection.setService(ThirdPartyService.TRAKT);
        connection.setRefreshToken("refresh-token");
        connection.setExpiresOn(LocalDateTime.now().plusDays(1));
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.TRAKT, connection);
        testUser.setConnections(connections);
    }

    private ServiceConfig buildValidConfig() {
        ServiceConfig config = new ServiceConfig();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setKey("test-key");
        clientConfig.setSecret("test-secret");
        config.setClient(clientConfig);
        config.setUrl("https://redirect.example.com");
        return config;
    }

    @Test
    @DisplayName("refresh() should update tokens when token is expiring soon and response is successful")
    @SuppressWarnings("unchecked")
    void refresh_shouldUpdateTokensWhenExpiringSoon() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(buildValidConfig());

        AccessToken newToken = new AccessToken();
        newToken.access_token = "new-access-token";
        newToken.refresh_token = "new-refresh-token";
        newToken.expires_in = 7776000;

        Response<AccessToken> mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(newToken);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.refreshAccessToken(any())).thenReturn(mockResponse);
        })) {
            traktTokenRefresher.refresh(testUser);
            verify(userRepository).save(testUser);
            assertEquals("new-access-token", connection.getAccessToken());
            assertEquals("new-refresh-token", connection.getRefreshToken());
        }
    }

    @Test
    @DisplayName("refresh() should throw ThirdPartyAuthenticationException when response is unsuccessful")
    @SuppressWarnings("unchecked")
    void refresh_shouldThrowWhenResponseUnsuccessful() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(buildValidConfig());

        Response<AccessToken> mockResponse = mock(Response.class);
        when(mockResponse.isSuccessful()).thenReturn(false);

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.refreshAccessToken(any())).thenReturn(mockResponse);
        })) {
            assertThrows(ThirdPartyAuthenticationException.class,
                    () -> traktTokenRefresher.refresh(testUser));
        }
    }

    @Test
    @DisplayName("refresh() should throw ThirdPartyAuthenticationException on IOException")
    @SuppressWarnings("unchecked")
    void refresh_shouldThrowOnIOException() throws IOException {
        when(thirdPartyConfig.getTrakt()).thenReturn(buildValidConfig());

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.refreshAccessToken(any())).thenThrow(new IOException("Network error"));
        })) {
            assertThrows(ThirdPartyAuthenticationException.class,
                    () -> traktTokenRefresher.refresh(testUser));
        }
    }
}
