package at.pcgamingfreaks.service.thirdparty.data.anilist;

import at.pcgamingfreaks.config.ClientConfig;
import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.repo.AniListEntryRepository;
import at.pcgamingfreaks.model.repo.AniListEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntry;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntryScore;
import at.pcgamingfreaks.model.thirdparty.anilist.external.*;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnilistDataService Pull Tests")
class AnilistDataServicePullTest {

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
        connection.setAccessToken("access-token");
        connection.setThirdPartyUserId("12345");
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.ANILIST, connection);
        testUser.setConnections(connections);
    }

    private AniListPage buildSinglePageResult(long mediaId, String title, String romaji, String cover, float score) {
        AniListMediaTitle titleObj = new AniListMediaTitle();
        titleObj.setEnglish(title);
        titleObj.setRomaji(romaji);

        AniListMediaCoverImage coverImage = new AniListMediaCoverImage();
        coverImage.setExtraLarge(cover);
        coverImage.setLarge(cover);

        AniListMedia media = new AniListMedia();
        media.setId(mediaId);
        media.setTitle(titleObj);
        media.setCoverImage(coverImage);

        AniListListEntry listEntry = new AniListListEntry();
        listEntry.setScore(score);
        listEntry.setMedia(media);

        AniListPageInfo pageInfo = new AniListPageInfo();
        pageInfo.setHasNextPage(false);
        pageInfo.setCurrentPage(1);
        pageInfo.setPerPage(50);

        AniListPage page = new AniListPage();
        page.setPageInfo(pageInfo);
        page.setMediaList(List.of(listEntry));
        return page;
    }

    @Test
    @DisplayName("pull(String) should save scores using mocked HttpGraphQlClient")
    @SuppressWarnings("unchecked")
    void pull_shouldSaveScoresFromGraphQlResponse() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        AniListPage page = buildSinglePageResult(42L, "Attack on Titan", "Shingeki no Kyojin", "https://cover.jpg", 9.5f);

        HttpGraphQlClient mockClient = mock(HttpGraphQlClient.class);
        GraphQlClient.RequestSpec mockRequestSpec = mock(GraphQlClient.RequestSpec.class);
        GraphQlClient.RetrieveSyncSpec mockField = mock(GraphQlClient.RetrieveSyncSpec.class);

        when(mockClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variable(anyString(), any())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieveSync(anyString())).thenReturn(mockField);
        when(mockField.toEntity(AniListPage.class)).thenReturn(page);

        when(aniListEntryRepository.findAllByIdIn(any())).thenReturn(List.of());
        when(aniListEntryScoreRepository.findAllByUserAndEntryIdIn(any(), any())).thenReturn(List.of());
        when(aniListEntryScoreRepository.saveAll(any())).thenReturn(List.of());

        try (MockedStatic<HttpGraphQlClient> mockedStatic = mockStatic(HttpGraphQlClient.class);
             MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
            WebClient mockWebClient = mock(WebClient.class);
            mockedWebClient.when(() -> WebClient.create(anyString())).thenReturn(mockWebClient);
            mockedStatic.when(() -> HttpGraphQlClient.create(any(WebClient.class))).thenReturn(mockClient);

            animeService.pull("testuser");
        }

        verify(aniListEntryScoreRepository).saveAll(any());
    }

    @Test
    @DisplayName("pull(String) should merge with existing entries")
    @SuppressWarnings("unchecked")
    void pull_shouldMergeWithExistingEntries() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        AniListPage page = buildSinglePageResult(42L, "Attack on Titan", "Shingeki", "https://cover.jpg", 9.0f);

        AniListEntry existingEntry = new AniListEntry();
        existingEntry.setId(42L);
        existingEntry.setTitle("Old Title");

        AniListEntryScore existingScore = new AniListEntryScore();
        existingScore.setEntry(existingEntry);
        existingScore.setScore(8.0f);
        existingScore.setUser(testUser);

        HttpGraphQlClient mockClient = mock(HttpGraphQlClient.class);
        GraphQlClient.RequestSpec mockRequestSpec = mock(GraphQlClient.RequestSpec.class);
        GraphQlClient.RetrieveSyncSpec mockField = mock(GraphQlClient.RetrieveSyncSpec.class);

        when(mockClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variable(anyString(), any())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieveSync(anyString())).thenReturn(mockField);
        when(mockField.toEntity(AniListPage.class)).thenReturn(page);

        when(aniListEntryRepository.findAllByIdIn(any())).thenReturn(List.of(existingEntry));
        when(aniListEntryScoreRepository.findAllByUserAndEntryIdIn(any(), any())).thenReturn(List.of(existingScore));
        when(aniListEntryScoreRepository.saveAll(any())).thenReturn(List.of());

        try (MockedStatic<HttpGraphQlClient> mockedStatic = mockStatic(HttpGraphQlClient.class);
             MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
            WebClient mockWebClient = mock(WebClient.class);
            mockedWebClient.when(() -> WebClient.create(anyString())).thenReturn(mockWebClient);
            mockedStatic.when(() -> HttpGraphQlClient.create(any(WebClient.class))).thenReturn(mockClient);

            animeService.pull("testuser");
        }

        assertEquals("Attack on Titan", existingEntry.getTitle());
        assertEquals(9.0f, existingScore.getScore());
    }

    @Test
    @DisplayName("pull(String) should handle extraLarge cover being blank by falling back to large")
    @SuppressWarnings("unchecked")
    void pull_shouldFallbackToLargeCoverWhenExtraLargeBlank() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        AniListMediaTitle titleObj = new AniListMediaTitle();
        titleObj.setEnglish("Naruto");
        titleObj.setRomaji("Naruto");

        AniListMediaCoverImage coverImage = new AniListMediaCoverImage();
        coverImage.setExtraLarge("");
        coverImage.setLarge("https://large-cover.jpg");

        AniListMedia media = new AniListMedia();
        media.setId(99L);
        media.setTitle(titleObj);
        media.setCoverImage(coverImage);

        AniListListEntry listEntry = new AniListListEntry();
        listEntry.setScore(7.0f);
        listEntry.setMedia(media);

        AniListPageInfo pageInfo = new AniListPageInfo();
        pageInfo.setHasNextPage(false);

        AniListPage page = new AniListPage();
        page.setPageInfo(pageInfo);
        page.setMediaList(List.of(listEntry));

        HttpGraphQlClient mockClient = mock(HttpGraphQlClient.class);
        GraphQlClient.RequestSpec mockRequestSpec = mock(GraphQlClient.RequestSpec.class);
        GraphQlClient.RetrieveSyncSpec mockField = mock(GraphQlClient.RetrieveSyncSpec.class);

        when(mockClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variable(anyString(), any())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieveSync(anyString())).thenReturn(mockField);
        when(mockField.toEntity(AniListPage.class)).thenReturn(page);

        when(aniListEntryRepository.findAllByIdIn(any())).thenReturn(List.of());
        when(aniListEntryScoreRepository.findAllByUserAndEntryIdIn(any(), any())).thenReturn(List.of());
        when(aniListEntryScoreRepository.saveAll(any())).thenAnswer(invocation -> {
            List<AniListEntryScore> saved = invocation.getArgument(0);
            assertEquals("https://large-cover.jpg", saved.get(0).getEntry().getCover());
            return saved;
        });

        try (MockedStatic<HttpGraphQlClient> mockedStatic = mockStatic(HttpGraphQlClient.class);
             MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
            WebClient mockWebClient = mock(WebClient.class);
            mockedWebClient.when(() -> WebClient.create(anyString())).thenReturn(mockWebClient);
            mockedStatic.when(() -> HttpGraphQlClient.create(any(WebClient.class))).thenReturn(mockClient);

            animeService.pull("testuser");
        }

        verify(aniListEntryScoreRepository).saveAll(any());
    }

    @Test
    @DisplayName("fetch() should call pull when no scores exist")
    @SuppressWarnings("unchecked")
    void fetch_shouldCallPullWhenNoExistingScores() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(aniListEntryScoreRepository.findAllByUserAndEntry_TypeOrderByScoreDesc(testUser, ContentType.ANIME))
                .thenReturn(java.util.Collections.emptySet());

        AniListPage page = buildSinglePageResult(1L, "Test", "Test", "https://cover.jpg", 8.0f);

        AniListEntry entry = new AniListEntry();
        entry.setId(1L);
        AniListEntryScore score = new AniListEntryScore();
        score.setEntry(entry);
        score.setScore(8.0f);

        HttpGraphQlClient mockClient = mock(HttpGraphQlClient.class);
        GraphQlClient.RequestSpec mockRequestSpec = mock(GraphQlClient.RequestSpec.class);
        GraphQlClient.RetrieveSyncSpec mockField = mock(GraphQlClient.RetrieveSyncSpec.class);

        when(mockClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variable(anyString(), any())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieveSync(anyString())).thenReturn(mockField);
        when(mockField.toEntity(AniListPage.class)).thenReturn(page);

        when(aniListEntryRepository.findAllByIdIn(any())).thenReturn(List.of());
        when(aniListEntryScoreRepository.findAllByUserAndEntryIdIn(any(), any())).thenReturn(List.of());
        when(aniListEntryScoreRepository.saveAll(any())).thenReturn(List.of());

        try (MockedStatic<HttpGraphQlClient> mockedStatic = mockStatic(HttpGraphQlClient.class);
             MockedStatic<WebClient> mockedWebClient = mockStatic(WebClient.class)) {
            WebClient mockWebClient = mock(WebClient.class);
            mockedWebClient.when(() -> WebClient.create(anyString())).thenReturn(mockWebClient);
            mockedStatic.when(() -> HttpGraphQlClient.create(any(WebClient.class))).thenReturn(mockClient);

            animeService.fetch("testuser");
        }

        verify(aniListEntryScoreRepository, atLeastOnce())
                .findAllByUserAndEntry_TypeOrderByScoreDesc(testUser, ContentType.ANIME);
    }
}
