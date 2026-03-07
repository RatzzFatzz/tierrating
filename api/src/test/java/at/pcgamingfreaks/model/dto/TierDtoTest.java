package at.pcgamingfreaks.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TierDTO Tests")
class TierDtoTest {

    @Test
    @DisplayName("equals() should return true for same values")
    void equals_shouldReturnTrueForSameValues() {
        UUID id = UUID.randomUUID();
        TierDTO tier1 = new TierDTO(id, "#FF0000", "S Tier", 10.0, 9.5);
        TierDTO tier2 = new TierDTO(id, "#FF0000", "S Tier", 10.0, 9.5);

        assertEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for different id")
    void equals_shouldReturnFalseForDifferentId() {
        TierDTO tier1 = new TierDTO(UUID.randomUUID(), "#FF0000", "S Tier", 10.0, 9.5);
        TierDTO tier2 = new TierDTO(UUID.randomUUID(), "#FF0000", "S Tier", 10.0, 9.5);

        assertNotEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for different color")
    void equals_shouldReturnFalseForDifferentColor() {
        UUID id = UUID.randomUUID();
        TierDTO tier1 = new TierDTO(id, "#FF0000", "S Tier", 10.0, 9.5);
        TierDTO tier2 = new TierDTO(id, "#00FF00", "S Tier", 10.0, 9.5);

        assertNotEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for different name")
    void equals_shouldReturnFalseForDifferentName() {
        UUID id = UUID.randomUUID();
        TierDTO tier1 = new TierDTO(id, "#FF0000", "S Tier", 10.0, 9.5);
        TierDTO tier2 = new TierDTO(id, "#FF0000", "A Tier", 10.0, 9.5);

        assertNotEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for different score")
    void equals_shouldReturnFalseForDifferentScore() {
        UUID id = UUID.randomUUID();
        TierDTO tier1 = new TierDTO(id, "#FF0000", "S Tier", 10.0, 9.5);
        TierDTO tier2 = new TierDTO(id, "#FF0000", "S Tier", 9.0, 9.5);

        assertNotEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for different adjustedScore")
    void equals_shouldReturnFalseForDifferentAdjustedScore() {
        UUID id = UUID.randomUUID();
        TierDTO tier1 = new TierDTO(id, "#FF0000", "S Tier", 10.0, 9.5);
        TierDTO tier2 = new TierDTO(id, "#FF0000", "S Tier", 10.0, 8.5);

        assertNotEquals(tier1, tier2);
    }

    @Test
    @DisplayName("equals() should return false for null")
    void equals_shouldReturnFalseForNull() {
        TierDTO tier = new TierDTO(UUID.randomUUID(), "#FF0000", "S Tier", 10.0, 9.5);

        assertNotEquals(null, tier);
    }

    @Test
    @DisplayName("equals() should return false for different class")
    void equals_shouldReturnFalseForDifferentClass() {
        TierDTO tier = new TierDTO(UUID.randomUUID(), "#FF0000", "S Tier", 10.0, 9.5);

        assertNotEquals("string", tier);
    }

    @Test
    @DisplayName("equals() should return true for same object")
    void equals_shouldReturnTrueForSameObject() {
        TierDTO tier = new TierDTO(UUID.randomUUID(), "#FF0000", "S Tier", 10.0, 9.5);

        assertEquals(tier, tier);
    }

    @Test
    @DisplayName("hashCode() should be consistent for same values")
    void hashCode_shouldBeConsistent() {
        UUID id = UUID.randomUUID();
        TierDTO tier1 = new TierDTO(id, "#FF0000", "S Tier", 10.0, 9.5);
        TierDTO tier2 = new TierDTO(id, "#FF0000", "S Tier", 10.0, 9.5);

        assertEquals(tier1.hashCode(), tier2.hashCode());
    }

    @Test
    @DisplayName("constructor without id should set other fields")
    void constructorWithoutId_shouldSetOtherFields() {
        TierDTO tier = new TierDTO("#FF0000", "S Tier", 10.0, 9.5);

        assertNull(tier.getId());
        assertEquals("#FF0000", tier.getColor());
        assertEquals("S Tier", tier.getName());
        assertEquals(10.0, tier.getScore());
        assertEquals(9.5, tier.getAdjustedScore());
    }

    @Test
    @DisplayName("constructor with id should set all fields")
    void constructorWithId_shouldSetAllFields() {
        UUID id = UUID.randomUUID();
        TierDTO tier = new TierDTO(id, "#FF0000", "S Tier", 10.0, 9.5);

        assertEquals(id, tier.getId());
        assertEquals("#FF0000", tier.getColor());
        assertEquals("S Tier", tier.getName());
        assertEquals(10.0, tier.getScore());
        assertEquals(9.5, tier.getAdjustedScore());
    }

    @Test
    @DisplayName("no-args constructor should create instance")
    void noArgsConstructor_shouldCreateInstance() {
        TierDTO tier = new TierDTO();

        assertNotNull(tier);
    }

    @Test
    @DisplayName("setters should update fields")
    void setters_shouldUpdateFields() {
        TierDTO tier = new TierDTO();
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
