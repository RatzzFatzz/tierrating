package at.pcgamingfreaks.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListEntryDTO {
    private long id;
    private String title;
    private String cover;

    private int score;
    private String tier;
}
