package at.pcgamingfreaks.service.thirdparty.data.update.anilist;

import at.pcgamingfreaks.model.ContentType;
import org.springframework.stereotype.Service;

@Service
public class AnilistAnimeUpdateService extends AnilistUpdateService {
    @Override
    public ContentType getContentType() {
        return ContentType.ANIME;
    }
}
