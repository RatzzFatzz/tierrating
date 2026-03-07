package at.pcgamingfreaks.service.tokenfreshing;

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
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenRefreshService Tests")
class TokenRefreshServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TraktTokenRefresher traktTokenRefresher;

    @InjectMocks
    private TokenRefreshService tokenRefreshService;

    @Test
    @DisplayName("refreshTokens() should refresh trakt tokens for users with trakt connections")
    void refreshTokens_shouldRefreshTraktTokensForConnectedUsers() {
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        ThirdPartyConnection traktConnection = mock(ThirdPartyConnection.class);
        connections.put(ThirdPartyService.TRAKT, traktConnection);

        when(user1.getConnections()).thenReturn(connections);
        when(user2.getConnections()).thenReturn(new HashMap<>());
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(traktTokenRefresher.isValid()).thenReturn(true);

        tokenRefreshService.refreshTokens();

        verify(traktTokenRefresher).refresh(user1);
        verify(traktTokenRefresher, never()).refresh(user2);
    }

    @Test
    @DisplayName("refreshTokens() should skip users without trakt connection")
    void refreshTokens_shouldSkipUsersWithoutTraktConnection() {
        User user = mock(User.class);
        when(user.getConnections()).thenReturn(new HashMap<>());
        when(userRepository.findAll()).thenReturn(List.of(user));

        tokenRefreshService.refreshTokens();

        verify(traktTokenRefresher, never()).refresh(any());
    }

    @Test
    @DisplayName("refreshTokens() should skip refresh when trakt is not valid")
    void refreshTokens_shouldSkipWhenTraktNotValid() {
        User user = mock(User.class);
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        ThirdPartyConnection traktConnection = mock(ThirdPartyConnection.class);
        connections.put(ThirdPartyService.TRAKT, traktConnection);

        when(user.getConnections()).thenReturn(connections);
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(traktTokenRefresher.isValid()).thenReturn(false);

        tokenRefreshService.refreshTokens();

        verify(traktTokenRefresher, never()).refresh(any());
    }

    @Test
    @DisplayName("refreshTokens() should handle empty user list")
    void refreshTokens_shouldHandleEmptyUserList() {
        when(userRepository.findAll()).thenReturn(List.of());

        tokenRefreshService.refreshTokens();

        verify(traktTokenRefresher, never()).refresh(any());
    }

    @Test
    @DisplayName("refreshTokens() should handle null trakt connection in map")
    void refreshTokens_shouldHandleNullTraktConnection() {
        User user = mock(User.class);
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.TRAKT, null);

        when(user.getConnections()).thenReturn(connections);
        when(userRepository.findAll()).thenReturn(List.of(user));

        tokenRefreshService.refreshTokens();

        verify(traktTokenRefresher, never()).refresh(any());
    }
}
