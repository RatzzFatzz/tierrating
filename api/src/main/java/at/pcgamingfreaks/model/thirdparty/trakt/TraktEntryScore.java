package at.pcgamingfreaks.model.thirdparty.trakt;

import at.pcgamingfreaks.model.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "trakt_entry_scores")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "entry_id"})
})
public class TraktEntryScore {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private TraktEntry entry;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private User user;

    private int score;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
