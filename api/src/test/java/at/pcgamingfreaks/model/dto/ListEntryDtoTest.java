package at.pcgamingfreaks.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ListEntryDTO Tests")
class ListEntryDtoTest {

    @Test
    @DisplayName("equals() should return true for same values")
    void equals_shouldReturnTrueForSameValues() {
        ListEntryDTO dto1 = new ListEntryDTO();
        dto1.setId(12345L);
        dto1.setTitle("Test Anime");
        dto1.setCover("https://example.com/cover.jpg");
        dto1.setScore(8.5f);

        ListEntryDTO dto2 = new ListEntryDTO();
        dto2.setId(12345L);
        dto2.setTitle("Test Anime");
        dto2.setCover("https://example.com/cover.jpg");
        dto2.setScore(8.5f);

        assertEquals(dto1, dto2);
    }

    @Test
    @DisplayName("equals() should return false for different id")
    void equals_shouldReturnFalseForDifferentId() {
        ListEntryDTO dto1 = new ListEntryDTO();
        dto1.setId(12345L);
        dto1.setTitle("Test");
        dto1.setCover("cover.jpg");
        dto1.setScore(8.5f);

        ListEntryDTO dto2 = new ListEntryDTO();
        dto2.setId(67890L);
        dto2.setTitle("Test");
        dto2.setCover("cover.jpg");
        dto2.setScore(8.5f);

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("equals() should return false for different title")
    void equals_shouldReturnFalseForDifferentTitle() {
        ListEntryDTO dto1 = new ListEntryDTO();
        dto1.setId(12345L);
        dto1.setTitle("Test1");
        dto1.setCover("cover.jpg");
        dto1.setScore(8.5f);

        ListEntryDTO dto2 = new ListEntryDTO();
        dto2.setId(12345L);
        dto2.setTitle("Test2");
        dto2.setCover("cover.jpg");
        dto2.setScore(8.5f);

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("equals() should return false for different cover")
    void equals_shouldReturnFalseForDifferentCover() {
        ListEntryDTO dto1 = new ListEntryDTO();
        dto1.setId(12345L);
        dto1.setTitle("Test");
        dto1.setCover("cover1.jpg");
        dto1.setScore(8.5f);

        ListEntryDTO dto2 = new ListEntryDTO();
        dto2.setId(12345L);
        dto2.setTitle("Test");
        dto2.setCover("cover2.jpg");
        dto2.setScore(8.5f);

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("equals() should return false for different score")
    void equals_shouldReturnFalseForDifferentScore() {
        ListEntryDTO dto1 = new ListEntryDTO();
        dto1.setId(12345L);
        dto1.setTitle("Test");
        dto1.setCover("cover.jpg");
        dto1.setScore(8.5f);

        ListEntryDTO dto2 = new ListEntryDTO();
        dto2.setId(12345L);
        dto2.setTitle("Test");
        dto2.setCover("cover.jpg");
        dto2.setScore(9.0f);

        assertNotEquals(dto1, dto2);
    }

    @Test
    @DisplayName("equals() should return false for null")
    void equals_shouldReturnFalseForNull() {
        ListEntryDTO dto = new ListEntryDTO();
        dto.setId(12345L);

        assertNotEquals(null, dto);
    }

    @Test
    @DisplayName("equals() should return false for different class")
    void equals_shouldReturnFalseForDifferentClass() {
        ListEntryDTO dto = new ListEntryDTO();
        dto.setId(12345L);

        assertNotEquals("string", dto);
    }

    @Test
    @DisplayName("equals() should return true for same object")
    void equals_shouldReturnTrueForSameObject() {
        ListEntryDTO dto = new ListEntryDTO();
        dto.setId(12345L);

        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("hashCode() should be consistent for same values")
    void hashCode_shouldBeConsistent() {
        ListEntryDTO dto1 = new ListEntryDTO();
        dto1.setId(12345L);
        dto1.setTitle("Test");
        dto1.setCover("cover.jpg");
        dto1.setScore(8.5f);

        ListEntryDTO dto2 = new ListEntryDTO();
        dto2.setId(12345L);
        dto2.setTitle("Test");
        dto2.setCover("cover.jpg");
        dto2.setScore(8.5f);

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
