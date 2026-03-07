package at.pcgamingfreaks.mapper.converter;

import at.pcgamingfreaks.model.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringToContentTypeConverter Tests")
class StringToContentTypeConverterTest {

    private final StringToContentTypeConverter converter = new StringToContentTypeConverter();

    @Test
    @DisplayName("convert() should convert anime")
    void convert_shouldConvertAnime() {
        assertEquals(ContentType.ANIME, converter.convert("anime"));
        assertEquals(ContentType.ANIME, converter.convert("ANIME"));
    }

    @Test
    @DisplayName("convert() should convert manga")
    void convert_shouldConvertManga() {
        assertEquals(ContentType.MANGA, converter.convert("manga"));
        assertEquals(ContentType.MANGA, converter.convert("MANGA"));
    }

    @Test
    @DisplayName("convert() should convert movies")
    void convert_shouldConvertMovies() {
        assertEquals(ContentType.MOVIES, converter.convert("movies"));
        assertEquals(ContentType.MOVIES, converter.convert("MOVIES"));
    }

    @Test
    @DisplayName("convert() should convert tvshows")
    void convert_shouldConvertTvshows() {
        assertEquals(ContentType.TVSHOWS, converter.convert("tvshows"));
        assertEquals(ContentType.TVSHOWS, converter.convert("TVSHOWS"));
    }

    @Test
    @DisplayName("convert() should convert tvshows_seasons")
    void convert_shouldConvertTvshowsSeasons() {
        assertEquals(ContentType.TVSHOWS_SEASONS, converter.convert("tvshows_seasons"));
        assertEquals(ContentType.TVSHOWS_SEASONS, converter.convert("TVSHOWS_SEASONS"));
    }

    @Test
    @DisplayName("convert() should throw for invalid type")
    void convert_shouldThrowForInvalidType() {
        assertThrows(IllegalArgumentException.class, () -> converter.convert("invalid"));
    }
}
