package at.pcgamingfreaks.model.anilist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AniListMedia {

    private long id;
    private AniListMediaTitle title;
    private AniListMediaCoverImage coverImage;
}
