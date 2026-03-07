package at.pcgamingfreaks.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tier Tests")
class TierTest {

    @Test
    @DisplayName("equals() should return true for same values")
    void equals_shouldReturnTrueForSameValues() {
        UUID id = UUID.randomUUID();
        Tier tier1 = new Tier(id, "#FF0000", "S Tier", 10.0, 9.5);
        Tier tier2 = new Tier(id, "#FF0000", "S Tier", 10.0, 9.5);

        assertEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for different id")
    void equals_shouldReturnFalseForDifferentId() {
        Tier tier1 = new Tier(UUID.randomUUID(), "#FF0000", "S Tier", 10.0, 9.5);
        Tier tier2 = new Tier(UUID.randomUUID(), "#FF0000", "S Tier", 10.0, 9.5);

        assertNotEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for different color")
    void equals_shouldReturnFalseForDifferentColor() {
        UUID id = UUID.randomUUID();
        Tier tier1 = new Tier(id, "#FF0000", "S Tier", 10.0, 9.5);
        Tier tier2 = new Tier(id, "#00FF00", "S Tier", 10.0, 9.5);

        assertNotEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for different name")
    void equals_shouldReturnFalseForDifferentName() {
        UUID id = UUID.randomUUID();
        Tier tier1 = new Tier(id, "#FF0000", "S Tier", 10.0, 9.5);
        Tier tier2 = new Tier(id, "#FF0000", "A Tier", 10.0, 9.5);

        assertNotEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for different score")
    void equals_shouldReturnFalseForDifferentScore() {
        UUID id = UUID.randomUUID();
        Tier tier1 = new Tier(id, "#FF0000", "S Tier", 10.0, 9.5);
        Tier tier2 = new Tier(id, "#FF0000", "S Tier", 9.0, 9.5);

        assertNotEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for different adjustedScore")
    void equals_shouldReturnFalseForDifferentAdjustedScore() {
        UUID id = UUID.randomUUID();
        Tier tier1 = new Tier(id, "#FF0000", "S Tier", 10.0, 9.5);
        Tier tier2 = new Tier(id, "#FF0000", "S Tier", 10.0, 8.5);

        assertNotEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for null")
    void equals_shouldReturnFalseForNull() {
        Tier tier = new Tier(UUID.randomUUID(), "#FF0000", "S Tier", 10.0, 9.5);

        assertNotEquals(null, tier);
    }

    @Test
    @DisplayName("equals() should return false for different class")
    void equals_shouldReturnFalseForDifferentClass() {
        Tier tier = new Tier(UUID.randomUUID(), "#FF0000", "S Tier", 10.0, 9.5);

        assertNotEquals("string", tier);
    }

    @Test
    @DisplayName("equals() should return true for same object")
    void equals_shouldReturnTrueForSameObject() {
        Tier tier = new Tier(UUID.randomUUID(), "#FF0000", "S Tier", 10.0, 9.5);

        assertEquals(tier, tier);
    }

    @Test
    @DisplayName("hashCode() should be consistent for same values")
    void hashCode_shouldBeConsistent() {
        UUID id = UUID.randomUUID();
        Tier tier1 = new Tier(id, "#FF0000", "S Tier", 10.0, 9.5);
        Tier tier2 = new Tier(id, "#FF0000", "S Tier", 10.0, 9.5);

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
        UUID id = UUID.randomUUID();
        Tier tier = new Tier(id, "#FF0000", "S Tier", 10.0, 9.5);

        assertEquals(id, tier.getId());
        assertEquals("#FF0000", tier.getColor());
        assertEquals("S Tier", tier.getName());
        assertEquals(10.0, tier.getScore());
        assertEquals(9.5, tier.getAdjustedScore());
    }

    @Test
    @DisplayName("setters should update fields")
    void setters_shouldUpdateFields() {
        Tier tier = new Tier();
        UUID id = UUID.randomUUID();
        
        tier.setId(id);
        tier.setColor("#00FF00");
        tier.setName("A Tier");
        tier.setScore(8.0);
        tier.setAdjustedScore(7.5);

        assertEquals(id, tier.getId());
        assertEquals("#00FF00", tier.getColor());
        assertEquals("A Tier", tier.getName());
        assertEquals(8.0, tier.getScore());
        assertEquals(7.5, tier.getAdjustedScore());
    }
}
