package at.pcgamingfreaks.model.repo;

import at.pcgamingfreaks.model.db.SyncJob;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.enums.SyncStatus;
import at.pcgamingfreaks.model.enums.SyncType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SyncJobRepository extends JpaRepository<SyncJob, Long> {

	@Query("select job from SyncJob job where job.user = ?1 and job.mediaSource = ?2 and job.mediaType = ?3 and job.status in ?4")
	Optional<SyncJob> findActiveSyncByUserAndSourceAndTypeAndStatus(User user, MediaSource source, MediaType type, List<SyncStatus> statuses);

	Optional<SyncJob> findFirstByUserAndMediaSourceAndMediaTypeAndTypeAndStatusIn(User user, MediaSource source, MediaType mediaType, SyncType type, List<SyncStatus> status);

	@Query("select job from SyncJob job where job.status in ?1")
	List<SyncJob> findAllByStatus(List<SyncStatus> status);

	@Transactional
	@Modifying
	@Query("UPDATE SyncJob j SET j.status = :status WHERE j.id = :id")
	void updateStatus(@Param("id") Long id, @Param("status") SyncStatus status);

	@Transactional
	@Modifying
	@Query("UPDATE SyncJob j SET j.status = :status, j.completedAt = :completedAt WHERE j.id = :id")
	void completeJob(@Param("id") Long id, @Param("status") SyncStatus status, @Param("completedAt") LocalDateTime completedAt);
}

