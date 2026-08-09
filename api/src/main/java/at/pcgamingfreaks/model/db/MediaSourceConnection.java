package at.pcgamingfreaks.model.db;

import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "media_source_connections", indexes = {
		@Index(name = "idx_media_source_connection_user_id", columnList = "user_id")
}, uniqueConstraints = {
		@UniqueConstraint(columnNames = {"third_party_user_id", "source"})
})
public class MediaSourceConnection {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_source_connections_seq")
	@SequenceGenerator(name = "media_source_connections_seq", allocationSize = 50)
	private Long id;

	@NotNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private User user;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MediaSource source;

	@NotBlank
	@Column(nullable = false)
	private String thirdPartyUserId;

	@Column(length = 2047)
	private String accessToken;

	@Column(length = 2047)
	private String refreshToken;

	private LocalDateTime expiresOn;

	@OneToMany(mappedBy = "connection", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "type")
	Map<MediaType, MediaTypeSettings> mediaTypeSettings = new HashMap<>();
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public void putMediaTypeSettings(MediaTypeSettings settings) {
		if (settings.getType() == null) throw new IllegalStateException("Type is required for MediaTypeSettings");
		settings.setConnection(this);
		this.mediaTypeSettings.put(settings.getType(), settings);
	}
}
