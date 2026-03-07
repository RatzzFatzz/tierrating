package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.config.ClientConfig;
import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.AuthTokenResponseDTO;
import at.pcgamingfreaks.model.dto.ThirdPartyAuthRequestDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyAuthenticationException;
import at.pcgamingfreaks.model.repo.ThirdPartyConnectionRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.util.JwtPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AniListAuthenticatorService Happy Path Tests")
class AniListAuthenticatorServiceHappyPathTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ThirdPartyConnectionRepository thirdPartyConnectionRepository;

    @Mock
    private ThirdPartyConfig thirdPartyConfig;

    @Mock
    private ObjectMapper objectMapper;

    private AniListAuthenticatorService service;
    private ServiceConfig validAnilistConfig;

    @BeforeEach
    void setUp() {
        service = new AniListAuthenticatorService(userRepository, thirdPartyConnectionRepository, objectMapper, thirdPartyConfig);

        validAnilistConfig = new ServiceConfig();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setKey("client-key");
        clientConfig.setSecret("client-secret");
        validAnilistConfig.setClient(clientConfig);
        validAnilistConfig.setUrl("https://redirect.example.com");
    }

    private String buildFakeJwt(long userId) {
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"HS256\"}".getBytes());
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(("{\"sub\":" + userId + "}").getBytes());
        return header + "." + payload + ".signature";
    }

    @Test
    @DisplayName("auth() should save connection on successful AniList auth")
    @SuppressWarnings("unchecked")
    void auth_shouldSaveConnectionOnSuccess() throws Exception {
        when(thirdPartyConfig.getAnilist()).thenReturn(validAnilistConfig);

        User user = new User();
        user.setConnections(new HashMap<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        AuthTokenResponseDTO tokenDTO = new AuthTokenResponseDTO();
        tokenDTO.setAccessToken(buildFakeJwt(12345L));
        tokenDTO.setRefreshToken("refresh-token");
        tokenDTO.setExpiresIn(7776000);

        ResponseEntity<AuthTokenResponseDTO> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.hasBody()).thenReturn(true);
        when(responseEntity.getBody()).thenReturn(tokenDTO);

        JwtPayload jwtPayload = new JwtPayload();
        jwtPayload.setUserId(12345L);
        when(objectMapper.readValue(any(byte[].class), eq(JwtPayload.class))).thenReturn(jwtPayload);

        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        try (MockedConstruction<RestTemplate> ignored = mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.exchange(any(String.class), any(), any(), eq(AuthTokenResponseDTO.class)))
                    .thenReturn(responseEntity);
        })) {
            assertDoesNotThrow(() -> service.auth("testuser", request));
        }

        verify(thirdPartyConnectionRepository).save(any());
    }

    @Test
    @DisplayName("auth() should throw ThirdPartyAuthenticationException when token response has no body")
    @SuppressWarnings("unchecked")
    void auth_shouldThrowWhenTokenResponseHasNoBody() {
        when(thirdPartyConfig.getAnilist()).thenReturn(validAnilistConfig);

        User user = new User();
        user.setConnections(new HashMap<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<AuthTokenResponseDTO> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.hasBody()).thenReturn(false);

        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        try (MockedConstruction<RestTemplate> ignored = mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.exchange(any(String.class), any(), any(), eq(AuthTokenResponseDTO.class)))
                    .thenReturn(responseEntity);
        })) {
            assertThrows(ThirdPartyAuthenticationException.class,
                    () -> service.auth("testuser", request));
        }
    }

    @Test
    @DisplayName("auth() should throw ThirdPartyAuthenticationException when token response body is null")
    @SuppressWarnings("unchecked")
    void auth_shouldThrowWhenTokenResponseBodyNull() {
        when(thirdPartyConfig.getAnilist()).thenReturn(validAnilistConfig);

        User user = new User();
        user.setConnections(new HashMap<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<AuthTokenResponseDTO> responseEntity = mock(ResponseEntity.class);
        when(responseEntity.hasBody()).thenReturn(true);
        when(responseEntity.getBody()).thenReturn(null);

        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        try (MockedConstruction<RestTemplate> ignored = mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.exchange(any(String.class), any(), any(), eq(AuthTokenResponseDTO.class)))
                    .thenReturn(responseEntity);
        })) {
            assertThrows(ThirdPartyAuthenticationException.class,
                    () -> service.auth("testuser", request));
        }
    }
}
