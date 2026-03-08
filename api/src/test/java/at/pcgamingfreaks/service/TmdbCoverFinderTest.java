package at.pcgamingfreaks.service;

import at.pcgamingfreaks.model.thirdparty.TmdbCoverCache;
import at.pcgamingfreaks.model.repo.TmdbCoverCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TmdbCoverFinder Tests")
class TmdbCoverFinderTest {

    @Mock
    private TmdbCoverCacheRepository tmdbCoverCacheRepository;

    private TmdbCoverFinder tmdbCoverFinder;

    @BeforeEach
    void setUp() {
        tmdbCoverFinder = new TmdbCoverFinder("test-api-key", tmdbCoverCacheRepository);
    }

    @Test
    @DisplayName("findMovie() should return cached cover URL when cache hit")
    void findMovie_shouldReturnCachedUrlWhenCacheHit() {
        TmdbCoverCache cache = new TmdbCoverCache();
        cache.setId(123L);
        cache.setCoverUrl("https://image.tmdb.org/t/p/w185/poster.jpg");
        when(tmdbCoverCacheRepository.findByIdAndSeason(123L, null)).thenReturn(Optional.of(cache));

        String result = tmdbCoverFinder.findMovie(123L);

        assertEquals("https://image.tmdb.org/t/p/w185/poster.jpg", result);
        verify(tmdbCoverCacheRepository).findByIdAndSeason(123L, null);
    }

    @Test
    @DisplayName("findShow() should return cached cover URL when cache hit")
    void findShow_shouldReturnCachedUrlWhenCacheHit() {
        TmdbCoverCache cache = new TmdbCoverCache();
        cache.setId(456L);
        cache.setCoverUrl("https://image.tmdb.org/t/p/w185/show.jpg");
        when(tmdbCoverCacheRepository.findByIdAndSeason(456L, null)).thenReturn(Optional.of(cache));

        String result = tmdbCoverFinder.findShow(456L);

        assertEquals("https://image.tmdb.org/t/p/w185/show.jpg", result);
    }

    @Test
    @DisplayName("findSeason() should return cached season cover when cache hit")
    void findSeason_shouldReturnCachedSeasonCover() {
        TmdbCoverCache cache = new TmdbCoverCache();
        cache.setId(789L);
        cache.setSeason(2L);
        cache.setCoverUrl("https://image.tmdb.org/t/p/w185/season2.jpg");
        when(tmdbCoverCacheRepository.findByIdAndSeason(789L, 2L)).thenReturn(Optional.of(cache));

        String result = tmdbCoverFinder.findSeason(789L, 2L);

        assertEquals("https://image.tmdb.org/t/p/w185/season2.jpg", result);
    }

    @Test
    @DisplayName("findSeason() should fall back to show cover when season not found")
    void findSeason_shouldFallBackToShowWhenSeasonNotFound() {
        TmdbCoverCache showCache = new TmdbCoverCache();
        showCache.setId(789L);
        showCache.setCoverUrl("https://image.tmdb.org/t/p/w185/show.jpg");
        when(tmdbCoverCacheRepository.findByIdAndSeason(789L, 2L)).thenReturn(Optional.empty());
        when(tmdbCoverCacheRepository.findByIdAndSeason(789L, null)).thenReturn(Optional.of(showCache));

        String result = tmdbCoverFinder.findSeason(789L, 2L);

        assertEquals("https://image.tmdb.org/t/p/w185/show.jpg", result);
    }

