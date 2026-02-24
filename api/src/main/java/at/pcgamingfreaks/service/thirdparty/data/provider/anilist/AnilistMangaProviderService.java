package at.pcgamingfreaks.service.thirdparty.data.provider.anilist;


import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.repo.AniListEntryRepository;
import at.pcgamingfreaks.model.repo.AniListEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AnilistMangaProviderService extends AnilistDataProviderService{

    public AnilistMangaProviderService(UserRepository userRepository, AniListEntryScoreRepository aniListEntryScoreRepository, AniListEntryRepository aniListEntryRepository, ListEntryDtoMapper listEntryDtoMapper) {
        super(userRepository, aniListEntryScoreRepository, aniListEntryRepository, listEntryDtoMapper);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.MANGA;
    }
}
