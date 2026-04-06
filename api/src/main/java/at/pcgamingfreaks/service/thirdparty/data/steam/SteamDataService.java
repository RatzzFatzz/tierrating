package at.pcgamingfreaks.service.thirdparty.data.steam;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.model.exceptions.EntryNotFoundException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.SteamEntryRepository;
import at.pcgamingfreaks.model.repo.SteamEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.thirdparty.steam.SteamEntry;
import at.pcgamingfreaks.model.thirdparty.steam.SteamEntryScore;
import at.pcgamingfreaks.model.thirdparty.steam.external.SteamGameInfo;
import at.pcgamingfreaks.model.thirdparty.steam.external.SteamOwnedGamesResponse;
import at.pcgamingfreaks.service.thirdparty.data.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public abstract class SteamDataService implements DataService {
	private static final String GET_OWNED_GAMES_URL = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/?key=%s&steamid=%s&format=json&include_appinfo=true&include_played_free_games=true";
	private static final String COVER_IMAGE = "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/%s/library_600x900.jpg";

	private final ThirdPartyConfig thirdPartyConfig;
	private final UserRepository userRepository;
	private final SteamEntryRepository steamEntryRepository;
	private final SteamEntryScoreRepository steamEntryScoreRepository;

	private final RestClient.Builder restClientBuilder;

	@Override
	public ThirdPartyService getService() {
		return ThirdPartyService.STEAM;
	}

	@Override
	public List<ListEntryDTO> fetch(String username) {
		if (!thirdPartyConfig.getSteam().isValid()) throw new ThirdPartyUnconfiguredException(getService());
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		Set<SteamEntryScore> existingScores = steamEntryScoreRepository.findAllByUserOrderByScoreDesc(user);

		if (existingScores.isEmpty()) {
			pull(username);
			existingScores = steamEntryScoreRepository.findAllByUserOrderByScoreDesc(user);
		}

		return existingScores.stream()
				.map(entry -> {
					ListEntryDTO dto = new ListEntryDTO();
					dto.setId(entry.getEntry().getId());
					dto.setTitle(entry.getEntry().getTitle());
					dto.setCover(entry.getEntry().getCover());
					dto.setScore(entry.getScore());
					return dto;
				})
				.toList();
	}

	@Override
	public void pull(String username) {
		if (!thirdPartyConfig.getSteam().isValid()) throw new ThirdPartyUnconfiguredException(getService());

		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

		String url = GET_OWNED_GAMES_URL.formatted(
				thirdPartyConfig.getSteam().getKey(),
				user.getConnections().get(getService()).getThirdPartyUserId());

		SteamOwnedGamesResponse result = restClientBuilder.build().get().uri(url).retrieve().body(SteamOwnedGamesResponse.class);

		List<Long> appIds = result.getResponse().getGames().stream().map(SteamGameInfo::getAppId).toList();
		Map<Long, SteamEntry> steamEntries = steamEntryRepository.findAllByIdIn(appIds).stream().collect(Collectors.toMap(SteamEntry::getId, Function.identity()));
		Map<Long, SteamEntryScore> steamEntryScores = steamEntryScoreRepository.findAllByUserAndEntryIdIn(user, appIds).stream().collect(Collectors.toMap(s -> s.getEntry().getId(), Function.identity()));

		List<SteamEntry> entriesToSave = new ArrayList<>();
		List<SteamEntryScore> scoresToSave = new ArrayList<>();

		for (SteamGameInfo gameInfo : result.getResponse().getGames()) {
			SteamEntry entry = steamEntries.getOrDefault(gameInfo.getAppId(), new SteamEntry());
			if (!steamEntries.containsKey(gameInfo.getAppId())
					|| !entry.getTitle().equals(gameInfo.getName())
					|| !entry.getCover().equals(COVER_IMAGE.formatted(gameInfo.getAppId()))) {
				entry.setId(gameInfo.getAppId());
				entry.setTitle(gameInfo.getName());
				entry.setCover(COVER_IMAGE.formatted(gameInfo.getAppId()));
				entriesToSave.add(entry);
			}

			if (!steamEntryScores.containsKey(gameInfo.getAppId())) {
				SteamEntryScore entryScore = new SteamEntryScore();
				entryScore.setEntry(entry);
				entryScore.setUser(user);
				scoresToSave.add(entryScore);
			}
		}

		steamEntryRepository.saveAll(entriesToSave);
		steamEntryScoreRepository.saveAll(scoresToSave);
	}

	@Override
	public void update(long id, float score, User user) {
		if (!thirdPartyConfig.getSteam().isValid()) throw new ThirdPartyUnconfiguredException(getService());

		SteamEntryScore entryScore = steamEntryScoreRepository.findByUserAndEntry_Id(user, id).orElseThrow(() -> new EntryNotFoundException(getContentType(), id));
		entryScore.setScore(score);
		steamEntryScoreRepository.save(entryScore);
	}

	@Override
	public void push(String username) {

	}
}
