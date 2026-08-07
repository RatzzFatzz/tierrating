package at.pcgamingfreaks.model.db;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"id", "season"})
})
public class TmdbCoverCache {
	@Id
	private long id;
	@Nullable
	private Long season;
	private String coverUrl;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;
}
