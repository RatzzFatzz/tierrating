package at.pcgamingfreaks.service.thirdparty.connector;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ThirdPartyRemovalResponseDTO;
import at.pcgamingfreaks.model.repo.ThirdPartyConnectionRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ThirdParty Connector Services Tests")
class ThirdPartyConnectorFactoryTest {

    @Nested
    @DisplayName("ThirdPartyConnectorFactory Tests")
    class FactoryTests {

        @Mock
        private TraktConnectorService traktConnectorService;

        @Mock
        private AnilistConnectorService anilistConnectorService;

        @Test
        @DisplayName("Constructor should create map from provider list")
        void constructor_shouldCreateMapFromProviderList() {
            when(traktConnectorService.getService()).thenReturn(ThirdPartyService.TRAKT);
            when(anilistConnectorService.getService()).thenReturn(ThirdPartyService.ANILIST);

            List<ThirdPartyConnectorService> providers = Arrays.asList(traktConnectorService, anilistConnectorService);
            ThirdPartyConnectorFactory factory = new ThirdPartyConnectorFactory(providers);

            assertDoesNotThrow(() -> factory.getProvider(ThirdPartyService.TRAKT));
            assertDoesNotThrow(() -> factory.getProvider(ThirdPartyService.ANILIST));
        }

        @Test
        @DisplayName("Constructor should handle empty provider list")
        void constructor_shouldHandleEmptyProviderList() {
            ThirdPartyConnectorFactory factory = new ThirdPartyConnectorFactory(Collections.emptyList());

            assertThrows(IllegalArgumentException.class, () -> factory.getProvider(ThirdPartyService.TRAKT));
            assertThrows(IllegalArgumentException.class, () -> factory.getProvider(ThirdPartyService.ANILIST));
        }

        @Test
        @DisplayName("getProvider() should return correct provider for TRAKT")
        void getProvider_shouldReturnCorrectProviderForTrakt() {
            when(traktConnectorService.getService()).thenReturn(ThirdPartyService.TRAKT);
            when(anilistConnectorService.getService()).thenReturn(ThirdPartyService.ANILIST);

            List<ThirdPartyConnectorService> providers = Arrays.asList(traktConnectorService, anilistConnectorService);
            ThirdPartyConnectorFactory factory = new ThirdPartyConnectorFactory(providers);

            ThirdPartyConnectorService result = factory.getProvider(ThirdPartyService.TRAKT);

            assertNotNull(result);
            assertEquals(traktConnectorService, result);
        }

        @Test
        @DisplayName("getProvider() should return correct provider for ANILIST")
        void getProvider_shouldReturnCorrectProviderForAnilist() {
            when(traktConnectorService.getService()).thenReturn(ThirdPartyService.TRAKT);
            when(anilistConnectorService.getService()).thenReturn(ThirdPartyService.ANILIST);

            List<ThirdPartyConnectorService> providers = Arrays.asList(traktConnectorService, anilistConnectorService);
            ThirdPartyConnectorFactory factory = new ThirdPartyConnectorFactory(providers);

            ThirdPartyConnectorService result = factory.getProvider(ThirdPartyService.ANILIST);

            assertNotNull(result);
            assertEquals(anilistConnectorService, result);
        }

        @Test
        @DisplayName("getProvider() should throw IllegalArgumentException for unknown service")
        void getProvider_shouldThrowExceptionForUnknownService() {
            when(traktConnectorService.getService()).thenReturn(ThirdPartyService.TRAKT);

            List<ThirdPartyConnectorService> providers = Collections.singletonList(traktConnectorService);
            ThirdPartyConnectorFactory factory = new ThirdPartyConnectorFactory(providers);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> factory.getProvider(ThirdPartyService.ANILIST)
            );

            assertTrue(exception.getMessage().contains("Third party service not found"));
            assertTrue(exception.getMessage().contains("ANILIST"));
        }

