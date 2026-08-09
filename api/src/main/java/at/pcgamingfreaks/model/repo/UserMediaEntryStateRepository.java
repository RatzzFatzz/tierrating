package at.pcgamingfreaks.model.repo;

import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.db.media.UserMediaEntryState;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserMediaEntryStateRepository extends JpaRepository<UserMediaEntryState, Long> {
	List<UserMediaEntryState> findAllByUserAndSource(User user, MediaSource source);
	List<UserMediaEntryState> findAllByUserAndSourceAndDirty(User user, MediaSource source, boolean dirty);
	List<UserMediaEntryState> findAllByUserAndSourceAndStateIn(User user, MediaSource source, Collection<MediaState> state);
	Optional<UserMediaEntryState> findByUserAndSourceAndEntryId(User user, MediaSource source, Long entryId);
}
