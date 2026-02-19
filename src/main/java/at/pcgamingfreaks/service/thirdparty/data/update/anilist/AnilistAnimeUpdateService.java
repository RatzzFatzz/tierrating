package at.pcgamingfreaks.service.thirdparty.data.update.anilist;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.repo.AniListEntryScoreRepository;
import org.springframework.stereotype.Service;

@Service
public class AnilistAnimeUpdateService extends AnilistUpdateService {
    public AnilistAnimeUpdateService(AniListEntryScoreRepository aniListEntryScoreRepository, ThirdPartyConfig thirdPartyConfig) {
        super(aniListEntryScoreRepository, thirdPartyConfig);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.ANIME;
    }
}
