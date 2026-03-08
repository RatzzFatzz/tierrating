package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.config.ClientConfig;
import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ThirdPartyAuthRequestDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyAuthenticationException;
import at.pcgamingfreaks.model.repo.ThirdPartyConnectionRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.AccessToken;
import com.uwetrottmann.trakt5.enums.Extended;
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

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraktAuthenticatorService Happy Path Tests")
class TraktAuthenticatorServiceHappyPathTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ThirdPartyConnectionRepository thirdPartyConnectionRepository;

    @Mock
    private ThirdPartyConfig thirdPartyConfig;

    private TraktAuthenticatorService service;
    private ServiceConfig validTraktConfig;

    @BeforeEach
    void setUp() {
        service = new TraktAuthenticatorService(userRepository, thirdPartyConnectionRepository, thirdPartyConfig);

        validTraktConfig = new ServiceConfig();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setKey("client-key");
        clientConfig.setSecret("client-secret");
        validTraktConfig.setClient(clientConfig);
        validTraktConfig.setUrl("https://redirect.example.com");
    }

    @Test
    @DisplayName("auth() should save connection on successful Trakt auth")
    @SuppressWarnings("unchecked")
    void auth_shouldSaveConnectionOnSuccess() throws Exception {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        User user = new User();
        user.setConnections(new HashMap<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        AccessToken token = new AccessToken();
        token.access_token = "access-token";
        token.refresh_token = "refresh-token";
        token.expires_in = 7776000;

        Response<AccessToken> tokenResponse = mock(Response.class);
        when(tokenResponse.isSuccessful()).thenReturn(true);
        when(tokenResponse.body()).thenReturn(token);

        com.uwetrottmann.trakt5.entities.User traktUser = new com.uwetrottmann.trakt5.entities.User();
        traktUser.ids = new com.uwetrottmann.trakt5.entities.User.UserIds();
        traktUser.ids.slug = "traktslug";

        Response<com.uwetrottmann.trakt5.entities.User> profileResponse = mock(Response.class);
        when(profileResponse.isSuccessful()).thenReturn(true);
        when(profileResponse.body()).thenReturn(traktUser);

        Call<com.uwetrottmann.trakt5.entities.User> profileCall = mock(Call.class);
        when(profileCall.execute()).thenReturn(profileResponse);

        Users mockUsers = mock(Users.class);
        when(mockUsers.profile(any(), any(Extended.class))).thenReturn(profileCall);

        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.exchangeCodeForAccessToken(any())).thenReturn(tokenResponse);
            when(mock.accessToken(any())).thenReturn(mock);
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertDoesNotThrow(() -> service.auth("testuser", request));
        }

        verify(thirdPartyConnectionRepository).save(any());
    }

    @Test
    @DisplayName("auth() should throw ThirdPartyAuthenticationException when token response is unsuccessful")
    @SuppressWarnings("unchecked")
    void auth_shouldThrowWhenTokenResponseUnsuccessful() throws Exception {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        User user = new User();
        user.setConnections(new HashMap<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Response<AccessToken> tokenResponse = mock(Response.class);
        when(tokenResponse.isSuccessful()).thenReturn(false);

        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("bad-code");

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.exchangeCodeForAccessToken(any())).thenReturn(tokenResponse);
        })) {
            assertThrows(at.pcgamingfreaks.model.exceptions.ThirdPartyAuthenticationException.class,
                    () -> service.auth("testuser", request));
        }
    }

    @Test
    @DisplayName("auth() should throw ThirdPartyAuthenticationException when profile response is unsuccessful")
    @SuppressWarnings("unchecked")
    void auth_shouldThrowWhenProfileResponseUnsuccessful() throws Exception {
        when(thirdPartyConfig.getTrakt()).thenReturn(validTraktConfig);

        User user = new User();
        user.setConnections(new HashMap<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        AccessToken token = new AccessToken();
        token.access_token = "access-token";
        token.refresh_token = "refresh-token";
        token.expires_in = 7776000;

        Response<AccessToken> tokenResponse = mock(Response.class);
        when(tokenResponse.isSuccessful()).thenReturn(true);
        when(tokenResponse.body()).thenReturn(token);

        Response<com.uwetrottmann.trakt5.entities.User> profileResponse = mock(Response.class);
        when(profileResponse.isSuccessful()).thenReturn(false);

        Call<com.uwetrottmann.trakt5.entities.User> profileCall = mock(Call.class);
        when(profileCall.execute()).thenReturn(profileResponse);

        Users mockUsers = mock(Users.class);
        when(mockUsers.profile(any(), any(Extended.class))).thenReturn(profileCall);

        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        try (MockedConstruction<TraktV2> ignored = mockConstruction(TraktV2.class, (mock, context) -> {
            when(mock.exchangeCodeForAccessToken(any())).thenReturn(tokenResponse);
            when(mock.accessToken(any())).thenReturn(mock);
            when(mock.users()).thenReturn(mockUsers);
        })) {
            assertThrows(at.pcgamingfreaks.model.exceptions.ThirdPartyAuthenticationException.class,
                    () -> service.auth("testuser", request));
        }
    }
}
