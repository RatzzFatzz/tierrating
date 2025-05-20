package at.pcgamingfreaks.model.anilist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnilistMedia {

    private long id;
    private AnilistMediaTitle title;
    private AniListMediaCoverImage coverImage;
}
