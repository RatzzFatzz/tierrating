package at.pcgamingfreaks.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ListEntryDTO Tests")
class ListEntryDtoTest {

    private static final long BASE_ID = 12345L;
    private static final String BASE_TITLE = "Test Anime";
    private static final String BASE_COVER = "https://example.com/cover.jpg";
    private static final float BASE_SCORE = 8.5f;

    private static ListEntryDTO createBaseDto() {
        ListEntryDTO dto = new ListEntryDTO();
        dto.setId(BASE_ID);
        dto.setTitle(BASE_TITLE);
        dto.setCover(BASE_COVER);
        dto.setScore(BASE_SCORE);
        return dto;
    }

    @Nested
    @DisplayName("equals() tests")
    class EqualsTests {

        static Stream<Arguments> equalDtos() {
            return Stream.of(
                Arguments.of("same values", createBaseDto(), createBaseDto())
            );
        }

        static Stream<Arguments> unequalDtos() {
            ListEntryDTO base = createBaseDto();
            
            ListEntryDTO differentId = new ListEntryDTO();
            differentId.setId(67890L);
            differentId.setTitle(BASE_TITLE);
            differentId.setCover(BASE_COVER);
            differentId.setScore(BASE_SCORE);

            ListEntryDTO differentTitle = new ListEntryDTO();
            differentTitle.setId(BASE_ID);
            differentTitle.setTitle("Different Title");
            differentTitle.setCover(BASE_COVER);
            differentTitle.setScore(BASE_SCORE);

            ListEntryDTO differentCover = new ListEntryDTO();
            differentCover.setId(BASE_ID);
            differentCover.setTitle(BASE_TITLE);
            differentCover.setCover("different.jpg");
            differentCover.setScore(BASE_SCORE);

            ListEntryDTO differentScore = new ListEntryDTO();
            differentScore.setId(BASE_ID);
            differentScore.setTitle(BASE_TITLE);
            differentScore.setCover(BASE_COVER);
            differentScore.setScore(9.0f);

            return Stream.of(
                Arguments.of("different id", base, differentId),
                Arguments.of("different title", base, differentTitle),
                Arguments.of("different cover", base, differentCover),
                Arguments.of("different score", base, differentScore)
            );
        }

        @ParameterizedTest(name = "should return true for {0}")
        @MethodSource("equalDtos")
        @DisplayName("equals() should return true for equal DTOs")
        void equals_shouldReturnTrueForEqualDtos(String description, ListEntryDTO dto1, ListEntryDTO dto2) {
            assertEquals(dto1, dto2);
        }

        @ParameterizedTest(name = "should return false for {0}")
        @MethodSource("unequalDtos")
        @DisplayName("equals() should return false for unequal DTOs")
        void equals_shouldReturnFalseForUnequalDtos(String description, ListEntryDTO dto1, ListEntryDTO dto2) {
            assertNotEquals(dto1, dto2);
        }

        @ParameterizedTest(name = "should return false for {0}")
        @ValueSource(strings = {"null", "different class"})
        @DisplayName("equals() edge cases")
        void equals_edgeCases(String caseType) {
            ListEntryDTO dto = createBaseDto();
            
            switch (caseType) {
                case "null" -> assertNotEquals(null, dto);
                case "different class" -> assertNotEquals("string", dto);
            }
        }

        @Test
        @DisplayName("equals() should return true for same object")
        void equals_shouldReturnTrueForSameObject() {
            ListEntryDTO dto = createBaseDto();
            assertEquals(dto, dto);
        }
    }

    @Test
    @DisplayName("hashCode() should be consistent for same values")
    void hashCode_shouldBeConsistent() {
        ListEntryDTO dto1 = createBaseDto();
        ListEntryDTO dto2 = createBaseDto();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("setters should update fields")
    void setters_shouldUpdateFields() {
        ListEntryDTO dto = new ListEntryDTO();
        
        dto.setId(12345L);
        dto.setTitle("Test Title");
        dto.setCover("https://example.com/cover.jpg");
        dto.setScore(9.5f);

        assertEquals(12345L, dto.getId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("https://example.com/cover.jpg", dto.getCover());
        assertEquals(9.5f, dto.getScore());
    }
}
