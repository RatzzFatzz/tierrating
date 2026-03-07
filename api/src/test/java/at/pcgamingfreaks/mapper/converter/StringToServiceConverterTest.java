package at.pcgamingfreaks.mapper.converter;

import at.pcgamingfreaks.model.ThirdPartyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringToServiceConverter Tests")
class StringToServiceConverterTest {

    private final StringToServiceConverter converter = new StringToServiceConverter();

    static Stream<Arguments> validServiceNames() {
        return Stream.of(
            Arguments.of("anilist", ThirdPartyService.ANILIST),
            Arguments.of("ANILIST", ThirdPartyService.ANILIST),
            Arguments.of("AniList", ThirdPartyService.ANILIST),
            Arguments.of("trakt", ThirdPartyService.TRAKT),
            Arguments.of("TRAKT", ThirdPartyService.TRAKT),
            Arguments.of("Trakt", ThirdPartyService.TRAKT)
        );
    }

    @ParameterizedTest(name = "convert(\"{0}\") should return {1}")
    @MethodSource("validServiceNames")
    @DisplayName("convert() should convert valid service names")
    void convert_shouldConvertValidNames(String input, ThirdPartyService expected) {
        assertEquals(expected, converter.convert(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "unknown", "", "null_service"})
    @DisplayName("convert() should throw for invalid service")
    void convert_shouldThrowForInvalidService(String invalidInput) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(invalidInput));
    }
}
