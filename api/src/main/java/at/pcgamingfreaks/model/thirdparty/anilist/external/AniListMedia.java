package at.pcgamingfreaks.model.thirdparty.anilist.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AniListMedia {

	private long id;
	private AniListMediaTitle title;
	private AniListMediaCoverImage coverImage;
}
