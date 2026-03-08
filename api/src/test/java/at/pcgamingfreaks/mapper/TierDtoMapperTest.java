package at.pcgamingfreaks.mapper;

import at.pcgamingfreaks.model.Tier;
import at.pcgamingfreaks.model.dto.TierDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TierDtoMapper Tests")
class TierDtoMapperTest {

    @Test
    @DisplayName("map(Tier) should map all fields correctly")
    void map_shouldMapTierToDto() {
        Tier tier = new Tier();
        tier.setId(UUID.randomUUID());
        tier.setName("S");
        tier.setColor("#FF0000");
        tier.setScore(100);
        tier.setAdjustedScore(95);

        TierDTO dto = TierDtoMapper.map(tier);

        assertNotNull(dto);
        assertEquals(tier.getId(), dto.getId());
        assertEquals("S", dto.getName());
        assertEquals("#FF0000", dto.getColor());
        assertEquals(100, dto.getScore());
        assertEquals(95, dto.getAdjustedScore());
    }

    @Test
    @DisplayName("map(TierDTO) should map all fields correctly")
    void map_shouldMapDtoToTier() {
        TierDTO dto = new TierDTO();
        dto.setId(UUID.randomUUID());
        dto.setName("A");
        dto.setColor("#00FF00");
        dto.setScore(80);
        dto.setAdjustedScore(75);

        Tier tier = TierDtoMapper.map(dto);

        assertNotNull(tier);
        assertEquals("A", tier.getName());
        assertEquals("#00FF00", tier.getColor());
        assertEquals(80, tier.getScore());
        assertEquals(75, tier.getAdjustedScore());
    }

    @Test
    @DisplayName("map(Tier) should handle null values gracefully")
    void map_shouldHandleNullTierValues() {
        Tier tier = new Tier();

        TierDTO dto = TierDtoMapper.map(tier);

        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getColor());
        assertEquals(0, dto.getScore());
        assertEquals(0, dto.getAdjustedScore());
    }

    @Test
    @DisplayName("map(TierDTO) should not set ID (handled by JPA)")
    void map_shouldNotSetIdOnTier() {
        TierDTO dto = new TierDTO();
        dto.setId(UUID.randomUUID());
        dto.setName("B");
        dto.setColor("#0000FF");
        dto.setScore(60);
        dto.setAdjustedScore(55);

        Tier tier = TierDtoMapper.map(dto);

        assertNotNull(tier);
        assertNull(tier.getId());
        assertEquals("B", tier.getName());
    }

    @Test
    @DisplayName("map(Tier) should preserve exact score values")
    void map_shouldPreserveExactScoreValues() {
        Tier tier = new Tier();
        tier.setId(UUID.randomUUID());
        tier.setName("C");
        tier.setColor("#FFFF00");
        tier.setScore(42);
        tier.setAdjustedScore(38);

        TierDTO dto = TierDtoMapper.map(tier);

        assertEquals(42, dto.getScore());
        assertEquals(38, dto.getAdjustedScore());
    }
}
