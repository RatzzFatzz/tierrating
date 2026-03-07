package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ThirdPartyAuthRequestDTO;
import at.pcgamingfreaks.model.auth.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ThirdPartyAuthenticatorFactory Tests")
class ThirdPartyAuthenticatorFactoryTest {

    @Mock
    private ThirdPartyAuthenticatorService anilistAuthenticator;

    @Mock
    private ThirdPartyAuthenticatorService traktAuthenticator;

    @Test
    @DisplayName("getProvider() should return ANILIST authenticator")
    void getProvider_shouldReturnAnilistAuthenticator() {
        when(anilistAuthenticator.getService()).thenReturn(ThirdPartyService.ANILIST);
        
        ThirdPartyAuthenticatorFactory factory = new ThirdPartyAuthenticatorFactory(List.of(anilistAuthenticator));

        ThirdPartyAuthenticatorService result = factory.getProvider(ThirdPartyService.ANILIST);

        assertNotNull(result);
        assertEquals(anilistAuthenticator, result);
    }

    @Test
    @DisplayName("getProvider() should return TRAKT authenticator")
    void getProvider_shouldReturnTraktAuthenticator() {
        when(traktAuthenticator.getService()).thenReturn(ThirdPartyService.TRAKT);
        
        ThirdPartyAuthenticatorFactory factory = new ThirdPartyAuthenticatorFactory(List.of(traktAuthenticator));

        ThirdPartyAuthenticatorService result = factory.getProvider(ThirdPartyService.TRAKT);

        assertNotNull(result);
        assertEquals(traktAuthenticator, result);
    }

    @Test
    @DisplayName("getProvider() should handle both authenticators")
    void getProvider_shouldHandleBothAuthenticators() {
        when(anilistAuthenticator.getService()).thenReturn(ThirdPartyService.ANILIST);
        when(traktAuthenticator.getService()).thenReturn(ThirdPartyService.TRAKT);
        
        ThirdPartyAuthenticatorFactory factory = new ThirdPartyAuthenticatorFactory(List.of(anilistAuthenticator, traktAuthenticator));

        assertEquals(anilistAuthenticator, factory.getProvider(ThirdPartyService.ANILIST));
        assertEquals(traktAuthenticator, factory.getProvider(ThirdPartyService.TRAKT));
    }

    @Test
    @DisplayName("getProvider() should throw IllegalArgumentException for unknown service")
    void getProvider_shouldThrowForUnknownService() {
        ThirdPartyAuthenticatorFactory factory = new ThirdPartyAuthenticatorFactory(List.of());

        assertThrows(IllegalArgumentException.class, () -> factory.getProvider(ThirdPartyService.ANILIST));
    }

    @Test
    @DisplayName("getProvider() should throw with service name in message")
    void getProvider_shouldThrowWithServiceNameInMessage() {
        ThirdPartyAuthenticatorFactory factory = new ThirdPartyAuthenticatorFactory(List.of());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.getProvider(ThirdPartyService.TRAKT)
        );

        assertTrue(exception.getMessage().contains("TRAKT"));
    }

    @Nested
    @DisplayName("ThirdPartyAuthenticatorService Interface Tests")
    class AuthenticatorServiceTests {

        @Mock
        private ThirdPartyAuthenticatorService service;

        @Mock
        private User user;

        @Test
        @DisplayName("auth() should be callable")
        void auth_shouldBeCallable() {
            ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
            request.setCode("test-code");

            doNothing().when(service).auth(any(), any());

            service.auth("testuser", request);

            verify(service).auth("testuser", request);
        }

        @Test
        @DisplayName("getService() should return ThirdPartyService")
        void getService_shouldReturnThirdPartyService() {
            when(service.getService()).thenReturn(ThirdPartyService.ANILIST);

            assertEquals(ThirdPartyService.ANILIST, service.getService());
        }
    }
}
