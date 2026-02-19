package at.pcgamingfreaks.model.repo;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntryScore;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AniListEntryScoreRepository extends CrudRepository<AniListEntryScore, UUID> {

    Set<AniListEntryScore> findAllByUserAndEntry_TypeOrderByScoreDesc(User user, ContentType type);
    Optional<AniListEntryScore> findByUserAndEntry_Id(User user, long id);
}
