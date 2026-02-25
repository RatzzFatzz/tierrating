package at.pcgamingfreaks.service.thirdparty.data.trakt;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.exceptions.ThirdPartySyncException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.TraktEntryRepository;
import at.pcgamingfreaks.model.repo.TraktEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntry;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import at.pcgamingfreaks.service.TmdbCoverFinder;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.*;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.enums.Rating;
import com.uwetrottmann.trakt5.enums.RatingsFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TraktTvShowData extends TraktDataService {

    public TraktTvShowData(UserRepository userRepository, TraktEntryScoreRepository entryScoreRepository, TraktEntryRepository entryRepository, TmdbCoverFinder coverFinder, ThirdPartyConfig thirdPartyConfig, ListEntryDtoMapper listEntryDtoMapper) {
        super(userRepository, entryScoreRepository, entryRepository, coverFinder, thirdPartyConfig, listEntryDtoMapper);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.TVSHOWS;
    }

    @Override
    protected List<TraktEntryScore> pull(User user) {
        Map<Long, TraktEntryScore> entries = new HashMap<>();
        pullRated(user).stream()
                .map(ratedShow -> {
                    TraktEntry entry = new TraktEntry(
                            ratedShow.show.ids.trakt,
                            ContentType.TVSHOWS, null,
                            ratedShow.show.title,
                            coverFinder.findShow(ratedShow.show.ids.tmdb)
                    );
                    TraktEntryScore entryScore = new TraktEntryScore();
                    entryScore.setUser(user);
                    entryScore.setEntry(entry);
                    entryScore.setScore(ratedShow.rating.value);
                    return entryScore;
                })
                .forEach(entry -> entries.put(entry.getEntry().getId(), entry));
        pullWatched(user).stream()
                .map(baseShow -> {
                    TraktEntry entry = new TraktEntry(
                            baseShow.show.ids.trakt,
                            ContentType.TVSHOWS, null,
                            baseShow.show.title,
                            coverFinder.findShow(baseShow.show.ids.tmdb)
                    );
                    TraktEntryScore entryScore = new TraktEntryScore();
                    entryScore.setUser(user);
                    entryScore.setEntry(entry);
                    entryScore.setScore(0);
                    return entryScore;
                })
                .forEach(entry -> entries.computeIfAbsent(entry.getEntry().getId(), key -> entry));
        return entries.values().stream().toList();
    }

    @Override
    protected List<RatedShow> pullRated(User user) {
        try {
            Response<List<RatedShow>> response = new TraktV2(
                        thirdPartyConfig.getTrakt().getClient().getKey(),
                        thirdPartyConfig.getTrakt().getClient().getSecret(),
                        thirdPartyConfig.getTrakt().getRedirectUrl())
                    .users()
                    .ratingsShows(
                            UserSlug.fromUsername(user.getConnections().get(ThirdPartyService.TRAKT).getThirdPartyUserId()),
                            RatingsFilter.ALL,
                            Extended.NOSEASONS)
                    .execute();

            if (!response.isSuccessful())
                throw new ThirdPartySyncException("Error retrieving rated shows of " + user.getUsername());

            return response.body();
        } catch (IOException e) {
            throw new ThirdPartySyncException("Error retrieving rated shows: " + e.getMessage());
        }
    }

    @Override
    protected List<BaseShow> pullWatched(User user) {
        try {
            Response<List<BaseShow>> response = new TraktV2(
                        thirdPartyConfig.getTrakt().getClient().getKey(),
                        thirdPartyConfig.getTrakt().getClient().getSecret(),
                        thirdPartyConfig.getTrakt().getRedirectUrl())
                    .users()
                    .watchedShows(
                            UserSlug.fromUsername(user.getConnections().get(ThirdPartyService.TRAKT).getThirdPartyUserId()),
                            Extended.NOSEASONS)
                    .execute();

            if (!response.isSuccessful())
                throw new ThirdPartySyncException("Error retrieving watched shows of " + user.getUsername());

            return response.body();
        } catch (IOException e) {
            throw new ThirdPartySyncException("Error retrieving watched shows: " + e.getMessage());
        }
    }

    protected void pushSingleChange(long id, float score, User user) {
        try {
            new TraktV2(thirdPartyConfig.getTrakt().getClient().getKey(), thirdPartyConfig.getTrakt().getClient().getSecret(), thirdPartyConfig.getTrakt().getRedirectUrl())
                    .accessToken(user.getConnections().get(ThirdPartyService.TRAKT).getAccessToken())
                    .sync()
                    .addRatings(new SyncItems().shows(new SyncShow()
                            .id(ShowIds.trakt((int) id))
                            .rating(Rating.fromValue((int) score))))
                    .execute();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
