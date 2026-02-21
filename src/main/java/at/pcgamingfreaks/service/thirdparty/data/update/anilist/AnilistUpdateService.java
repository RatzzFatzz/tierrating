package at.pcgamingfreaks.service.thirdparty.data.update.anilist;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.exceptions.ThirdPartySyncException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.AniListEntryScoreRepository;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntryScore;
import at.pcgamingfreaks.service.thirdparty.data.update.DataUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import static at.pcgamingfreaks.config.GlobalProperties.ANILIST_API_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class AnilistUpdateService implements DataUpdateService {
    private final AniListEntryScoreRepository aniListEntryScoreRepository;
    private final ThirdPartyConfig thirdPartyConfig;
    private final String ANILIST_UPDATE_QUERY = """
            mutation ($listEntryId: Int, $mediaId: Int, $score: Float) {
              SaveMediaListEntry(id: $listEntryId, mediaId: $mediaId, score: $score) {
                id
                mediaId
                score
              }
            }
            """;

    @Override
    public ThirdPartyService getService() {
        return ThirdPartyService.ANILIST;
    }

    @Override
    public void updateData(long id, float score, User user) {
        if (!thirdPartyConfig.getAnilist().isValid())  throw new ThirdPartyUnconfiguredException(ThirdPartyService.ANILIST);

        AniListEntryScore entryScore = aniListEntryScoreRepository.findByUserAndEntry_Id(user, id).orElseThrow(() -> new ThirdPartySyncException("Anilist entry not found"));
        entryScore.setScore(score);
        aniListEntryScoreRepository.save(entryScore);

        if (user.getConnections().get(ThirdPartyService.ANILIST).isAutoUpdateSync()) syncData(id, score, user);
    }

    protected void syncData(long id, float score, User user) {
        HttpGraphQlClient.create(WebClient.create(ANILIST_API_URL))
                .mutate()
                .header("Authorization", user.getConnections().get(ThirdPartyService.ANILIST).getAccessToken())
                .build()
                .document(ANILIST_UPDATE_QUERY)
                .variable("mediaId", id)
                .variable("score", score)
                .retrieveSync("UpdateMediaListEntries");
    }
}
