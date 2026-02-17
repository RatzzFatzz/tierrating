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
import com.uwetrottmann.trakt5.entities.BaseMovie;
import com.uwetrottmann.trakt5.entities.RatedMovie;
import com.uwetrottmann.trakt5.entities.UserSlug;
import com.uwetrottmann.trakt5.enums.Extended;
import com.uwetrottmann.trakt5.enums.RatingsFilter;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TraktMovieDataProvider extends TraktDataProviderService{

    public TraktMovieDataProvider(UserRepository userRepository, TraktEntryScoreRepository entryScoreRepository, TmdbCoverFinder coverFinder, ThirdPartyConfig thirdPartyConfig, ListEntryDtoMapper listEntryDtoMapper) {
        super(userRepository, entryScoreRepository, coverFinder, thirdPartyConfig, listEntryDtoMapper);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.MOVIES;
    }

    @Override
    protected List<TraktEntryScore> fetch(User user) {
        Map<Long, TraktEntryScore> entries = new HashMap<>();
        fetchRated(user).stream()
                .map(ratedMovie -> {
                    TraktEntry entry = new TraktEntry(
                            ratedMovie.movie.ids.trakt,
                            ContentType.MOVIES, null,
                            ratedMovie.movie.title,
                            coverFinder.findMovie(ratedMovie.movie.ids.tmdb)
                    );
                    TraktEntryScore entryScore = new TraktEntryScore();
                    entryScore.setUser(user);
                    entryScore.setEntry(entry);
                    entryScore.setScore(ratedMovie.rating.value);
                    return entryScore;
                })
                .forEach(entry -> entries.put(entry.getEntry().getId(), entry));
        fetchWatched(user).stream()
                .map(baseMovie -> {
                    TraktEntry entry = new TraktEntry(
                            baseMovie.movie.ids.trakt,
                            ContentType.MOVIES, null,
                            baseMovie.movie.title,
                            coverFinder.findMovie(baseMovie.movie.ids.tmdb)
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
    protected List<RatedMovie> fetchRated(User user) {
        try {
            Response<List<RatedMovie>> response = new TraktV2(
                    thirdPartyConfig.getTrakt().getClient().getKey(),
                    thirdPartyConfig.getTrakt().getClient().getSecret(),
                    thirdPartyConfig.getTrakt().getRedirectUrl())
                    .users()
                    .ratingsMovies(
                            UserSlug.fromUsername(user.getConnections().get(ThirdPartyService.TRAKT).getThirdPartyUserId()),
                            RatingsFilter.ALL,
                            Extended.FULL)
                    .execute();

            if (!response.isSuccessful())
                throw new ThirdPartySyncException("Error occurred fetching rated movies for " + user.getUsername());

            return response.body();
        } catch (IOException e) {
            throw new ThirdPartySyncException("Error occurred fetching rated movies: " + e.getMessage());
        }
    }

    @Override
    protected List<BaseMovie> fetchWatched(User user) {
        try {
            Response<List<BaseMovie>> response = new TraktV2(
                    thirdPartyConfig.getTrakt().getClient().getKey(),
                    thirdPartyConfig.getTrakt().getClient().getSecret(),
                    thirdPartyConfig.getTrakt().getRedirectUrl())
                    .users()
                    .watchedMovies(
                            UserSlug.fromUsername(user.getConnections().get(ThirdPartyService.TRAKT).getThirdPartyUserId()),
                            Extended.FULL)
                    .execute();

            if (!response.isSuccessful())
                throw new ThirdPartySyncException("Error occurred fetching watched movies of " + user.getUsername());

            return response.body();
        } catch (IOException e) {
            throw new ThirdPartySyncException("Error occurred fetching watched movies: " + e.getMessage());
        }
    }
}
