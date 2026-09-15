package at.pcgamingfreaks.service.media.sync;

import at.pcgamingfreaks.exceptions.MediaSourceNotConnectedException;
import at.pcgamingfreaks.exceptions.MediaSyncAlreadyQueued;
import at.pcgamingfreaks.model.db.MediaSourceConnection;
import at.pcgamingfreaks.model.db.SyncJob;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.enums.SyncStatus;
import at.pcgamingfreaks.model.enums.SyncType;
import at.pcgamingfreaks.model.repo.MediaSourceConnectionRepository;
import at.pcgamingfreaks.model.repo.SyncJobRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaSyncManagerTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private MediaSourceConnectionRepository mediaSourceConnectionRepository;

	@Mock
	private SyncJobRepository syncJobRepository;

	@Mock
	private MediaSyncProcessor mediaSyncProcessor;

	@Mock
	private ScheduledExecutorService syncExecutorService;

	@InjectMocks
	private MediaSyncManager mediaSyncManager;

	@Test
	void restartExistingSyncs() {
		SyncJob syncJob1 = new SyncJob();
		syncJob1.setUser(new User());
		SyncJob syncJob2 = new SyncJob();
		syncJob2.setUser(new User());

		when(syncJobRepository.findAllByStatus(List.of(SyncStatus.PENDING, SyncStatus.IN_PROGRESS)))
				.thenReturn(List.of(syncJob1, syncJob2));

		mediaSyncManager.restartExistingSyncs();

		verify(syncExecutorService, times(2)).submit(any(Runnable.class));
	}

	@Test
	void restartExistingSyncs_noSyncs() {
		when(syncJobRepository.findAllByStatus(List.of(SyncStatus.PENDING, SyncStatus.IN_PROGRESS)))
				.thenReturn(List.of());

		mediaSyncManager.restartExistingSyncs();

		verify(syncExecutorService, times(0)).submit(any(Runnable.class));
	}

	@Test
	void enqueueSync_alreadyRunning() {
		SyncJob syncJob = new SyncJob();
		syncJob.setUser(new User());

		when(userRepository.getReferenceById(anyLong())).thenReturn(new User());
		when(syncJobRepository.findActiveSyncByUserAndSourceAndTypeAndStatus(any(), any(), any(), anyList()))
				.thenReturn(Optional.of(syncJob));

		assertDoesNotThrow(() -> mediaSyncManager.enqueueSync(1L, MediaSource.ANILIST, MediaType.ANIME, SyncType.PULL));
	}

	@Test
	void enqueueSync_noConnection() {
		when(userRepository.getReferenceById(anyLong())).thenReturn(new User());
		when(mediaSourceConnectionRepository.findByUserIdAndSource(anyLong(), any(MediaSource.class))).thenReturn(Optional.empty());
		when(syncJobRepository.findActiveSyncByUserAndSourceAndTypeAndStatus(any(), any(), any(), anyList()))
				.thenReturn(Optional.empty());

		assertThrows(MediaSourceNotConnectedException.class, () -> mediaSyncManager.enqueueSync(1L, MediaSource.ANILIST, MediaType.ANIME, SyncType.PULL));
	}

	@Test
	void enqueueSync_successful() {
		when(userRepository.getReferenceById(anyLong())).thenReturn(new User());
		when(mediaSourceConnectionRepository.findByUserIdAndSource(anyLong(), any(MediaSource.class))).thenReturn(Optional.of(new MediaSourceConnection()));
		when(syncJobRepository.findActiveSyncByUserAndSourceAndTypeAndStatus(any(), any(), any(), anyList()))
				.thenReturn(Optional.empty());

		mediaSyncManager.enqueueSync(1L, MediaSource.ANILIST, MediaType.ANIME, SyncType.PULL);

		verify(syncJobRepository, times(1)).saveAndFlush(any(SyncJob.class));
		verify(syncExecutorService, times(1)).schedule(any(MediaSyncJob.class), eq(0L), eq(TimeUnit.SECONDS));
	}
}