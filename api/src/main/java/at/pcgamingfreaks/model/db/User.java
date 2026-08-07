package at.pcgamingfreaks.model.db;

import at.pcgamingfreaks.model.enums.MediaSource;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
	@SequenceGenerator(name = "users_seq", allocationSize = 50)
	private Long id;

	@NotNull
	@Column(unique = true, nullable = false)
	private String username;

	@NotNull
	@Column(unique = true, nullable = false)
	private String email;

	@NotNull
	@Column(nullable = false)
	private String password;

	@NotNull
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@NotNull
	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	private String bio;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "user")
	@MapKey(name = "source")
	private Map<MediaSource, MediaSourceConnection> connections = new HashMap<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
	private List<Tierlist> tierlists = new ArrayList<>(); // TODO: shouldn't this be a set?

	public boolean hasMediaSourceConnection(MediaSource source) {
		return connections.containsKey(source) && connections.get(source) != null;
	}
}
