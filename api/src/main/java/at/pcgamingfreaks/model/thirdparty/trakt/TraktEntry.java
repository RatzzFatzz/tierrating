package at.pcgamingfreaks.model.thirdparty.trakt;

import at.pcgamingfreaks.model.ContentType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "trakt_entries")
public class TraktEntry {

    @Id
    private long id;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    @Nullable
    private Integer season;

    private String title;
    private String cover;
}
