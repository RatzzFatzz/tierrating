package at.pcgamingfreaks.model.db;

import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.enums.SyncStatus;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.SyncType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sync_jobs", indexes = {
		@Index(name = "idx_syncjob_user_id", columnList = "user_id")
})
@NoArgsConstructor
public class SyncJob {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sync_jobs_seq")
	@SequenceGenerator(name = "sync_jobs_seq", allocationSize = 50)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MediaSource mediaSource;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MediaType mediaType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SyncStatus status;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SyncType type;

	@Nullable
	private String error;

	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime startedAt;

	private LocalDateTime completedAt;
}
