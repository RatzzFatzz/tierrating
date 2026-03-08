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
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;

		TraktEntry that = (TraktEntry) o;
		return type == that.type && Objects.equals(season, that.season) && Objects.equals(title, that.title) && Objects.equals(cover, that.cover);
	}

	@Override
	public int hashCode() {
		int result = Objects.hashCode(type);
		result = 31 * result + Objects.hashCode(season);
		result = 31 * result + Objects.hashCode(title);
		result = 31 * result + Objects.hashCode(cover);
		return result;
	}
}
