package at.pcgamingfreaks.service.thirdparty.data.provider.trakt;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartySyncException;
import at.pcgamingfreaks.model.repo.TraktEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntry;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import at.pcgamingfreaks.service.TmdbCoverFinder;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.BaseShow;
import com.uwetrottmann.trakt5.entities.RatedShow;
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
public class TraktTvShowDataProvider extends TraktDataProviderService {

    public TraktTvShowDataProvider(UserRepository userRepository, TraktEntryScoreRepository entryScoreRepository, TmdbCoverFinder coverFinder, ThirdPartyConfig thirdPartyConfig, ListEntryDtoMapper listEntryDtoMapper) {
        super(userRepository, entryScoreRepository, coverFinder, thirdPartyConfig, listEntryDtoMapper);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.TVSHOWS;
    }

    @Override
    protected List<TraktEntryScore> fetch(User user) {
        Map<Long, TraktEntryScore> entries = new HashMap<>();
        fetchRated(user).stream()
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
        fetchWatched(user).stream()
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
    protected List<RatedShow> fetchRated(User user) {
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
    protected List<BaseShow> fetchWatched(User user) {
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
}
