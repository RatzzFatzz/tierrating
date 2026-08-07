package at.pcgamingfreaks.model.db.media;

import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_media_entry_states", indexes = {
		@Index(name = "idx_user_media_entry_state_user_id", columnList = "user_id")
}, uniqueConstraints = {
		@UniqueConstraint(columnNames = {"user_id", "source", "entry_id"})
})
public class UserMediaEntryState {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_media_entry_states_seq")
	@SequenceGenerator(name = "user_media_entry_states_seq", allocationSize = 50)
	private Long id;

	@NotNull
	@ManyToOne(optional = false)
	private User user;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MediaSource source;

	@NotNull
	@Column(nullable = false)
	private Long entryId;

	private float score;

	@Enumerated(EnumType.STRING)
	private MediaState state;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;
}
