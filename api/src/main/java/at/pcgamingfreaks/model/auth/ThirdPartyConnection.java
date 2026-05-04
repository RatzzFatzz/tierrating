package at.pcgamingfreaks.model.auth;

import at.pcgamingfreaks.model.ThirdPartyService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"third_party_user_id", "service"})
})
public class ThirdPartyConnection {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private User user;

	@Enumerated(EnumType.STRING)
	private ThirdPartyService service;
	private String thirdPartyUserId;

	private LocalDateTime expiresOn;
	@Column(length = 2047)
	private String accessToken;
	@Column(length = 2047)
	private String refreshToken;

	private boolean autoUpdateSync = true;
	private boolean autoImportSync = true;
}
