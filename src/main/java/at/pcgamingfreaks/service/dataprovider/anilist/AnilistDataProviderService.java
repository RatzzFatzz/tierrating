package at.pcgamingfreaks.service.dataprovider.anilist;

import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.repo.AniListEntryScoreRepository;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntry;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntryScore;
import at.pcgamingfreaks.model.thirdparty.anilist.external.AniListListEntry;
import at.pcgamingfreaks.model.thirdparty.anilist.external.AniListMediaCoverImage;
import at.pcgamingfreaks.model.thirdparty.anilist.external.AniListPage;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.dataprovider.DataProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static at.pcgamingfreaks.config.GlobalProperties.ANILIST_API_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class AnilistDataProviderService implements DataProviderService {
    private final UserRepository userRepository;
    private final AniListEntryScoreRepository aniListEntryScoreRepository;
    private final ListEntryDtoMapper listEntryDtoMapper;

    public ThirdPartyService getService() {
        return ThirdPartyService.ANILIST;
    }

    @Override
    public List<ListEntryDTO> fetchData(String username) {
        long duration = System.currentTimeMillis();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        Set<AniListEntryScore> existingScores = aniListEntryScoreRepository.findAllByUserAndEntry_TypeOrderByScoreDesc(user, getContentType());

        if (existingScores.isEmpty()) {
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

            existingScores = anilistQueryResult.stream()
                    .map(queryResult -> {
                        AniListEntry entry = new AniListEntry();
                        entry.setId(queryResult.getMedia().getId());
                        entry.setTitle(queryResult.getMedia().getTitle().getEnglish());
                        entry.setTitleRomaji(queryResult.getMedia().getTitle().getRomaji());
                        AniListMediaCoverImage cover = queryResult.getMedia().getCoverImage();
                        entry.setCover(Strings.isNotBlank(cover.getLarge()) ? cover.getLarge() : cover.getExtraLarge());
                        entry.setType(getContentType());
                        AniListEntryScore entryScore = new AniListEntryScore();
                        entryScore.setScore(queryResult.getScore());
                        entryScore.setUser(user);
                        entryScore.setEntry(entry);
                        return entryScore;
                    })
                    .collect(Collectors.toSet());
            aniListEntryScoreRepository.saveAll(existingScores);
        }

        log.info("Fetched {} {} for {} in {}s",
                getService(),
                getContentType(),
                username,
                (System.currentTimeMillis() - duration) / 1000);

        return existingScores.stream().map(listEntryDtoMapper::map).toList();
    }
}
