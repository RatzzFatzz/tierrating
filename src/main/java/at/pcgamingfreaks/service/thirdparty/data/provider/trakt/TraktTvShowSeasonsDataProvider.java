package at.pcgamingfreaks.service.thirdparty.data.provider.trakt;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.exceptions.ThirdPartySyncException;
import at.pcgamingfreaks.model.repo.TraktEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntry;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import at.pcgamingfreaks.service.TmdbCoverFinder;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.RatedSeason;
import com.uwetrottmann.trakt5.entities.UserSlug;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.enums.RatingsFilter;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TraktTvShowSeasonsDataProvider extends TraktDataProviderService{

    public TraktTvShowSeasonsDataProvider(UserRepository userRepository, TraktEntryScoreRepository entryScoreRepository, TmdbCoverFinder coverFinder, ThirdPartyConfig thirdPartyConfig, ListEntryDtoMapper listEntryDtoMapper) {
        super(userRepository, entryScoreRepository, coverFinder, thirdPartyConfig, listEntryDtoMapper);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.TVSHOWS_SEASONS;
    }

    @Override
    protected List<TraktEntryScore> fetch(User user) {
        Map<Long, TraktEntryScore> entries = new HashMap<>();
        fetchRated(user).stream()
                .map(ratedSeason -> {
                    TraktEntry entry = new TraktEntry(
                            ratedSeason.season.ids.trakt,
                            ContentType.TVSHOWS_SEASONS,
                            ratedSeason.season.number,
                            String.format("%s Season %d", ratedSeason.show.title, ratedSeason.season.number),
                            coverFinder.findSeason(ratedSeason.show.ids.tmdb, ratedSeason.season.number)
                    );
                    TraktEntryScore entryScore = new TraktEntryScore();
                    entryScore.setUser(user);
                    entryScore.setEntry(entry);
                    entryScore.setScore(ratedSeason.rating.value);
                    return entryScore;
                })
                .forEach(entry -> entries.put(entry.getEntry().getId(), entry));
        return entries.values().stream().toList();
    }

    @Override
    protected List<RatedSeason> fetchRated(User user) {
        try {
            Response<List<RatedSeason>> response = new TraktV2(
                    thirdPartyConfig.getTrakt().getClient().getKey(),
                    thirdPartyConfig.getTrakt().getClient().getSecret(),
                    thirdPartyConfig.getTrakt().getRedirectUrl())
                    .users()
                    .ratingsSeasons(
                            UserSlug.fromUsername(user.getConnections().get(ThirdPartyService.TRAKT).getThirdPartyUserId()),
                            RatingsFilter.ALL,
                            Extended.FULL)
                    .execute();

            if (!response.isSuccessful())
                throw new ThirdPartySyncException("Error retrieving rated seasons of " + user.getUsername());

            return response.body();
        } catch (IOException e) {
            throw new ThirdPartySyncException("Error retrieving rated seasons: " + e.getMessage());
        }
    }

    @Override
    protected List<Object> fetchWatched(User user) {
        return List.of();
    }
}
