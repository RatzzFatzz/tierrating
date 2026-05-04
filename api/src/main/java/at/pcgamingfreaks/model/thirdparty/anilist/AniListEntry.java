package at.pcgamingfreaks.model.thirdparty.anilist;

import at.pcgamingfreaks.model.ContentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "anilist_entries")
public class AniListEntry implements Persistable<Long> {

	@Id
	private Long id;

	@Enumerated(EnumType.STRING)
	private ContentType type;

	private String title;
	private String titleRomaji;

	private String cover;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Override
	public boolean isNew() {
		return createdAt == null;
	}
}
