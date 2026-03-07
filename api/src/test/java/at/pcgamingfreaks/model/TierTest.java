package at.pcgamingfreaks.model;

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

@DisplayName("Tier Tests")
class TierTest {

    private static final UUID BASE_ID = UUID.randomUUID();
    private static final String BASE_COLOR = "#FF0000";
    private static final String BASE_NAME = "S Tier";
    private static final double BASE_SCORE = 10.0;
    private static final double BASE_ADJUSTED_SCORE = 9.5;

    private static Tier createBaseTier() {
        return new Tier(BASE_ID, BASE_COLOR, BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE);
    }

    @Nested
    @DisplayName("equals() tests")
    class EqualsTests {

        static Stream<Arguments> equalTiers() {
            return Stream.of(
                Arguments.of("same values", createBaseTier(), createBaseTier())
            );
        }

        static Stream<Arguments> unequalTiers() {
            return Stream.of(
                Arguments.of("different id", 
                    new Tier(UUID.randomUUID(), BASE_COLOR, BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE),
                    new Tier(UUID.randomUUID(), BASE_COLOR, BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE)),
                Arguments.of("different color",
                    new Tier(BASE_ID, "#FF0000", BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE),
                    new Tier(BASE_ID, "#00FF00", BASE_NAME, BASE_SCORE, BASE_ADJUSTED_SCORE)),
                Arguments.of("different name",
                    new Tier(BASE_ID, BASE_COLOR, "S Tier", BASE_SCORE, BASE_ADJUSTED_SCORE),
                    new Tier(BASE_ID, BASE_COLOR, "A Tier", BASE_SCORE, BASE_ADJUSTED_SCORE)),
                Arguments.of("different score",
                    new Tier(BASE_ID, BASE_COLOR, BASE_NAME, 10.0, BASE_ADJUSTED_SCORE),
                    new Tier(BASE_ID, BASE_COLOR, BASE_NAME, 9.0, BASE_ADJUSTED_SCORE)),
                Arguments.of("different adjustedScore",
                    new Tier(BASE_ID, BASE_COLOR, BASE_NAME, BASE_SCORE, 9.5),
                    new Tier(BASE_ID, BASE_COLOR, BASE_NAME, BASE_SCORE, 8.5))
            );
        }

        @ParameterizedTest(name = "should return true for {0}")
        @MethodSource("equalTiers")
        @DisplayName("equals() should return true for equal tiers")
        void equals_shouldReturnTrueForEqualTiers(String description, Tier tier1, Tier tier2) {
            assertEquals(tier1, tier2);
        }

        @ParameterizedTest(name = "should return false for {0}")
        @MethodSource("unequalTiers")
        @DisplayName("equals() should return false for unequal tiers")
        void equals_shouldReturnFalseForUnequalTiers(String description, Tier tier1, Tier tier2) {
            assertNotEquals(tier1, tier2);
        }

        @ParameterizedTest(name = "should return false for {0}")
        @ValueSource(strings = {"null", "different class"})
        @DisplayName("equals() edge cases")
        void equals_edgeCases(String caseType) {
            Tier tier = createBaseTier();
            
            switch (caseType) {
                case "null" -> assertNotEquals(null, tier);
                case "different class" -> assertNotEquals("string", tier);
            }
        }

        @Test
        @DisplayName("equals() should return true for same object")
        void equals_shouldReturnTrueForSameObject() {
            Tier tier = createBaseTier();
            assertEquals(tier, tier);
        }
    }

    @Test
    @DisplayName("hashCode() should be consistent for same values")
    void hashCode_shouldBeConsistent() {
        Tier tier1 = createBaseTier();
        Tier tier2 = createBaseTier();

        assertEquals(tier1.hashCode(), tier2.hashCode());
    }

    @Test
    @DisplayName("no-args constructor should create instance")
    void noArgsConstructor_shouldCreateInstance() {
        Tier tier = new Tier();

        assertNotNull(tier);
    }

    @Test
    @DisplayName("all-args constructor should set all fields")
    void allArgsConstructor_shouldSetAllFields() {
        Tier tier = createBaseTier();

        assertEquals(BASE_ID, tier.getId());
        assertEquals(BASE_COLOR, tier.getColor());
        assertEquals(BASE_NAME, tier.getName());
        assertEquals(BASE_SCORE, tier.getScore());
        assertEquals(BASE_ADJUSTED_SCORE, tier.getAdjustedScore());
    }

    @Test
    @DisplayName("setters should update fields")
    void setters_shouldUpdateFields() {
        Tier tier = new Tier();
        UUID newId = UUID.randomUUID();
        
        tier.setId(newId);
        tier.setColor("#00FF00");
        tier.setName("A Tier");
        tier.setScore(8.0);
        tier.setAdjustedScore(7.5);

        assertEquals(newId, tier.getId());
        assertEquals("#00FF00", tier.getColor());
        assertEquals("A Tier", tier.getName());
        assertEquals(8.0, tier.getScore());
        assertEquals(7.5, tier.getAdjustedScore());
    }
}
