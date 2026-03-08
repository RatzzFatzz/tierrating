package at.pcgamingfreaks.mapper.converter;

import at.pcgamingfreaks.model.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringToContentTypeConverter Tests")
class StringToContentTypeConverterTest {

    private final StringToContentTypeConverter converter = new StringToContentTypeConverter();

    static Stream<Arguments> validContentTypeNames() {
        return Stream.of(
            Arguments.of("anime", ContentType.ANIME),
            Arguments.of("ANIME", ContentType.ANIME),
            Arguments.of("manga", ContentType.MANGA),
            Arguments.of("MANGA", ContentType.MANGA),
            Arguments.of("movies", ContentType.MOVIES),
            Arguments.of("MOVIES", ContentType.MOVIES),
            Arguments.of("tvshows", ContentType.TVSHOWS),
            Arguments.of("TVSHOWS", ContentType.TVSHOWS),
            Arguments.of("tvshows_seasons", ContentType.TVSHOWS_SEASONS),
            Arguments.of("TVSHOWS_SEASONS", ContentType.TVSHOWS_SEASONS)
        );
    }

    @ParameterizedTest(name = "convert(\"{0}\") should return {1}")
    @MethodSource("validContentTypeNames")
    @DisplayName("convert() should convert valid content type names")
    void convert_shouldConvertValidNames(String input, ContentType expected) {
        assertEquals(expected, converter.convert(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "unknown", "", "null_type"})
    @DisplayName("convert() should throw for invalid content type")
    void convert_shouldThrowForInvalidType(String invalidInput) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(invalidInput));
    }
}
