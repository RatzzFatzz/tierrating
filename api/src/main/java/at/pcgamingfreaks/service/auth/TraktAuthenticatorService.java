package at.pcgamingfreaks.service.auth;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.db.MediaSourceConnection;
import at.pcgamingfreaks.model.db.MediaTypeSettings;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.dto.ThirdPartyOAuthRequestDTO;
import at.pcgamingfreaks.exceptions.ThirdPartyAuthenticationException;
import at.pcgamingfreaks.exceptions.MediaSourceUnconfiguredException;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.enums.SyncType;
import at.pcgamingfreaks.model.repo.MediaSourceConnectionRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.media.sync.MediaSyncManager;
import com.uwetrottmann.trakt5.TraktV2;
import com.uwetrottmann.trakt5.entities.AccessToken;
import com.uwetrottmann.trakt5.entities.UserSlug;
import com.uwetrottmann.trakt5.enums.Extended;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TraktAuthenticatorService implements ThirdPartyOAuthAuthenticatorService {
	private final UserRepository userRepository;
	private final MediaSourceConnectionRepository mediaSourceConnectionRepository;
	private final ThirdPartyConfig thirdPartyConfig;
	private final MediaSyncManager mediaSyncManager;

	@Override
	public MediaSource getMediaSource() {
		return MediaSource.TRAKT;
	}

	@Override
	public void auth(String username, ThirdPartyOAuthRequestDTO request) {
		if (!thirdPartyConfig.getTrakt().isValid()) throw new MediaSourceUnconfiguredException(getMediaSource());

		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		if (user.getConnections().get(getMediaSource()) != null)
			throw new ThirdPartyAuthenticationException("Already authenticated");

		try {
			TraktV2 trakt = new TraktV2(thirdPartyConfig.getTrakt().getKey(), thirdPartyConfig.getTrakt().getSecret(), thirdPartyConfig.getTrakt().getRedirectUrl());
			Response<AccessToken> response = trakt.exchangeCodeForAccessToken(request.getCode());
			if (!response.isSuccessful())
				throw new ThirdPartyAuthenticationException("Trakt OAuth responded with empty body");

			Response<com.uwetrottmann.trakt5.entities.User> traktUserInfo = trakt.accessToken(response.body().access_token).users().profile(UserSlug.ME, Extended.METADATA).execute();
			if (!traktUserInfo.isSuccessful())
				throw new ThirdPartyAuthenticationException("Trakt OAuth responded with empty username");

			MediaSourceConnection connection = new MediaSourceConnection();
			connection.setSource(getMediaSource());
			connection.setAccessToken(response.body().access_token);
			connection.setRefreshToken(response.body().refresh_token);
			connection.setExpiresOn(LocalDateTime.now().plusSeconds(response.body().expires_in));
			connection.setThirdPartyUserId(traktUserInfo.body().ids.slug);
			connection.setUser(user);

			MediaTypeSettings tvShowSettings = new MediaTypeSettings();
			tvShowSettings.setType(MediaType.TV_SHOW);
			connection.putMediaTypeSettings(tvShowSettings);

			MediaTypeSettings tvShowSeasonSettings = new MediaTypeSettings();
			tvShowSeasonSettings.setType(MediaType.TV_SHOW_SEASON);
			connection.putMediaTypeSettings(tvShowSeasonSettings);

			MediaTypeSettings movieSettings = new MediaTypeSettings();
			movieSettings.setType(MediaType.MOVIE);
			connection.putMediaTypeSettings(movieSettings);

			mediaSourceConnectionRepository.save(connection);

			mediaSyncManager.enqueueSync(user.getId(), getMediaSource(), MediaType.TV_SHOW, SyncType.PULL);
			mediaSyncManager.enqueueSync(user.getId(), getMediaSource(), MediaType.TV_SHOW_SEASON, SyncType.PULL);
			mediaSyncManager.enqueueSync(user.getId(), getMediaSource(), MediaType.MOVIE, SyncType.PULL);
		} catch (IOException e) {
			throw new ThirdPartyAuthenticationException(e);
		}
	}
}
