package at.pcgamingfreaks.model.repo;

import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface TraktEntryRepository extends CrudRepository<TraktEntry, Long> {
	List<TraktEntry> findAllByIdIn(Collection<Long> ids);
}
