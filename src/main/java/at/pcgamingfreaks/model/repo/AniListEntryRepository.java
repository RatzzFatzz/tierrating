package at.pcgamingfreaks.model.repo;

import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntry;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface AniListEntryRepository extends CrudRepository<AniListEntry, Long> {
    List<AniListEntry> findAllByIdIn(Collection<Long> ids);
}
