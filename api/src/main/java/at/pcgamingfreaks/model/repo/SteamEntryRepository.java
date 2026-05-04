package at.pcgamingfreaks.model.repo;

import at.pcgamingfreaks.model.thirdparty.steam.SteamEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface SteamEntryRepository extends CrudRepository<SteamEntry, Long> {
	List<SteamEntry> findAllByIdIn(Collection<Long> ids);
}
