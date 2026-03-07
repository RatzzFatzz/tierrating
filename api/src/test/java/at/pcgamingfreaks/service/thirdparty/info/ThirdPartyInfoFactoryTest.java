package at.pcgamingfreaks.service.thirdparty.info;

import at.pcgamingfreaks.config.ClientConfig;
import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ThirdPartyInfoResponseDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
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
@DisplayName("ThirdPartyInfoFactory Tests")
class ThirdPartyInfoFactoryTest {

    @Mock
    private ThirdPartyConfig thirdPartyConfig;

    @Mock
    private TraktInfoService traktInfoService;

    @Mock
    private AniListInfoService aniListInfoService;

    @Test
    @DisplayName("getProvider() should return ANILIST service")
    void getProvider_shouldReturnAnilistService() {
        when(aniListInfoService.getService()).thenReturn(ThirdPartyService.ANILIST);
        ThirdPartyInfoFactory factory = new ThirdPartyInfoFactory(List.of(aniListInfoService));

        ThirdPartyInfoService result = factory.getProvider(ThirdPartyService.ANILIST);

        assertNotNull(result);
        assertEquals(aniListInfoService, result);
    }

    @Test
    @DisplayName("getProvider() should return TRAKT service")
    void getProvider_shouldReturnTraktService() {
        when(traktInfoService.getService()).thenReturn(ThirdPartyService.TRAKT);
        ThirdPartyInfoFactory factory = new ThirdPartyInfoFactory(List.of(traktInfoService));

        ThirdPartyInfoService result = factory.getProvider(ThirdPartyService.TRAKT);

        assertNotNull(result);
        assertEquals(traktInfoService, result);
    }

    @Test
    @DisplayName("getProvider() should throw IllegalArgumentException for unknown service")
    void getProvider_shouldThrowForUnknownService() {
        ThirdPartyInfoFactory factory = new ThirdPartyInfoFactory(List.of());

        assertThrows(IllegalArgumentException.class, () -> factory.getProvider(ThirdPartyService.ANILIST));
    }

    @Test
    @DisplayName("getProvider() should throw with service name in message")
    void getProvider_shouldThrowWithServiceNameInMessage() {
        ThirdPartyInfoFactory factory = new ThirdPartyInfoFactory(List.of());

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.getProvider(ThirdPartyService.TRAKT)
        );

        assertTrue(exception.getMessage().contains("TRAKT"));
    }

    @Nested
    @DisplayName("AniListInfoService Tests")
    class AniListInfoServiceTests {

        @Mock
        private ThirdPartyConfig config;

        @Mock
        private ServiceConfig serviceConfig;

        @Mock
        private ClientConfig clientConfig;

        @InjectMocks
        private AniListInfoService service;

        @Test
        @DisplayName("getService() should return ANILIST")
        void getService_shouldReturnAnilist() {
            assertEquals(ThirdPartyService.ANILIST, service.getService());
        }

        @Test
        @DisplayName("info() should return DTO with clientId when configured")
        void info_shouldReturnDtoWithClientId() {
            when(config.getAnilist()).thenReturn(serviceConfig);
            when(serviceConfig.isValid()).thenReturn(true);
            when(serviceConfig.getClient()).thenReturn(clientConfig);
            when(clientConfig.getKey()).thenReturn("test-client-id");

            ThirdPartyInfoResponseDTO result = service.info();

            assertNotNull(result);
            assertEquals("test-client-id", result.getClientId());
        }

        @Test
        @DisplayName("info() should throw ThirdPartyUnconfiguredException when not configured")
        void info_shouldThrowWhenNotConfigured() {
            when(config.getAnilist()).thenReturn(null);

            assertThrows(NullPointerException.class, () -> service.info());
        }

        @Test
        @DisplayName("info() should throw ThirdPartyUnconfiguredException when invalid")
        void info_shouldThrowWhenInvalid() {
            when(config.getAnilist()).thenReturn(serviceConfig);
            when(serviceConfig.isValid()).thenReturn(false);

            assertThrows(ThirdPartyUnconfiguredException.class, () -> service.info());
        }
    }

    @Nested
    @DisplayName("TraktInfoService Tests")
    class TraktInfoServiceTests {

        @Mock
        private ThirdPartyConfig config;

        @Mock
        private ServiceConfig serviceConfig;

        @Mock
        private ClientConfig clientConfig;

        @InjectMocks
        private TraktInfoService service;

        @Test
        @DisplayName("getService() should return TRAKT")
        void getService_shouldReturnTrakt() {
            assertEquals(ThirdPartyService.TRAKT, service.getService());
        }

        @Test
        @DisplayName("info() should return DTO with clientId when configured")
        void info_shouldReturnDtoWithClientId() {
            when(config.getTrakt()).thenReturn(serviceConfig);
            when(serviceConfig.isValid()).thenReturn(true);
            when(serviceConfig.getClient()).thenReturn(clientConfig);
            when(clientConfig.getKey()).thenReturn("trakt-client-id");

            ThirdPartyInfoResponseDTO result = service.info();

            assertNotNull(result);
            assertEquals("trakt-client-id", result.getClientId());
        }

        @Test
        @DisplayName("info() should throw ThirdPartyUnconfiguredException when not configured")
        void info_shouldThrowWhenNotConfigured() {
            when(config.getTrakt()).thenReturn(null);

            assertThrows(NullPointerException.class, () -> service.info());
        }

        @Test
        @DisplayName("info() should throw ThirdPartyUnconfiguredException when invalid")
        void info_shouldThrowWhenInvalid() {
            when(config.getTrakt()).thenReturn(serviceConfig);
            when(serviceConfig.isValid()).thenReturn(false);

            assertThrows(ThirdPartyUnconfiguredException.class, () -> service.info());
        }
    }
}
