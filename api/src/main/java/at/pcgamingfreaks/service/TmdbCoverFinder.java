package at.pcgamingfreaks.service;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.dto.TmdbInfoRequest;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.TmdbCoverCacheRepository;
import at.pcgamingfreaks.model.thirdparty.TmdbCoverCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static at.pcgamingfreaks.model.ThirdPartyService.TMDB;

@Slf4j
@Service
@RequiredArgsConstructor
public class TmdbCoverFinder {
	private static final String TMDB_API_URL = "https://api.themoviedb.org/3";
	private static final String TMDB_IMAGE_API_URL = "https://image.tmdb.org/t/p/w185";
	private final TmdbCoverCacheRepository tmdbCoverCacheRepository;
	private final ThirdPartyConfig thirdPartyConfig;

	/**
	 * Find cover image for movies from TMDB
	 *
	 * @param id movie id
	 * @return cover url
	 */
	public String findMovie(long id) {
		return find("/movie/%d", id, null);
	}

	/**
	 * Find cover image for tv shows from TMDB
	 *
	 * @param id tv show id
	 * @return cover url
	 */
	public String findShow(long id) {
		return find("/tv/%d", id, null);
	}

	/**
	 * Find cover image for seasons from TMDB
	 *
	 * @param showId show id
	 * @param season season number
	 * @return url to cover image
	 */
	public String findSeason(long showId, long season) {
		String result = find("/tv/%s/season/" + season, showId, season);

		return Strings.isNotBlank(result)
				? result
				: findShow(showId);
	}

	/**
	 * @param urlExtension api url extension containing %s placeholder for requestId
	 * @param id           content id (e.g. movie id, show id, season id)
	 * @param season       season number
	 * @return cover url
	 */
	private String find(String urlExtension, long id, Long season) {
		if (!thirdPartyConfig.getTmdb().isValid()) throw new ThirdPartyUnconfiguredException(TMDB);
		TmdbCoverCache tmdbCoverCache = tmdbCoverCacheRepository.findByIdAndSeason(id, season != null && season > 0 ? season : null).orElse(null);
		if (tmdbCoverCache != null) return tmdbCoverCache.getCoverUrl();

		try {
			TmdbInfoRequest response = RestClient.builder()
					.baseUrl(TMDB_API_URL)
					.defaultHeader("Authorization", "Bearer " + thirdPartyConfig.getTmdb().getKey())
					.build()
					.get()
					.uri(urlExtension.formatted(id))
					.header("accept", MediaType.APPLICATION_JSON.toString())
					.retrieve()
					.body(TmdbInfoRequest.class);

			if (response.getPosterPath() == null) return null;

			tmdbCoverCache = new TmdbCoverCache();
			tmdbCoverCache.setId(id);
			if (season != null && season > 0) tmdbCoverCache.setSeason(season);
			tmdbCoverCache.setCoverUrl(TMDB_IMAGE_API_URL + response.getPosterPath());
			tmdbCoverCacheRepository.save(tmdbCoverCache);
			return tmdbCoverCache.getCoverUrl();
		} catch (Exception e) {
			log.warn("Couldn't find image for {}: {}", urlExtension.formatted(id), e.getMessage());
			return null;
		}
	}
}
