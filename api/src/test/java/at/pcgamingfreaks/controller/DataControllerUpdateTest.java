package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.UpdateScoreRequestDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.thirdparty.data.DataFactory;
import at.pcgamingfreaks.service.thirdparty.data.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataController Update Tests")
class DataControllerUpdateTest {

    @Mock
    private DataFactory dataFactory;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DataService dataService;

    @InjectMocks
    private DataController dataController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.ANILIST, mock(ThirdPartyConnection.class));
        testUser.setConnections(connections);
    }

    @Test
    @DisplayName("update() should delegate to data service for valid request")
    void update_shouldDelegateToDataService() {
        UpdateScoreRequestDTO request = new UpdateScoreRequestDTO();
        request.setUsername("testuser");
        request.setService(ThirdPartyService.ANILIST);
        request.setType(ContentType.ANIME);
        request.setId(42L);
        request.setScore(9.0f);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(dataFactory.getProvider(ThirdPartyService.ANILIST, ContentType.ANIME)).thenReturn(dataService);
        doNothing().when(dataService).update(42L, 9.0f, testUser);

        assertDoesNotThrow(() -> dataController.update(request));
        verify(dataService).update(42L, 9.0f, testUser);
    }

    @Test
    @DisplayName("update() should throw UsernameNotFoundException when user not found")
    void update_shouldThrowWhenUserNotFound() {
        UpdateScoreRequestDTO request = new UpdateScoreRequestDTO();
        request.setUsername("unknown");
        request.setService(ThirdPartyService.ANILIST);
        request.setType(ContentType.ANIME);

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> dataController.update(request));
    }

    @Test
    @DisplayName("update() should throw ThirdPartyUnconfiguredException when user has no connection")
    void update_shouldThrowWhenNoConnection() {
        User userWithNoConnections = new User();
        userWithNoConnections.setUsername("testuser");
        userWithNoConnections.setConnections(new HashMap<>());

        UpdateScoreRequestDTO request = new UpdateScoreRequestDTO();
        request.setUsername("testuser");
        request.setService(ThirdPartyService.TRAKT);
        request.setType(ContentType.MOVIES);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userWithNoConnections));

        assertThrows(ThirdPartyUnconfiguredException.class, () -> dataController.update(request));
    }

    @Test
    @DisplayName("pull() should throw ThirdPartyUnconfiguredException when user has no connection")
    void pull_shouldThrowWhenNoConnection() {
        User userWithNoConnections = new User();
        userWithNoConnections.setUsername("testuser");
        userWithNoConnections.setConnections(new HashMap<>());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userWithNoConnections));

        assertThrows(ThirdPartyUnconfiguredException.class,
                () -> dataController.pull("testuser", ThirdPartyService.TRAKT, ContentType.MOVIES));
    }

    @Test
    @DisplayName("push() should do nothing")
    void push_shouldDoNothing() {
        assertDoesNotThrow(() -> dataController.push("testuser", ThirdPartyService.ANILIST, ContentType.ANIME));
        verifyNoInteractions(userRepository);
    }
}
