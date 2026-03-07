package at.pcgamingfreaks.mapper.converter;

import at.pcgamingfreaks.model.ThirdPartyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringToServiceConverter Tests")
class StringToServiceConverterTest {

    private final StringToServiceConverter converter = new StringToServiceConverter();

    @Test
    @DisplayName("convert() should convert 'anilist' to ANILIST")
    void convert_shouldConvertAnilist() {
        assertEquals(ThirdPartyService.ANILIST, converter.convert("anilist"));
        assertEquals(ThirdPartyService.ANILIST, converter.convert("ANILIST"));
        assertEquals(ThirdPartyService.ANILIST, converter.convert("AniList"));
    }

    @Test
    @DisplayName("convert() should convert 'trakt' to TRAKT")
    void convert_shouldConvertTrakt() {
        assertEquals(ThirdPartyService.TRAKT, converter.convert("trakt"));
        assertEquals(ThirdPartyService.TRAKT, converter.convert("TRAKT"));
        assertEquals(ThirdPartyService.TRAKT, converter.convert("Trakt"));
    }

    @Test
    @DisplayName("convert() should throw for invalid service")
    void convert_shouldThrowForInvalidService() {
        assertThrows(IllegalArgumentException.class, () -> converter.convert("invalid"));
    }
}
