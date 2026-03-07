package at.pcgamingfreaks.model.repo;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.TierList;
import at.pcgamingfreaks.model.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TierListsRepository extends JpaRepository<TierList, UUID> {

	Optional<TierList> findByUserAndServiceAndType(User user, ThirdPartyService service, ContentType type);
}
