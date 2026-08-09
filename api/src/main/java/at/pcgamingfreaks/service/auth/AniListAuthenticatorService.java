package at.pcgamingfreaks.service.auth;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.db.MediaSourceConnection;
import at.pcgamingfreaks.model.db.MediaTypeSettings;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.dto.AuthTokenResponseDTO;
import at.pcgamingfreaks.model.dto.ThirdPartyOAuthRequestDTO;
import at.pcgamingfreaks.exceptions.ThirdPartyAuthenticationException;
import at.pcgamingfreaks.exceptions.MediaSourceUnconfiguredException;
import at.pcgamingfreaks.model.enums.SyncType;
import at.pcgamingfreaks.model.repo.MediaSourceConnectionRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.util.JwtPayload;
import at.pcgamingfreaks.service.media.sync.MediaSyncManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AniListAuthenticatorService implements ThirdPartyOAuthAuthenticatorService {
	private final UserRepository userRepository;
	private final MediaSourceConnectionRepository mediaSourceConnectionRepository;
	private final ThirdPartyConfig thirdPartyConfig;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final MediaSyncManager mediaSyncManager;

	@Override
	public MediaSource getMediaSource() {
		return MediaSource.ANILIST;
	}

	@Override
	public void auth(String username, ThirdPartyOAuthRequestDTO request) {
		if (!thirdPartyConfig.getAnilist().isValid())
			throw new MediaSourceUnconfiguredException(getMediaSource());

		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		if (user.getConnections().get(getMediaSource()) != null)
			throw new ThirdPartyAuthenticationException("Already authenticated");

		try {

			AuthTokenResponseDTO tokenResponse = auth(request.getCode());

			MediaSourceConnection connection = new MediaSourceConnection();
			connection.setSource(getMediaSource());
			connection.setAccessToken(tokenResponse.getAccessToken());
			connection.setRefreshToken(tokenResponse.getRefreshToken());
			connection.setExpiresOn(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
			connection.setThirdPartyUserId(String.valueOf(extractUserIdFrom(connection.getAccessToken())));
			connection.setUser(user);

			MediaTypeSettings mangaSettings = new MediaTypeSettings();
			mangaSettings.setType(at.pcgamingfreaks.model.enums.MediaType.MANGA);
			connection.putMediaTypeSettings(mangaSettings);

			MediaTypeSettings animeSettings = new MediaTypeSettings();
			animeSettings.setType(at.pcgamingfreaks.model.enums.MediaType.ANIME);
			connection.putMediaTypeSettings(animeSettings);

			mediaSourceConnectionRepository.save(connection);

			mediaSyncManager.enqueueSync(user.getId(), getMediaSource(), at.pcgamingfreaks.model.enums.MediaType.ANIME, SyncType.PULL);
			mediaSyncManager.enqueueSync(user.getId(), getMediaSource(), at.pcgamingfreaks.model.enums.MediaType.MANGA, SyncType.PULL);

		} catch (Exception e) {
			throw new ThirdPartyAuthenticationException(e);
		}
	}

	private AuthTokenResponseDTO auth(String code) {
		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("grant_type", "authorization_code");
		requestBody.put("client_id", thirdPartyConfig.getAnilist().getKey());
		requestBody.put("client_secret", thirdPartyConfig.getAnilist().getSecret());
		requestBody.put("redirect_uri", thirdPartyConfig.getAnilist().getRedirectUrl());
		requestBody.put("code", code);

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<AuthTokenResponseDTO> tokenResponse = restTemplate.exchange(
				"https://anilist.co/api/v2/oauth/token",
				HttpMethod.POST,
				entity,
				AuthTokenResponseDTO.class
		);

		if (!tokenResponse.hasBody() || tokenResponse.getBody() == null)
			throw new ThirdPartyAuthenticationException("AniList OAuth responded with empty body");

		return tokenResponse.getBody();
	}

	private long extractUserIdFrom(String jwt) throws IOException {
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String[] chunks = jwt.split("\\.");
		return objectMapper.readValue(decoder.decode(chunks[1]), JwtPayload.class).getUserId();
	}
}
