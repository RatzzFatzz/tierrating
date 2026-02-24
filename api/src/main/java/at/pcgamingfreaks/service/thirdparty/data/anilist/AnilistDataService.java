package at.pcgamingfreaks.service.thirdparty.data.anilist;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.AniListEntryRepository;
import at.pcgamingfreaks.model.repo.AniListEntryScoreRepository;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntry;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntryScore;
import at.pcgamingfreaks.model.thirdparty.anilist.external.AniListListEntry;
import at.pcgamingfreaks.model.thirdparty.anilist.external.AniListMediaCoverImage;
import at.pcgamingfreaks.model.thirdparty.anilist.external.AniListMediaTitle;
import at.pcgamingfreaks.model.thirdparty.anilist.external.AniListPage;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.thirdparty.data.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static at.pcgamingfreaks.config.GlobalProperties.ANILIST_API_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class AnilistDataService implements DataService {
    private final UserRepository userRepository;
    private final AniListEntryScoreRepository aniListEntryScoreRepository;
    private final AniListEntryRepository aniListEntryRepository;
    private final ThirdPartyConfig thirdPartyConfig;
    private final ListEntryDtoMapper listEntryDtoMapper;

    public ThirdPartyService getService() {
        return ThirdPartyService.ANILIST;
    }

    @Override
    public List<ListEntryDTO> fetch(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        Set<AniListEntryScore> existingScores = aniListEntryScoreRepository.findAllByUserAndEntry_TypeOrderByScoreDesc(user, getContentType());

        if (existingScores.isEmpty()) {
            pull(username);
            existingScores = aniListEntryScoreRepository.findAllByUserAndEntry_TypeOrderByScoreDesc(user, getContentType());
        }

        return existingScores.stream().map(listEntryDtoMapper::map).toList();
    }

    @Override
    public void pull(String username) {
        long duration = System.currentTimeMillis();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        List<AniListListEntry> anilistQueryResult = new ArrayList<>();
        AniListPage page;
        int currentPage = 1;

        do {
            String query = """
                query ($userId: Int, $type: MediaType, $status: MediaListStatus, $page: Int, $perPage: Int) {
                    Page(page: $page, perPage: $perPage) {
                        pageInfo {
                            currentPage
                            hasNextPage
                            perPage
                        }
                        mediaList(userId: $userId, type: $type, status: $status) {
                            score(format: POINT_10_DECIMAL)
                            media {
                                id
                                title {
                                    romaji
                                    english
                                    native
                                }
                                coverImage {
                                    large
                                    extraLarge
                                }
                            }
                        }
                    }
                }
                """;

            page = HttpGraphQlClient.create(WebClient.create(ANILIST_API_URL))
                    .document(query)
                    .variable("userId", user.getConnections().get(ThirdPartyService.ANILIST).getThirdPartyUserId())
                    .variable("type", getContentType().name())
                    .variable("status", "COMPLETED")
                    .variable("page", currentPage++)
                    .variable("perPage", 50)
                    .retrieveSync("Page")
                    .toEntity(AniListPage.class);

            anilistQueryResult.addAll(page.getMediaList());
        } while (page.getPageInfo().isHasNextPage());

        // Collect all remote IDs from this sync for batch lookup
        Set<Long> remoteIds = anilistQueryResult.stream()
                .map(q -> q.getMedia().getId())
                .collect(Collectors.toSet());

        // Fetch all existing entries and scores in bulk to avoid N+1 queries
        Map<Long, AniListEntry> existingEntries = aniListEntryRepository
                .findAllByIdIn(remoteIds)
                .stream()
                .collect(Collectors.toMap(AniListEntry::getId, Function.identity()));

        Map<Long, AniListEntryScore> existingScores = aniListEntryScoreRepository
                .findAllByUserAndEntryIdIn(user, remoteIds)
                .stream()
                .collect(Collectors.toMap(s -> s.getEntry().getId(), Function.identity()));

        List<AniListEntryScore> scoresToSave = new ArrayList<>();

        for (AniListListEntry queryResult : anilistQueryResult) {
            long mediaId = queryResult.getMedia().getId();
            AniListMediaTitle title = queryResult.getMedia().getTitle();
            AniListMediaCoverImage cover = queryResult.getMedia().getCoverImage();
            String resolvedCover = Strings.isNotBlank(cover.getExtraLarge())
                    ? cover.getExtraLarge()
                    : cover.getLarge();

            // Update existing entry or create a new one
            AniListEntry entry = existingEntries.getOrDefault(mediaId, new AniListEntry());
            entry.setId(mediaId);
            entry.setTitle(title.getEnglish());
            entry.setTitleRomaji(title.getRomaji());
            entry.setCover(resolvedCover);
            entry.setType(getContentType());

            // Update existing score or create a new one
            AniListEntryScore entryScore = existingScores.getOrDefault(mediaId, new AniListEntryScore());
            entryScore.setScore(queryResult.getScore());
            entryScore.setUser(user);
            entryScore.setEntry(entry);
            scoresToSave.add(entryScore);
        }

        aniListEntryScoreRepository.saveAll(scoresToSave);

        log.info("Synced {} {} for {} in {}s",
                getService(),
                getContentType(),
                username,
                (System.currentTimeMillis() - duration) / 1000);
    }

    @Override
    public void update(long id, float score, User user) {
        if (!thirdPartyConfig.getAnilist().isValid())  throw new ThirdPartyUnconfiguredException(ThirdPartyService.ANILIST);

        AniListEntryScore entryScore = aniListEntryScoreRepository.findByUserAndEntry_Id(user, id).orElseThrow(() -> new RuntimeException("Anilist entry not found"));
        entryScore.setScore(score);
        aniListEntryScoreRepository.save(entryScore);

        String anilistUpdateQuery = """
            mutation ($listEntryId: Int, $mediaId: Int, $score: Float) {
              SaveMediaListEntry(id: $listEntryId, mediaId: $mediaId, score: $score) {
                id
                mediaId
                score
              }
            }
            """;
        HttpGraphQlClient.create(WebClient.create(ANILIST_API_URL))
                .mutate()
                .header("Authorization", user.getConnections().get(ThirdPartyService.ANILIST).getAccessToken())
                .build()
                .document(anilistUpdateQuery)
                .variable("mediaId", id)
                .variable("score", score)
                .retrieveSync("UpdateMediaListEntries");
    }

    @Override
    public void push(String username) {

    }
}
