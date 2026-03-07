package at.pcgamingfreaks.service.thirdparty.data;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataFactory Tests")
class DataFactoryTest {

    @Mock
    private DataService anilistAnimeService;

    @Mock
    private DataService anilistMangaService;

    @Mock
    private DataService traktMovieService;

    @Mock
    private DataService traktTvShowService;

    private DataFactory dataFactory;

    @BeforeEach
    void setUp() {
        when(anilistAnimeService.getService()).thenReturn(ThirdPartyService.ANILIST);
        when(anilistAnimeService.getContentType()).thenReturn(ContentType.ANIME);

        when(anilistMangaService.getService()).thenReturn(ThirdPartyService.ANILIST);
        when(anilistMangaService.getContentType()).thenReturn(ContentType.MANGA);

        when(traktMovieService.getService()).thenReturn(ThirdPartyService.TRAKT);
        when(traktMovieService.getContentType()).thenReturn(ContentType.MOVIES);

        when(traktTvShowService.getService()).thenReturn(ThirdPartyService.TRAKT);
        when(traktTvShowService.getContentType()).thenReturn(ContentType.TVSHOWS);

        List<DataService> providers = Arrays.asList(
                anilistAnimeService,
                anilistMangaService,
                traktMovieService,
                traktTvShowService
        );

        dataFactory = new DataFactory(providers);
    }

    @Test
    @DisplayName("getProvider() should return AniList anime service")
    void getProvider_shouldReturnAnilistAnimeService() {
        DataService provider = dataFactory.getProvider(ThirdPartyService.ANILIST, ContentType.ANIME);
        assertEquals(anilistAnimeService, provider);
    }

    @Test
    @DisplayName("getProvider() should return AniList manga service")
    void getProvider_shouldReturnAnilistMangaService() {
        DataService provider = dataFactory.getProvider(ThirdPartyService.ANILIST, ContentType.MANGA);
        assertEquals(anilistMangaService, provider);
    }

    @Test
    @DisplayName("getProvider() should return Trakt movies service")
    void getProvider_shouldReturnTraktMoviesService() {
        DataService provider = dataFactory.getProvider(ThirdPartyService.TRAKT, ContentType.MOVIES);
        assertEquals(traktMovieService, provider);
    }

    @Test
    @DisplayName("getProvider() should return Trakt TV shows service")
    void getProvider_shouldReturnTraktTvShowsService() {
        DataService provider = dataFactory.getProvider(ThirdPartyService.TRAKT, ContentType.TVSHOWS);
        assertEquals(traktTvShowService, provider);
    }

    @Test
    @DisplayName("getProvider() should throw exception for unknown service")
    void getProvider_shouldThrowExceptionForUnknownService() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> dataFactory.getProvider(ThirdPartyService.ANILIST, ContentType.TVSHOWS)
        );
        assertTrue(exception.getMessage().contains("No provider found"));
    }

    @Test
    @DisplayName("getProvider() should throw exception for mismatched content type")
    void getProvider_shouldThrowExceptionForMismatchedContentType() {
        assertThrows(
                IllegalArgumentException.class,
                () -> dataFactory.getProvider(ThirdPartyService.TRAKT, ContentType.MANGA)
        );
    }
}
