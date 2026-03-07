package at.pcgamingfreaks.service.thirdparty.data.anilist;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.repo.AniListEntryRepository;
import at.pcgamingfreaks.model.repo.AniListEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AnilistAnimeService extends AnilistDataService {

	public AnilistAnimeService(UserRepository userRepository, AniListEntryScoreRepository aniListEntryScoreRepository, AniListEntryRepository aniListEntryRepository, ThirdPartyConfig thirdPartyConfig, ListEntryDtoMapper listEntryDtoMapper) {
		super(userRepository, aniListEntryScoreRepository, aniListEntryRepository, thirdPartyConfig, listEntryDtoMapper);
	}

	@Override
	public ContentType getContentType() {
		return ContentType.ANIME;
	}
}