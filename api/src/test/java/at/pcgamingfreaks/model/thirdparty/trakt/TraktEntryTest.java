package at.pcgamingfreaks.model.thirdparty.trakt;

import at.pcgamingfreaks.model.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TraktEntry Tests")
class TraktEntryTest {

    @Test
    @DisplayName("constructor should set all fields")
    void constructor_shouldSetAllFields() {
        TraktEntry entry = new TraktEntry(123L, ContentType.MOVIES, null, "Test Movie", "cover.jpg");

        assertEquals(123L, entry.getId());
        assertEquals(ContentType.MOVIES, entry.getType());
        assertNull(entry.getSeason());
        assertEquals("Test Movie", entry.getTitle());
        assertEquals("cover.jpg", entry.getCover());
    }

    @Test
    @DisplayName("constructor should set season for TV show season entries")
    void constructor_shouldSetSeason() {
        TraktEntry entry = new TraktEntry(456L, ContentType.TVSHOWS_SEASONS, 2, "Show Season 2", null);

        assertEquals(456L, entry.getId());
        assertEquals(ContentType.TVSHOWS_SEASONS, entry.getType());
        assertEquals(2, entry.getSeason());
        assertNull(entry.getCover());
    }

    @Test
    @DisplayName("setters should update fields")
    void setters_shouldUpdateFields() {
        TraktEntry entry = new TraktEntry();
        entry.setId(789L);
        entry.setType(ContentType.TVSHOWS);
        entry.setTitle("Updated Show");
        entry.setCover("new_cover.jpg");

        assertEquals(789L, entry.getId());
        assertEquals(ContentType.TVSHOWS, entry.getType());
        assertEquals("Updated Show", entry.getTitle());
        assertEquals("new_cover.jpg", entry.getCover());
    }
}
