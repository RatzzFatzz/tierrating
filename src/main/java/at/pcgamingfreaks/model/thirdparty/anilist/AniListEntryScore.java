package at.pcgamingfreaks.model.thirdparty.anilist;

import at.pcgamingfreaks.model.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "anilist_entry_score")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "entry_id"})
})
public class AniListEntryScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private AniListEntry entry;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private User user;

    private float score;
}
