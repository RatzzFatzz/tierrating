package at.pcgamingfreaks.service.thirdparty.data.steam;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.repo.SteamEntryRepository;
import at.pcgamingfreaks.model.repo.SteamEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SteamGameService extends SteamDataService {
	public SteamGameService(ThirdPartyConfig thirdPartyConfig, UserRepository userRepository, SteamEntryRepository steamEntryRepository, SteamEntryScoreRepository steamEntryScoreRepository, RestClient.Builder restClientBuilder) {
		super(thirdPartyConfig, userRepository, steamEntryRepository, steamEntryScoreRepository, restClientBuilder);
	}

	@Override
	public ContentType getContentType() {
		return ContentType.GAMES;
	}
}