        @Test
        @DisplayName("getProvider() should handle single provider")
        void getProvider_shouldHandleSingleProvider() {
            when(traktConnectorService.getService()).thenReturn(ThirdPartyService.TRAKT);

            List<ThirdPartyConnectorService> providers = Collections.singletonList(traktConnectorService);
            ThirdPartyConnectorFactory factory = new ThirdPartyConnectorFactory(providers);

            ThirdPartyConnectorService result = factory.getProvider(ThirdPartyService.TRAKT);

            assertNotNull(result);
            assertEquals(traktConnectorService, result);
        }
    }

    @Nested
    @DisplayName("TraktConnectorService Tests")
    class TraktConnectorServiceTests {

        @Mock
        private ThirdPartyConnectionRepository thirdpartyConnectionRepository;

        @Mock
        private UserRepository userRepository;

        private TraktConnectorService traktConnectorService;

        private User testUser;
        private ThirdPartyConnection traktConnection;

        @BeforeEach
        void setUp() {
            traktConnectorService = new TraktConnectorService(thirdpartyConnectionRepository, userRepository);

            testUser = new User();
            testUser.setUsername("testuser");
            testUser.setEmail("test@example.com");

            traktConnection = new ThirdPartyConnection();
            traktConnection.setService(ThirdPartyService.TRAKT);
            traktConnection.setThirdPartyUserId("trakt-user-123");

            Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
            connections.put(ThirdPartyService.TRAKT, traktConnection);
            testUser.setConnections(connections);
        }

        @Test
        @DisplayName("getService() should return TRAKT")
        void getService_shouldReturnTrakt() {
            assertEquals(ThirdPartyService.TRAKT, traktConnectorService.getService());
        }

        @Test
        @DisplayName("removeConnection() should successfully remove connection")
        void removeConnection_shouldSuccessfullyRemoveConnection() {
            UUID connectionId = UUID.randomUUID();
            traktConnection.setId(connectionId);

            doNothing().when(thirdpartyConnectionRepository).deleteById(connectionId);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            ThirdPartyRemovalResponseDTO response = traktConnectorService.removeConnection(testUser);

            assertTrue(response.isSuccess());
            assertEquals("", response.getMessage());
            verify(thirdpartyConnectionRepository).deleteById(connectionId);
            verify(userRepository).save(testUser);
            assertNull(testUser.getConnections().get(ThirdPartyService.TRAKT));
        }

        @Test
        @DisplayName("removeConnection() should return failure on exception")
        void removeConnection_shouldReturnFailureOnException() {
            UUID connectionId = UUID.randomUUID();
            traktConnection.setId(connectionId);

            doThrow(new RuntimeException("Database error")).when(thirdpartyConnectionRepository).deleteById(connectionId);

            ThirdPartyRemovalResponseDTO response = traktConnectorService.removeConnection(testUser);

            assertFalse(response.isSuccess());
            assertEquals("Database error", response.getMessage());
            verify(thirdpartyConnectionRepository).deleteById(connectionId);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("removeConnection() should handle null pointer exception")
        void removeConnection_shouldHandleNullPointerException() {
            testUser.setConnections(null);

            ThirdPartyRemovalResponseDTO response = traktConnectorService.removeConnection(testUser);

            assertFalse(response.isSuccess());
            assertNotNull(response.getMessage());
            verify(thirdpartyConnectionRepository, never()).deleteById(any());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("removeConnection() should handle missing connection gracefully")
        void removeConnection_shouldHandleMissingConnectionGracefully() {
            testUser.getConnections().put(ThirdPartyService.TRAKT, null);

            ThirdPartyRemovalResponseDTO response = traktConnectorService.removeConnection(testUser);

            assertFalse(response.isSuccess());
            assertNotNull(response.getMessage());
        }
    }

    @Nested
    @DisplayName("AnilistConnectorService Tests")
    class AnilistConnectorServiceTests {

        @Mock
        private ThirdPartyConnectionRepository thirdpartyConnectionRepository;

        @Mock
        private UserRepository userRepository;

        private AnilistConnectorService anilistConnectorService;

        private User testUser;
        private ThirdPartyConnection anilistConnection;

        @BeforeEach
        void setUp() {
            anilistConnectorService = new AnilistConnectorService(thirdpartyConnectionRepository, userRepository);

            testUser = new User();
            testUser.setUsername("testuser");
            testUser.setEmail("test@example.com");

            anilistConnection = new ThirdPartyConnection();
            anilistConnection.setService(ThirdPartyService.ANILIST);
            anilistConnection.setThirdPartyUserId("anilist-user-456");

            Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
            connections.put(ThirdPartyService.ANILIST, anilistConnection);
            testUser.setConnections(connections);
        }

        @Test
        @DisplayName("getService() should return ANILIST")
        void getService_shouldReturnAnilist() {
            assertEquals(ThirdPartyService.ANILIST, anilistConnectorService.getService());
        }

        @Test
        @DisplayName("removeConnection() should successfully remove connection")
        void removeConnection_shouldSuccessfullyRemoveConnection() {
            UUID connectionId = UUID.randomUUID();
            anilistConnection.setId(connectionId);

            doNothing().when(thirdpartyConnectionRepository).deleteById(connectionId);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            ThirdPartyRemovalResponseDTO response = anilistConnectorService.removeConnection(testUser);

            assertTrue(response.isSuccess());
            assertEquals("", response.getMessage());
            verify(thirdpartyConnectionRepository).deleteById(connectionId);
            verify(userRepository).save(testUser);
            assertNull(testUser.getConnections().get(ThirdPartyService.ANILIST));
        }

        @Test
        @DisplayName("removeConnection() should return failure on exception")
        void removeConnection_shouldReturnFailureOnException() {
            UUID connectionId = UUID.randomUUID();
            anilistConnection.setId(connectionId);

            doThrow(new RuntimeException("Connection failed")).when(thirdpartyConnectionRepository).deleteById(connectionId);

            ThirdPartyRemovalResponseDTO response = anilistConnectorService.removeConnection(testUser);

            assertFalse(response.isSuccess());
            assertEquals("Connection failed", response.getMessage());
            verify(thirdpartyConnectionRepository).deleteById(connectionId);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("removeConnection() should handle null pointer exception")
        void removeConnection_shouldHandleNullPointerException() {
            testUser.setConnections(null);

            ThirdPartyRemovalResponseDTO response = anilistConnectorService.removeConnection(testUser);

            assertFalse(response.isSuccess());
            assertNotNull(response.getMessage());
            verify(thirdpartyConnectionRepository, never()).deleteById(any());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("removeConnection() should handle missing connection gracefully")
        void removeConnection_shouldHandleMissingConnectionGracefully() {
            testUser.getConnections().put(ThirdPartyService.ANILIST, null);

            ThirdPartyRemovalResponseDTO response = anilistConnectorService.removeConnection(testUser);

            assertFalse(response.isSuccess());
            assertNotNull(response.getMessage());
        }
    }

    @Nested
    @DisplayName("ThirdPartyService Enum Tests")
    class ThirdPartyServiceEnumTests {

        @Test
        @DisplayName("from() should return ANILIST for valid string")
        void from_shouldReturnAnilistForValidString() {
            assertEquals(ThirdPartyService.ANILIST, ThirdPartyService.from("ANILIST"));
            assertEquals(ThirdPartyService.ANILIST, ThirdPartyService.from("anilist"));
            assertEquals(ThirdPartyService.ANILIST, ThirdPartyService.from("Anilist"));
        }

        @Test
        @DisplayName("from() should return TRAKT for valid string")
        void from_shouldReturnTraktForValidString() {
            assertEquals(ThirdPartyService.TRAKT, ThirdPartyService.from("TRAKT"));
            assertEquals(ThirdPartyService.TRAKT, ThirdPartyService.from("trakt"));
            assertEquals(ThirdPartyService.TRAKT, ThirdPartyService.from("Trakt"));
        }

        @Test
        @DisplayName("from() should throw IllegalArgumentException for invalid string")
        void from_shouldThrowExceptionForInvalidString() {
            assertThrows(IllegalArgumentException.class, () -> ThirdPartyService.from("INVALID"));
            assertThrows(IllegalArgumentException.class, () -> ThirdPartyService.from(""));
            assertThrows(IllegalArgumentException.class, () -> ThirdPartyService.from("null"));
        }

        @Test
        @DisplayName("hasUserConnection() should return true when connection exists")
        void hasUserConnection_shouldReturnTrueWhenConnectionExists() {
            User user = new User();
            ThirdPartyConnection connection = new ThirdPartyConnection();
            connection.setService(ThirdPartyService.ANILIST);

            Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
            connections.put(ThirdPartyService.ANILIST, connection);
            user.setConnections(connections);

            assertTrue(ThirdPartyService.hasUserConnection(user, ThirdPartyService.ANILIST));
        }

        @Test
        @DisplayName("hasUserConnection() should return false when connection does not exist")
        void hasUserConnection_shouldReturnFalseWhenConnectionDoesNotExist() {
            User user = new User();
            user.setConnections(new HashMap<>());

            assertFalse(ThirdPartyService.hasUserConnection(user, ThirdPartyService.ANILIST));
        }

        @Test
        @DisplayName("hasUserConnection() should return false when connection is null")
        void hasUserConnection_shouldReturnFalseWhenConnectionIsNull() {
            User user = new User();
            Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
            connections.put(ThirdPartyService.ANILIST, null);
            user.setConnections(connections);

            assertFalse(ThirdPartyService.hasUserConnection(user, ThirdPartyService.ANILIST));
        }

        @Test
        @DisplayName("Enum should contain exactly ANILIST and TRAKT values")
        void enumShouldContainExpectedValues() {
            ThirdPartyService[] values = ThirdPartyService.values();
            assertEquals(2, values.length);
            assertTrue(Arrays.asList(values).contains(ThirdPartyService.ANILIST));
            assertTrue(Arrays.asList(values).contains(ThirdPartyService.TRAKT));
        }
    }

    @Nested
    @DisplayName("ThirdPartyRemovalResponseDTO Tests")
    class ThirdPartyRemovalResponseDTOTests {

        @Test
        @DisplayName("Constructor should set success and message fields")
        void constructor_shouldSetSuccessAndMessageFields() {
            ThirdPartyRemovalResponseDTO dto = new ThirdPartyRemovalResponseDTO(true, "Success message");

            assertTrue(dto.isSuccess());
            assertEquals("Success message", dto.getMessage());
        }

        @Test
        @DisplayName("Should handle failure response")
        void shouldHandleFailureResponse() {
            ThirdPartyRemovalResponseDTO dto = new ThirdPartyRemovalResponseDTO(false, "Error occurred");

            assertFalse(dto.isSuccess());
            assertEquals("Error occurred", dto.getMessage());
        }

        @Test
        @DisplayName("Should handle empty message")
        void shouldHandleEmptyMessage() {
            ThirdPartyRemovalResponseDTO dto = new ThirdPartyRemovalResponseDTO(true, "");

            assertTrue(dto.isSuccess());
            assertEquals("", dto.getMessage());
        }

        @Test
        @DisplayName("Should handle null message")
        void shouldHandleNullMessage() {
            ThirdPartyRemovalResponseDTO dto = new ThirdPartyRemovalResponseDTO(true, null);

            assertTrue(dto.isSuccess());
            assertNull(dto.getMessage());
        }
    }

    @Nested
    @DisplayName("Integration-style Tests")
    class IntegrationTests {

        @Mock
        private ThirdPartyConnectionRepository thirdpartyConnectionRepository;

        @Mock
        private UserRepository userRepository;

        @Test
        @DisplayName("Factory should correctly route to TraktConnectorService")
        void factory_shouldCorrectlyRouteToTraktConnectorService() {
            TraktConnectorService traktService = new TraktConnectorService(thirdpartyConnectionRepository, userRepository);
            AnilistConnectorService anilistService = new AnilistConnectorService(thirdpartyConnectionRepository, userRepository);

            List<ThirdPartyConnectorService> providers = Arrays.asList(traktService, anilistService);
            ThirdPartyConnectorFactory factory = new ThirdPartyConnectorFactory(providers);

            ThirdPartyConnectorService result = factory.getProvider(ThirdPartyService.TRAKT);

            assertNotNull(result);
            assertEquals(ThirdPartyService.TRAKT, result.getService());
            assertInstanceOf(TraktConnectorService.class, result);
        }

        @Test
        @DisplayName("Factory should correctly route to AnilistConnectorService")
        void factory_shouldCorrectlyRouteToAnilistConnectorService() {
            TraktConnectorService traktService = new TraktConnectorService(thirdpartyConnectionRepository, userRepository);
            AnilistConnectorService anilistService = new AnilistConnectorService(thirdpartyConnectionRepository, userRepository);

            List<ThirdPartyConnectorService> providers = Arrays.asList(traktService, anilistService);
            ThirdPartyConnectorFactory factory = new ThirdPartyConnectorFactory(providers);

            ThirdPartyConnectorService result = factory.getProvider(ThirdPartyService.ANILIST);

            assertNotNull(result);
            assertEquals(ThirdPartyService.ANILIST, result.getService());
            assertInstanceOf(AnilistConnectorService.class, result);
        }

        @Test
        @DisplayName("Both services should use same repository instances")
        void bothServices_shouldUseSameRepositoryInstances() {
            TraktConnectorService traktService = new TraktConnectorService(thirdpartyConnectionRepository, userRepository);
            AnilistConnectorService anilistService = new AnilistConnectorService(thirdpartyConnectionRepository, userRepository);

            User user = new User();
            user.setUsername("testuser");

            ThirdPartyConnection traktConnection = new ThirdPartyConnection();
            traktConnection.setId(UUID.randomUUID());
            traktConnection.setService(ThirdPartyService.TRAKT);

            ThirdPartyConnection anilistConnection = new ThirdPartyConnection();
            anilistConnection.setId(UUID.randomUUID());
            anilistConnection.setService(ThirdPartyService.ANILIST);

            Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
            connections.put(ThirdPartyService.TRAKT, traktConnection);
            connections.put(ThirdPartyService.ANILIST, anilistConnection);
            user.setConnections(connections);

            doNothing().when(thirdpartyConnectionRepository).deleteById(any());
            when(userRepository.save(any(User.class))).thenReturn(user);

            ThirdPartyRemovalResponseDTO traktResponse = traktService.removeConnection(user);
            ThirdPartyRemovalResponseDTO anilistResponse = anilistService.removeConnection(user);

            assertTrue(traktResponse.isSuccess());
            assertTrue(anilistResponse.isSuccess());
            verify(thirdpartyConnectionRepository, times(2)).deleteById(any());
            verify(userRepository, times(2)).save(any(User.class));
        }
    }
}
