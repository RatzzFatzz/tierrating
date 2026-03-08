package at.pcgamingfreaks.service.tokenfreshing;

import at.pcgamingfreaks.config.ClientConfig;
import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraktTokenRefresher Tests")
class TraktTokenRefresherTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ThirdPartyConfig thirdPartyConfig;

    @InjectMocks
    private TraktTokenRefresher traktTokenRefresher;

    private ServiceConfig buildValidConfig() {
        ServiceConfig config = new ServiceConfig();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setKey("test-key");
        clientConfig.setSecret("test-secret");
        config.setClient(clientConfig);
        config.setUrl("https://redirect.example.com");
        return config;
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
    @DisplayName("isValid() should return true when trakt config is valid")
    void isValid_shouldReturnTrueWhenConfigIsValid() {
        when(thirdPartyConfig.getTrakt()).thenReturn(buildValidConfig());
        assertTrue(traktTokenRefresher.isValid());
    }

    @Test
    @DisplayName("isValid() should return false when trakt config is invalid")
    void isValid_shouldReturnFalseWhenConfigIsInvalid() {
        when(thirdPartyConfig.getTrakt()).thenReturn(buildInvalidConfig());
        assertFalse(traktTokenRefresher.isValid());
    }

    @Test
    @DisplayName("refresh() should not refresh when token is not expiring soon")
    void refresh_shouldNotRefreshWhenTokenNotExpiringSoon() {
        User user = new User();
        ThirdPartyConnection connection = new ThirdPartyConnection();
        connection.setExpiresOn(LocalDateTime.now().plusDays(10));
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.TRAKT, connection);
        user.setConnections(connections);

        traktTokenRefresher.refresh(user);

        verify(userRepository, never()).save(any());
    }
}
