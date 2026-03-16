package at.pcgamingfreaks.model.repo;

import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.thirdparty.steam.SteamEntryScore;
import org.springframework.data.repository.CrudRepository;

import java.util.*;

public interface SteamEntryScoreRepository extends CrudRepository<SteamEntryScore, UUID> {

	Set<SteamEntryScore> findAllByUserOrderByScoreDesc(User user);

	Optional<SteamEntryScore> findByUserAndEntry_Id(User user, long id);

	List<SteamEntryScore> findAllByUserAndEntryIdIn(User user, Collection<Long> ids);
}
