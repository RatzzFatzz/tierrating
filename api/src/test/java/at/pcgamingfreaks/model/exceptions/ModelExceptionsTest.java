package at.pcgamingfreaks.model.exceptions;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Model Exceptions Tests")
class ModelExceptionsTest {

    @Test
    @DisplayName("ThirdPartyUnconfiguredException should store service")
    void thirdPartyUnconfiguredException_shouldStoreService() {
        ThirdPartyUnconfiguredException ex = new ThirdPartyUnconfiguredException(ThirdPartyService.ANILIST);
        
        assertEquals(ThirdPartyService.ANILIST, ex.getUnconfiguredService());
    }

    @Test
    @DisplayName("ThirdPartyUnconfiguredException should work with TRAKT")
    void thirdPartyUnconfiguredException_shouldWorkWithTrakt() {
        ThirdPartyUnconfiguredException ex = new ThirdPartyUnconfiguredException(ThirdPartyService.TRAKT);
        
        assertEquals(ThirdPartyService.TRAKT, ex.getUnconfiguredService());
    }

    @Test
    @DisplayName("ThirdPartySyncException should store message")
    void thirdPartySyncException_shouldStoreMessage() {
        ThirdPartySyncException ex = new ThirdPartySyncException("Sync failed");
        
        assertEquals("Sync failed", ex.getMessage());
    }

    @Test
    @DisplayName("ThirdPartyAuthenticationException should store message")
    void thirdPartyAuthenticationException_shouldStoreMessage() {
        ThirdPartyAuthenticationException ex = new ThirdPartyAuthenticationException("Auth failed");
        
        assertEquals("Auth failed", ex.getMessage());
    }

    @Test
    @DisplayName("ThirdPartyAuthenticationException should store cause")
    void thirdPartyAuthenticationException_shouldStoreCause() {
        RuntimeException cause = new RuntimeException("Root cause");
        ThirdPartyAuthenticationException ex = new ThirdPartyAuthenticationException(cause);
        
        assertEquals(cause, ex.getCause());
    }

    @Test
    @DisplayName("EntryNotFoundException should store message")
    void entryNotFoundException_shouldStoreMessage() {
        EntryNotFoundException ex = new EntryNotFoundException("Entry not found");
        
        assertEquals("Entry not found", ex.getMessage());
    }

    @Test
    @DisplayName("EntryNotFoundException should format message with type and id")
    void entryNotFoundException_shouldFormatMessage() {
        EntryNotFoundException ex = new EntryNotFoundException(ContentType.ANIME, 12345L);
        
        assertTrue(ex.getMessage().contains("ANIME"));
        assertTrue(ex.getMessage().contains("12345"));
    }

    @Test
    @DisplayName("EntryNotFoundException should work with MOVIES type")
    void entryNotFoundException_shouldWorkWithMovieType() {
        EntryNotFoundException ex = new EntryNotFoundException(ContentType.MOVIES, 67890L);
        
        assertTrue(ex.getMessage().contains("MOVIES"));
        assertTrue(ex.getMessage().contains("67890"));
    }

    @Test
    @DisplayName("EntryNotFoundException should work with TVSHOWS type")
    void entryNotFoundException_shouldWorkWithShowType() {
        EntryNotFoundException ex = new EntryNotFoundException(ContentType.TVSHOWS, 11111L);
        
        assertTrue(ex.getMessage().contains("TVSHOWS"));
        assertTrue(ex.getMessage().contains("11111"));
    }
}
