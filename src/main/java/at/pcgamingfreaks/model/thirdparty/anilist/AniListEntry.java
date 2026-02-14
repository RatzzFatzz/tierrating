package at.pcgamingfreaks.model.thirdparty.anilist;

import at.pcgamingfreaks.model.ContentType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "anilist_entry")
public class AniListEntry {
    @Id
    private long id;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    private String title;
    private String titleRomaji;

    private String cover;
}
