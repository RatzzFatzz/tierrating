package at.pcgamingfreaks.service.thirdparty.data.provider.anilist;

import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.repo.AniListEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AnilistAnimeProviderService extends AnilistDataProviderService{

    public AnilistAnimeProviderService(UserRepository userRepository, AniListEntryScoreRepository aniListEntryScoreRepository, ListEntryDtoMapper listEntryDtoMapper) {
        super(userRepository, aniListEntryScoreRepository, listEntryDtoMapper);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.ANIME;
    }
}