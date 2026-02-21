package at.pcgamingfreaks.service.thirdparty.data.update.trakt;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.TraktEntryScoreRepository;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.*;
import com.uwetrottmann.trakt5.enums.Rating;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraktMoviesUpdateService extends TraktUpdateService {
    private final TraktEntryScoreRepository entryScoreRepository;
    private final ThirdPartyConfig thirdPartyConfig;

    @Override
    public ContentType getContentType() {
        return ContentType.MOVIES;
    }

    @Override
    public void updateData(long id, float score, User user) {
        if (!thirdPartyConfig.getTrakt().isValid())  throw new ThirdPartyUnconfiguredException(ThirdPartyService.TRAKT);

        TraktEntryScore entryScore = entryScoreRepository.findByUserAndEntry_Id(user, id).orElseThrow(() -> new RuntimeException("Trakt entry not found"));
        entryScore.setScore((int) score);
        entryScoreRepository.save(entryScore);

        if (user.getConnections().get(ThirdPartyService.TRAKT).isAutoUpdateSync()) syncData(id, score, user);
    }

    protected void syncData(long id, float score, User user) {
        try {
            new TraktV2(thirdPartyConfig.getTrakt().getClient().getKey(), thirdPartyConfig.getTrakt().getClient().getSecret(), thirdPartyConfig.getTrakt().getRedirectUrl())
                    .accessToken(user.getConnections().get(ThirdPartyService.TRAKT).getAccessToken())
                    .sync()
                    .addRatings(new SyncItems().movies(new SyncMovie()
                            .id(MovieIds.trakt((int) id))
                            .rating(Rating.fromValue((int) score))))
                    .execute();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
