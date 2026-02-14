package at.pcgamingfreaks.model.repo;

import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ThirdPartyConnectionRepository extends JpaRepository<ThirdPartyConnection, UUID> {

}
