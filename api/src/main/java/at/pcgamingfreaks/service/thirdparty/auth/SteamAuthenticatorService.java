package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ThirdPartyOpenIdAuthRequestDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyAuthenticationException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.ThirdPartyConnectionRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.net.URLEncoder.encode;

@Slf4j
@RequiredArgsConstructor
@Service
public class SteamAuthenticatorService implements ThirdPartyOpenIdAuthenticatorService {
	private static final String STEAM_OPENID_URL = "https://steamcommunity.com/openid/login";
	private static final String STEAM_ID_PATTERN = "https://steamcommunity.com/openid/id/(\\d+)";

	private final UserRepository userRepository;
	private final ThirdPartyConnectionRepository thirdPartyConnectionRepository;
	private final ThirdPartyConfig thirdPartyConfig;
	private final RestClient.Builder restClientBuilder = RestClient.builder();

	@Override
	public ThirdPartyService getService() {
		return ThirdPartyService.STEAM;
	}

	@Override
	public void auth(String username, ThirdPartyOpenIdAuthRequestDTO request) {
		if (!thirdPartyConfig.getSteam().isValid()) throw new ThirdPartyUnconfiguredException(getService());

		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		if (user.getConnections().get(getService()) != null)
			throw new ThirdPartyAuthenticationException("Already authenticated");

		if (!"id_res".equals(request.getParams().get("openid.mode"))) {
			throw new IllegalArgumentException("Invalid openid.mode");
		}

		Map<String, String> verificationParams = new HashMap<>(request.getParams());
		verificationParams.put("openid.mode", "check_authentication");

		String verificationBody = verificationParams.entrySet().stream()
				.map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
				.collect(Collectors.joining("&"));
		String response = restClientBuilder.build().post()
				.uri(STEAM_OPENID_URL)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.body(verificationBody)
				.retrieve()
				.body(String.class);

		if (!response.contains("is_valid:true")) {
			throw new SecurityException("Steam OpenID verification failed");
		}

		String steamId = extractSteamId(request.getParams().get("openid.claimed_id"));

		ThirdPartyConnection connection = new ThirdPartyConnection();
		connection.setService(getService());
		connection.setUser(user);
		connection.setThirdPartyUserId(steamId);
		connection.setAutoUpdateSync(false);
		thirdPartyConnectionRepository.save(connection);
	}

	private String extractSteamId(String claimedId) {
		if (claimedId == null) {
			throw new IllegalArgumentException("Missing openid.claimed_id");
		}

		Pattern pattern = Pattern.compile(STEAM_ID_PATTERN);
		Matcher matcher = pattern.matcher(claimedId);

		if (!matcher.find()) {
			throw new IllegalArgumentException("Could not extract SteamID from: " + claimedId);
		}

		return matcher.group(1);
	}
}
