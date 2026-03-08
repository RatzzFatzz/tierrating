package at.pcgamingfreaks.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TierDTO Tests")
class TierDtoTest {

    private static final UUID BASE_ID = UUID.randomUUID();
    private static final String BASE_COLOR = "#FF0000";
    private static final String BASE_NAME = "S Tier";
    private static final double BASE_SCORE = 10.0;
    private static final double BASE_ADJUSTED_SCORE = 9.5;

    private static TierDTO createBaseTierDto() {
        return new TierDTO(BASE_ID, BASE_COLOR, BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE);
    }

    @Nested
    @DisplayName("equals() tests")
    class EqualsTests {

        static Stream<Arguments> equalTierDtos() {
            return Stream.of(
                Arguments.of("same values", createBaseTierDto(), createBaseTierDto())
            );
        }

        static Stream<Arguments> unequalTierDtos() {
            return Stream.of(
                Arguments.of("different id",
                    new TierDTO(UUID.randomUUID(), BASE_COLOR, BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE),
                    new TierDTO(UUID.randomUUID(), BASE_COLOR, BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE)),
                Arguments.of("different color",
                    new TierDTO(BASE_ID, "#FF0000", BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE),
                    new TierDTO(BASE_ID, "#00FF00", BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE)),
                Arguments.of("different name",
                    new TierDTO(BASE_ID, BASE_COLOR, "S Tier", BASE_SCORE, BASE_ADJUSTED_SCORE),
                    new TierDTO(BASE_ID, BASE_COLOR, "A Tier", BASE_SCORE, BASE_ADJUSTED_SCORE)),
                Arguments.of("different score",
                    new TierDTO(BASE_ID, BASE_COLOR, BASE_NAME, 10.0, BASE_ADJUSTED_SCORE),
                    new TierDTO(BASE_ID, BASE_COLOR, BASE_NAME, 9.0, BASE_ADJUSTED_SCORE)),
                Arguments.of("different adjustedScore",
                    new TierDTO(BASE_ID, BASE_COLOR, BASE_NAME, BASE_SCORE, 9.5),
                    new TierDTO(BASE_ID, BASE_COLOR, BASE_NAME, BASE_SCORE, 8.5))
            );
        }

        @ParameterizedTest(name = "should return true for {0}")
        @MethodSource("equalTierDtos")
        @DisplayName("equals() should return true for equal TierDTOs")
        void equals_shouldReturnTrueForEqualTierDtos(String description, TierDTO dto1, TierDTO dto2) {
            assertEquals(dto1, dto2);
        }

        @ParameterizedTest(name = "should return false for {0}")
        @MethodSource("unequalTierDtos")
        @DisplayName("equals() should return false for unequal TierDTOs")
        void equals_shouldReturnFalseForUnequalTierDtos(String description, TierDTO dto1, TierDTO dto2) {
            assertNotEquals(dto1, dto2);
        }

        @ParameterizedTest(name = "should return false for {0}")
        @ValueSource(strings = {"null", "different class"})
        @DisplayName("equals() edge cases")
        void equals_edgeCases(String caseType) {
            TierDTO dto = createBaseTierDto();
            
            switch (caseType) {
                case "null" -> assertNotEquals(null, dto);
                case "different class" -> assertNotEquals("string", dto);
            }
        }

        @Test
        @DisplayName("equals() should return true for same object")
        void equals_shouldReturnTrueForSameObject() {
            TierDTO dto = createBaseTierDto();
            assertEquals(dto, dto);
        }
    }

    @Test
    @DisplayName("hashCode() should be consistent for same values")
    void hashCode_shouldBeConsistent() {
        TierDTO dto1 = createBaseTierDto();
        TierDTO dto2 = createBaseTierDto();

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("constructor without id should set other fields")
    void constructorWithoutId_shouldSetOtherFields() {
        TierDTO dto = new TierDTO(BASE_COLOR, BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE);

        assertNull(dto.getId());
        assertEquals(BASE_COLOR, dto.getColor());
        assertEquals(BASE_NAME, dto.getName());
        assertEquals(BASE_SCORE, dto.getScore());
        assertEquals(BASE_ADJUSTED_SCORE, dto.getAdjustedScore());
    }

    @Test
    @DisplayName("constructor with id should set all fields")
    void constructorWithId_shouldSetAllFields() {
        TierDTO dto = createBaseTierDto();

        assertEquals(BASE_ID, dto.getId());
        assertEquals(BASE_COLOR, dto.getColor());
        assertEquals(BASE_NAME, dto.getName());
        assertEquals(BASE_SCORE, dto.getScore());
        assertEquals(BASE_ADJUSTED_SCORE, dto.getAdjustedScore());
    }

    @Test
    @DisplayName("no-args constructor should create instance")
    void noArgsConstructor_shouldCreateInstance() {
        TierDTO dto = new TierDTO();

        assertNotNull(dto);
    }

    @Test
    @DisplayName("setters should update fields")
    void setters_shouldUpdateFields() {
        TierDTO dto = new TierDTO();
        UUID newId = UUID.randomUUID();
        
        dto.setId(newId);
        dto.setColor("#00FF00");
        dto.setName("A Tier");
        dto.setScore(8.0);
        dto.setAdjustedScore(7.5);

        assertEquals(newId, dto.getId());
        assertEquals("#00FF00", dto.getColor());
        assertEquals("A Tier", dto.getName());
        assertEquals(8.0, dto.getScore());
        assertEquals(7.5, dto.getAdjustedScore());
    }
}
