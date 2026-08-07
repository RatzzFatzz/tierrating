package at.pcgamingfreaks.model.db;

import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.enums.MediaState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@Table(name = "media_type_settings", indexes = {
		@Index(name = "idx_media_type_settings_connection_id", columnList = "connection_id")
})
public class MediaTypeSettings {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_type_settings_seq")
	@SequenceGenerator(name = "media_type_settings_seq", allocationSize = 50)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "connection_id")
	private MediaSourceConnection connection;

	@NotNull
	private MediaType type;

	private boolean loginPull = true;
	private boolean autoPush = true;

	private boolean showPublic = false;
	private boolean hidden = false;

	@ElementCollection
	@CollectionTable(
			name = "hidden_states",
			joinColumns = @JoinColumn(name = "media_type_settings_id")
	)
	@Enumerated(EnumType.STRING)
	@Column(name = "hidden_state")
	private List<MediaState> hiddenStates;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;
}
