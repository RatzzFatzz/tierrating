package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ThirdPartyRemovalResponseDTO;
import at.pcgamingfreaks.model.dto.UserDTO;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.thirdparty.connector.ThirdPartyConnectorFactory;
import at.pcgamingfreaks.service.thirdparty.connector.ThirdPartyConnectorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ThirdPartyConnectorFactory thirdPartyConnectorFactory;

    @Mock
    private ThirdPartyConnectorService connectorService;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_BIO = "Test bio";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername(TEST_USERNAME);
        testUser.setBio(TEST_BIO);
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.ANILIST, new ThirdPartyConnection());
        testUser.setConnections(connections);
    }

    @Test
    @DisplayName("user() should return user DTO for existing user")
    void user_shouldReturnUserDtoForExistingUser() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        ResponseEntity<UserDTO> response = userController.user(TEST_USERNAME);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(TEST_USERNAME, response.getBody().getUsername());
        assertEquals(TEST_BIO, response.getBody().getBio());
        assertTrue(response.getBody().getConnectedServices().contains(ThirdPartyService.ANILIST));
    }

    @Test
    @DisplayName("user() should return 404 for non-existing user")
    void user_shouldReturn404ForNonExistingUser() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        ResponseEntity<UserDTO> response = userController.user(TEST_USERNAME);

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("removeThirdPartyService() should remove connection for existing user")
    void removeThirdPartyService_shouldRemoveConnectionForExistingUser() {
        ThirdPartyRemovalResponseDTO responseDTO = new ThirdPartyRemovalResponseDTO(true, "Connection removed");
        
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(thirdPartyConnectorFactory.getProvider(ThirdPartyService.ANILIST)).thenReturn(connectorService);
        when(connectorService.removeConnection(any(User.class))).thenReturn(responseDTO);

        ResponseEntity<ThirdPartyRemovalResponseDTO> response = userController.removeThirdPartyService(TEST_USERNAME, ThirdPartyService.ANILIST);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Connection removed", response.getBody().getMessage());
    }

    @Test
    @DisplayName("removeThirdPartyService() should return failure for non-existing user")
    void removeThirdPartyService_shouldReturnFailureForNonExistingUser() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        ResponseEntity<ThirdPartyRemovalResponseDTO> response = userController.removeThirdPartyService(TEST_USERNAME, ThirdPartyService.ANILIST);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Unknown user", response.getBody().getMessage());
    }

    @Test
    @DisplayName("removeThirdPartyService() should work with TRAKT service")
    void removeThirdPartyService_shouldWorkWithTraktService() {
        ThirdPartyRemovalResponseDTO responseDTO = new ThirdPartyRemovalResponseDTO(true, "Connection removed");
        
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(thirdPartyConnectorFactory.getProvider(ThirdPartyService.TRAKT)).thenReturn(connectorService);
        when(connectorService.removeConnection(any(User.class))).thenReturn(responseDTO);

        ResponseEntity<ThirdPartyRemovalResponseDTO> response = userController.removeThirdPartyService(TEST_USERNAME, ThirdPartyService.TRAKT);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }
}
