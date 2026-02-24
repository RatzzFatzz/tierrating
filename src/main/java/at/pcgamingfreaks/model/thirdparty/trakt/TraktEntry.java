package at.pcgamingfreaks.model.thirdparty.trakt;

import at.pcgamingfreaks.model.ContentType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "trakt_entries")
public class TraktEntry {

    public TraktEntry(long id, ContentType type, @Nullable Integer season, String title, String cover) {
        this.id = id;
        this.type = type;
        this.season = season;
        this.title = title;
        this.cover = cover;
    }

    @Id
    private long id;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    @Nullable
    private Integer season;

    private String title;
    private String cover;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
