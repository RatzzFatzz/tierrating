package at.pcgamingfreaks.model.exceptions;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Model Exceptions Tests")
class ModelExceptionsTest {

    @Nested
    @DisplayName("ThirdPartyUnconfiguredException Tests")
    class ThirdPartyUnconfiguredExceptionTests {

        static Stream<Arguments> services() {
            return Stream.of(
                Arguments.of(ThirdPartyService.ANILIST),
                Arguments.of(ThirdPartyService.TRAKT)
            );
        }

        @ParameterizedTest(name = "should store service: {0}")
        @MethodSource("services")
        @DisplayName("ThirdPartyUnconfiguredException should store service")
        void thirdPartyUnconfiguredException_shouldStoreService(ThirdPartyService service) {
            ThirdPartyUnconfiguredException ex = new ThirdPartyUnconfiguredException(service);
            
            assertEquals(service, ex.getUnconfiguredService());
        }
    }

    @Nested
    @DisplayName("ThirdPartySyncException Tests")
    class ThirdPartySyncExceptionTests {

        @Test
        @DisplayName("should store message")
        void shouldStoreMessage() {
            ThirdPartySyncException ex = new ThirdPartySyncException("Sync failed");
            
            assertEquals("Sync failed", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("ThirdPartyAuthenticationException Tests")
    class ThirdPartyAuthenticationExceptionTests {

        @Test
        @DisplayName("should store message")
        void shouldStoreMessage() {
            ThirdPartyAuthenticationException ex = new ThirdPartyAuthenticationException("Auth failed");
            
            assertEquals("Auth failed", ex.getMessage());
        }

        @Test
        @DisplayName("should store cause")
        void shouldStoreCause() {
            RuntimeException cause = new RuntimeException("Root cause");
            ThirdPartyAuthenticationException ex = new ThirdPartyAuthenticationException(cause);
            
            assertEquals(cause, ex.getCause());
        }
    }

    @Nested
    @DisplayName("EntryNotFoundException Tests")
    class EntryNotFoundExceptionTests {

        @Test
        @DisplayName("should store message")
        void shouldStoreMessage() {
            EntryNotFoundException ex = new EntryNotFoundException("Entry not found");
            
            assertEquals("Entry not found", ex.getMessage());
        }

        static Stream<Arguments> contentTypeAndId() {
            return Stream.of(
                Arguments.of(ContentType.ANIME, 12345L),
                Arguments.of(ContentType.MOVIES, 67890L),
                Arguments.of(ContentType.TVSHOWS, 11111L),
                Arguments.of(ContentType.MANGA, 22222L),
                Arguments.of(ContentType.TVSHOWS_SEASONS, 33333L)
            );
        }

        @ParameterizedTest(name = "should format message with type={0} and id={1}")
        @MethodSource("contentTypeAndId")
        @DisplayName("should format message with type and id")
        void shouldFormatMessage(ContentType type, long id) {
            EntryNotFoundException ex = new EntryNotFoundException(type, id);
            
            assertTrue(ex.getMessage().contains(type.name()));
            assertTrue(ex.getMessage().contains(String.valueOf(id)));
        }
    }
}
