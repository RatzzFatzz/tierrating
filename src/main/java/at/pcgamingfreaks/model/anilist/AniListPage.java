package at.pcgamingfreaks.model.anilist;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AniListPage {
    private Page pageInfo;
    private List<AniListListEntry> mediaList;
}
