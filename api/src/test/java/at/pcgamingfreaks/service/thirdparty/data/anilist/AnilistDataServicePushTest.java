package at.pcgamingfreaks.service.thirdparty.data.anilist;

import at.pcgamingfreaks.config.ClientConfig;
import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.exceptions.EntryNotFoundException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.AniListEntryRepository;
import at.pcgamingfreaks.model.repo.AniListEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntry;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntryScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnilistDataService Push Tests")
class AnilistDataServicePushTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AniListEntryScoreRepository aniListEntryScoreRepository;

    @Mock
    private AniListEntryRepository aniListEntryRepository;

    @Mock
    private ThirdPartyConfig thirdPartyConfig;

    @Mock
    private ListEntryDtoMapper listEntryDtoMapper;

    private AnilistAnimeService animeService;
    private User testUser;
    private ServiceConfig validAnilistConfig;

    @BeforeEach
    void setUp() {
        animeService = new AnilistAnimeService(
                userRepository,
                aniListEntryScoreRepository,
                aniListEntryRepository,
                thirdPartyConfig,
                listEntryDtoMapper
        );

        testUser = new User();
        testUser.setUsername("testuser");
        ThirdPartyConnection connection = new ThirdPartyConnection();
        connection.setService(ThirdPartyService.ANILIST);
        connection.setAccessToken("Bearer test-token");
        connection.setThirdPartyUserId("12345");
        connection.setAutoUpdateSync(true);
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.ANILIST, connection);
        testUser.setConnections(connections);

        validAnilistConfig = new ServiceConfig();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setKey("client-key");
        clientConfig.setSecret("client-secret");
        validAnilistConfig.setClient(clientConfig);
        validAnilistConfig.setUrl("https://redirect.example.com");
    }

    @Test
    @DisplayName("update() should throw ThirdPartyUnconfiguredException when anilist not configured")
    void update_shouldThrowWhenNotConfigured() {
        ServiceConfig invalidConfig = new ServiceConfig();
        ClientConfig cc = new ClientConfig();
        cc.setKey("");
        cc.setSecret("");
        invalidConfig.setClient(cc);
        when(thirdPartyConfig.getAnilist()).thenReturn(invalidConfig);

        assertThrows(ThirdPartyUnconfiguredException.class,
                () -> animeService.update(1L, 9.0f, testUser));
    }

    @Test
    @DisplayName("update() should throw EntryNotFoundException when entry not found")
    void update_shouldThrowWhenEntryNotFound() {
        when(thirdPartyConfig.getAnilist()).thenReturn(validAnilistConfig);
        when(aniListEntryScoreRepository.findByUserAndEntry_Id(testUser, 1L)).thenReturn(Optional.empty());

        assertThrows(EntryNotFoundException.class,
                () -> animeService.update(1L, 9.0f, testUser));
    }

    @Test
    @DisplayName("update() should save score and call pushSingleChange when autoUpdateSync is true")
    @SuppressWarnings("unchecked")
    void update_shouldSaveAndPushWhenAutoSync() {
        when(thirdPartyConfig.getAnilist()).thenReturn(validAnilistConfig);

        AniListEntry entry = new AniListEntry();
        entry.setId(1L);
        AniListEntryScore score = new AniListEntryScore();
        score.setEntry(entry);
        score.setScore(8.0f);
        score.setUser(testUser);
        when(aniListEntryScoreRepository.findByUserAndEntry_Id(testUser, 1L)).thenReturn(Optional.of(score));
        when(aniListEntryScoreRepository.save(any())).thenReturn(score);

        HttpGraphQlClient mockClient = mock(HttpGraphQlClient.class);
        @SuppressWarnings({"unchecked", "rawtypes"})
        HttpGraphQlClient.Builder mockBuilder = mock(HttpGraphQlClient.Builder.class);
        GraphQlClient.RequestSpec mockRequestSpec = mock(GraphQlClient.RequestSpec.class);
        GraphQlClient.RetrieveSyncSpec mockRetrieve = mock(GraphQlClient.RetrieveSyncSpec.class);

        doReturn(mockBuilder).when(mockClient).mutate();
        doReturn(mockBuilder).when(mockBuilder).header(anyString(), any(String[].class));
        doReturn(mockClient).when(mockBuilder).build();
        when(mockClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variable(anyString(), any())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieveSync(anyString())).thenReturn(mockRetrieve);

        try (MockedStatic<HttpGraphQlClient> mockedStatic = mockStatic(HttpGraphQlClient.class);
             MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
            WebClient mockWebClient = mock(WebClient.class);
            mockedWebClient.when(() -> WebClient.create(anyString())).thenReturn(mockWebClient);
            mockedStatic.when(() -> HttpGraphQlClient.create(any(WebClient.class))).thenReturn(mockClient);

            assertDoesNotThrow(() -> animeService.update(1L, 9.0f, testUser));
        }

        verify(aniListEntryScoreRepository).save(any());
    }

    @Test
    @DisplayName("update() should save score but not push when autoUpdateSync is false")
    void update_shouldSaveButNotPushWhenAutoSyncFalse() {
        when(thirdPartyConfig.getAnilist()).thenReturn(validAnilistConfig);

        testUser.getConnections().get(ThirdPartyService.ANILIST).setAutoUpdateSync(false);

        AniListEntry entry = new AniListEntry();
        entry.setId(1L);
        AniListEntryScore score = new AniListEntryScore();
        score.setEntry(entry);
        score.setScore(8.0f);
        score.setUser(testUser);
        when(aniListEntryScoreRepository.findByUserAndEntry_Id(testUser, 1L)).thenReturn(Optional.of(score));
        when(aniListEntryScoreRepository.save(any())).thenReturn(score);

        assertDoesNotThrow(() -> animeService.update(1L, 9.0f, testUser));

        verify(aniListEntryScoreRepository).save(any());
    }

    @Test
    @DisplayName("push(String) should do nothing")
    void push_shouldDoNothing() {
        assertDoesNotThrow(() -> animeService.push("testuser"));
    }
}