    @Test
    @DisplayName("findMovie() should return null and log warning when API call fails")
    void findMovie_shouldReturnNullWhenApiCallFails() {
        when(tmdbCoverCacheRepository.findByIdAndSeason(anyLong(), any())).thenReturn(Optional.empty());

        try (MockedStatic<RestClient> mockedRestClient = mockStatic(RestClient.class)) {
            Builder mockBuilder = mock(Builder.class);
            mockedRestClient.when(RestClient::builder).thenReturn(mockBuilder);
            when(mockBuilder.baseUrl(anyString())).thenReturn(mockBuilder);
            when(mockBuilder.defaultHeader(anyString(), anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenThrow(new RuntimeException("Connection refused"));

            String result = tmdbCoverFinder.findMovie(999L);

            assertNull(result);
        }
    }

    @Test
    @DisplayName("findMovie() should save and return cover URL when API call succeeds")
    void findMovie_shouldSaveAndReturnCoverUrlWhenApiSucceeds() {
        when(tmdbCoverCacheRepository.findByIdAndSeason(100L, null)).thenReturn(Optional.empty());

        try (MockedStatic<RestClient> mockedRestClient = mockStatic(RestClient.class)) {
            Builder mockBuilder = mock(Builder.class);
            RestClient mockClient = mock(RestClient.class);
            RequestHeadersUriSpec mockUriSpec = mock(RequestHeadersUriSpec.class);
            ResponseSpec mockResponseSpec = mock(ResponseSpec.class);

            mockedRestClient.when(RestClient::builder).thenReturn(mockBuilder);
            when(mockBuilder.baseUrl(anyString())).thenReturn(mockBuilder);
            when(mockBuilder.defaultHeader(anyString(), anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockClient);
            when(mockClient.get()).thenReturn(mockUriSpec);
            when(mockUriSpec.uri(anyString())).thenReturn(mockUriSpec);
            when(mockUriSpec.header(anyString(), anyString())).thenReturn(mockUriSpec);

            at.pcgamingfreaks.model.dto.TmdbInfoRequest tmdbResponse = mock(at.pcgamingfreaks.model.dto.TmdbInfoRequest.class);
            when(tmdbResponse.getPosterPath()).thenReturn("/poster.jpg");
            when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec);
            when(mockResponseSpec.body(at.pcgamingfreaks.model.dto.TmdbInfoRequest.class)).thenReturn(tmdbResponse);

            ArgumentCaptor<TmdbCoverCache> cacheCaptor = ArgumentCaptor.forClass(TmdbCoverCache.class);
            when(tmdbCoverCacheRepository.save(cacheCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

            String result = tmdbCoverFinder.findMovie(100L);

            assertNotNull(result);
            assertTrue(result.endsWith("/poster.jpg"));
            TmdbCoverCache savedCache = cacheCaptor.getValue();
            assertEquals(100L, savedCache.getId());
        }
    }

    @Test
    @DisplayName("findMovie() should return null when poster path is null")
    void findMovie_shouldReturnNullWhenPosterPathIsNull() {
        when(tmdbCoverCacheRepository.findByIdAndSeason(200L, null)).thenReturn(Optional.empty());

        try (MockedStatic<RestClient> mockedRestClient = mockStatic(RestClient.class)) {
            Builder mockBuilder = mock(Builder.class);
            RestClient mockClient = mock(RestClient.class);
            RequestHeadersUriSpec mockUriSpec = mock(RequestHeadersUriSpec.class);
            ResponseSpec mockResponseSpec = mock(ResponseSpec.class);

            mockedRestClient.when(RestClient::builder).thenReturn(mockBuilder);
            when(mockBuilder.baseUrl(anyString())).thenReturn(mockBuilder);
            when(mockBuilder.defaultHeader(anyString(), anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockClient);
            when(mockClient.get()).thenReturn(mockUriSpec);
            when(mockUriSpec.uri(anyString())).thenReturn(mockUriSpec);
            when(mockUriSpec.header(anyString(), anyString())).thenReturn(mockUriSpec);

            at.pcgamingfreaks.model.dto.TmdbInfoRequest tmdbResponse = mock(at.pcgamingfreaks.model.dto.TmdbInfoRequest.class);
            when(tmdbResponse.getPosterPath()).thenReturn(null);
            when(mockUriSpec.retrieve()).thenReturn(mockResponseSpec);
            when(mockResponseSpec.body(at.pcgamingfreaks.model.dto.TmdbInfoRequest.class)).thenReturn(tmdbResponse);

            String result = tmdbCoverFinder.findMovie(200L);

            assertNull(result);
        }
    }

    @Test
    @DisplayName("findSeason() with season 0 should treat as no season")
    void findSeason_withSeasonZeroShouldTreatAsNoSeason() {
        TmdbCoverCache cache = new TmdbCoverCache();
        cache.setId(300L);
        cache.setCoverUrl("https://image.tmdb.org/t/p/w185/show.jpg");
        when(tmdbCoverCacheRepository.findByIdAndSeason(300L, null)).thenReturn(Optional.of(cache));

        String result = tmdbCoverFinder.findSeason(300L, 0L);

        assertEquals("https://image.tmdb.org/t/p/w185/show.jpg", result);
    }
}
