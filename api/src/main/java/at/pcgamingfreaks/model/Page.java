package at.pcgamingfreaks.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Page {
    private boolean hasNextPage;
    private int currentPage;
    private int perPage;
}
