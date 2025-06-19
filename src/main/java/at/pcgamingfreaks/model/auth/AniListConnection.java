package at.pcgamingfreaks.model.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class AniListConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    private User user;

    private long anilistId;

    private LocalDateTime expiresOn;
    @Column(length = 2047)
    private String accessToken;
    @Column(length = 2047)
    private String refreshToken;

}
