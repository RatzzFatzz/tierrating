package at.pcgamingfreaks.service.thirdparty.data.update.trakt;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.TraktEntryScoreRepository;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraktTvShowsSeasonsUpdateService extends TraktUpdateService{
    private final TraktEntryScoreRepository entryScoreRepository;
    private final ThirdPartyConfig thirdPartyConfig;

    @Override
    public ContentType getContentType() {
        return ContentType.TVSHOWS_SEASONS;
    }

    @Override
    public void updateData(long id, float score, User user) {
        if (!thirdPartyConfig.getTrakt().isValid())  throw new ThirdPartyUnconfiguredException(ThirdPartyService.TRAKT);

        TraktEntryScore entryScore = entryScoreRepository.findByUserAndEntry_Id(user, id).orElseThrow(() -> new RuntimeException("Trakt entry not found"));
        entryScore.setScore((int) score);
        entryScoreRepository.save(entryScore);

        String body = "{\"seasons\":[{\"ids\":{\"trakt\":" + id + "},\"rating\":" + (int) score + "}]}";
        RestClient.builder()
                .baseUrl("https://api.trakt.tv")
                .defaultHeader("Authorization", user.getConnections().get(ThirdPartyService.TRAKT).getAccessToken())
                .build()
                .post()
                .uri("/sync/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("trakt-api-key", thirdPartyConfig.getTrakt().getClient().getKey())
                .body(body)
                .retrieve()
                .body(String.class);
    }
}
