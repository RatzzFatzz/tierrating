package at.pcgamingfreaks.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("GlobalProperties Tests")
class GlobalPropertiesTest {

    @Test
    @DisplayName("ANILIST_API_URL constant should have correct value")
    void anilistApiUrl_shouldHaveCorrectValue() {
        assertEquals("https://graphql.anilist.co", GlobalProperties.ANILIST_API_URL);
    }

    @Test
    @DisplayName("TRAKT_API_URL constant should have correct value")
    void traktApiUrl_shouldHaveCorrectValue() {
        assertEquals("https://api.trakt.tv", GlobalProperties.TRAKT_API_URL);
    }

    @Test
    @DisplayName("GlobalProperties can be instantiated")
    void globalProperties_canBeInstantiated() {
        assertNotNull(new GlobalProperties());
    }
}
