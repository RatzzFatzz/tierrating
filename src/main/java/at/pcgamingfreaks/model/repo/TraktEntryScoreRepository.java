package at.pcgamingfreaks.model.repo;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TraktEntryScoreRepository extends CrudRepository<TraktEntryScore, UUID> {

    Set<TraktEntryScore> findAllByUserAndEntry_TypeOrderByScoreDesc(User user, ContentType type);
    Optional<TraktEntryScore> findByUserAndEntry_Id(User user, long id);
}
