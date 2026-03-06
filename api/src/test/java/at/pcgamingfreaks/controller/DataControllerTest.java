package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
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
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.mockito.Mockito.lenient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataController Tests")
class DataControllerTest {

    @Mock
    private DataFactory dataFactory;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DataService dataService;

    @InjectMocks
    private DataController dataController;

    private User testUser;
    private static final String TEST_USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername(TEST_USERNAME);
        Map<ThirdPartyService, ThirdPartyConnection> connectionsForPullTests = new HashMap<>();
        connectionsForPullTests.put(ThirdPartyService.ANILIST, mock(ThirdPartyConnection.class));
        testUser.setConnections(connectionsForPullTests);
    }

    @Test
    @DisplayName("fetch() should return sorted data for valid request")
    void fetch_shouldReturnSortedDataForValidRequest() {
        ListEntryDTO entry1 = mock(ListEntryDTO.class);
        when(entry1.getScore()).thenReturn(100f);
        
        ListEntryDTO entry2 = mock(ListEntryDTO.class);
        when(entry2.getScore()).thenReturn(50f);
        
        when(dataFactory.getProvider(ThirdPartyService.ANILIST, ContentType.ANIME))
                .thenReturn(dataService);
        when(dataService.fetch(TEST_USERNAME)).thenReturn(List.of(entry1, entry2));

        ResponseEntity<List<ListEntryDTO>> response = dataController.fetch(
                TEST_USERNAME,
                ThirdPartyService.ANILIST,
                ContentType.ANIME
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(100, response.getBody().get(0).getScore());
        assertEquals(50, response.getBody().get(1).getScore());
    }

    @Test
    @DisplayName("fetch() should return 404 when provider not found")
    void fetch_shouldReturn404WhenProviderNotFound() {
        when(dataFactory.getProvider(any(ThirdPartyService.class), any(ContentType.class)))
                .thenReturn(null);

        ResponseEntity<List<ListEntryDTO>> response = dataController.fetch(
                TEST_USERNAME,
                ThirdPartyService.ANILIST,
                ContentType.ANIME
        );

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("fetch() should return empty list when no data available")
    void fetch_shouldReturnEmptyListWhenNoData() {
        when(dataFactory.getProvider(ThirdPartyService.ANILIST, ContentType.ANIME))
                .thenReturn(dataService);
        when(dataService.fetch(TEST_USERNAME)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ListEntryDTO>> response = dataController.fetch(
                TEST_USERNAME,
                ThirdPartyService.ANILIST,
                ContentType.ANIME
        );

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("pull() should pull data for valid user and service")
    void pull_shouldPullDataForValidUser() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(dataFactory.getProvider(ThirdPartyService.ANILIST, ContentType.ANIME))
                .thenReturn(dataService);
        doNothing().when(dataService).pull(TEST_USERNAME);

        assertDoesNotThrow(() -> dataController.pull(
                TEST_USERNAME,
                ThirdPartyService.ANILIST,
                ContentType.ANIME
        ));

        verify(dataService).pull(TEST_USERNAME);
    }

    @Test
    @DisplayName("pull() should throw exception for non-existent user")
    void pull_shouldThrowExceptionForNonExistentUser() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> dataController.pull(
                TEST_USERNAME,
                ThirdPartyService.ANILIST,
                ContentType.ANIME
        ));
    }
}
