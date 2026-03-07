package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.config.ClientConfig;
import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ThirdPartyAuthRequestDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyAuthenticationException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.ThirdPartyConnectionRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraktAuthenticatorService Tests")
class TraktAuthenticatorServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ThirdPartyConnectionRepository thirdPartyConnectionRepository;

    @Mock
    private ThirdPartyConfig thirdPartyConfig;

    @InjectMocks
    private TraktAuthenticatorService service;

    private ServiceConfig validTraktConfig;

    @BeforeEach
    void setUp() {
        validTraktConfig = new ServiceConfig();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setKey("client-key");
        clientConfig.setSecret("client-secret");
        validTraktConfig.setClient(clientConfig);
        validTraktConfig.setUrl("https://redirect.example.com");
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
    @DisplayName("getService() should return TRAKT")
    void getService_shouldReturnTrakt() {
        assertEquals(ThirdPartyService.TRAKT, service.getService());
    }

    @Test
    @DisplayName("auth() should throw ThirdPartyUnconfiguredException when config is invalid")
    void auth_shouldThrowWhenConfigInvalid() {
        when(thirdPartyConfig.getTrakt()).thenReturn(buildInvalidConfig());

        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        assertThrows(ThirdPartyUnconfiguredException.class,
                () -> service.auth("testuser", request));
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    @DisplayName("auth() should throw UsernameNotFoundException when user not found")
    void auth_shouldThrowWhenUserNotFound() {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        assertThrows(UsernameNotFoundException.class,
                () -> service.auth("testuser", request));
    }

    @Test
    @DisplayName("auth() should throw ThirdPartyAuthenticationException when already authenticated")
    void auth_shouldThrowWhenAlreadyAuthenticated() {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        User user = new User();
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.TRAKT, new ThirdPartyConnection());
        user.setConnections(connections);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        assertThrows(ThirdPartyAuthenticationException.class,
                () -> service.auth("testuser", request));
    }

    @Test
    @DisplayName("auth() should throw ThirdPartyAuthenticationException on Trakt API error")
    void auth_shouldThrowOnTraktApiError() {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        User user = new User();
        user.setConnections(new HashMap<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        assertThrows(ThirdPartyAuthenticationException.class,
                () -> service.auth("testuser", request));
    }
}
