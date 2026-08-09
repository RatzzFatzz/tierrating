package at.pcgamingfreaks.service.media.sync;

import at.pcgamingfreaks.model.db.SyncJob;
import at.pcgamingfreaks.model.db.User;
import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.enums.MediaType;
import at.pcgamingfreaks.model.enums.SyncStatus;
import at.pcgamingfreaks.model.enums.SyncType;
import at.pcgamingfreaks.model.repo.SyncJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaSyncJobTest {

	private SyncJob syncJob;

	@Mock
	private MediaSyncProcessor mediaSyncProcessor;

	@Mock
	private SyncJobRepository syncJobRepository;

	@BeforeEach
	void setup() {
		syncJob = new SyncJob();
		syncJob.setId(1L);
		syncJob.setUser(new User());
		syncJob.setMediaSource(MediaSource.ANILIST);
		syncJob.setMediaType(MediaType.ANIME);
		syncJob.setType(SyncType.PULL);
	}

	@Test
	void run_successful() {
		MediaSyncJob underTest = new MediaSyncJob(syncJob, mediaSyncProcessor, syncJobRepository);
		underTest.run();

		verify(mediaSyncProcessor, times(1)).processSync(any(), any(), any(), eq(SyncType.PULL));
		verify(syncJobRepository, times(1)).updateStatus(anyLong(), any(SyncStatus.class));
		verify(syncJobRepository, times(1)).completeJob(anyLong(), eq(SyncStatus.COMPLETED), any(LocalDateTime.class));
		verify(syncJobRepository, times(0)).completeJob(anyLong(), eq(SyncStatus.FAILED), any(LocalDateTime.class));
	}

	@Test
	void run_failed() {
		doThrow(RuntimeException.class).when(mediaSyncProcessor).processSync(any(), any(), any(), eq(SyncType.PULL));

		MediaSyncJob underTest = new MediaSyncJob(syncJob, mediaSyncProcessor, syncJobRepository);
		underTest.run();

		verify(mediaSyncProcessor, times(1)).processSync(any(), any(), any(),eq(SyncType.PULL));
		verify(syncJobRepository, times(1)).updateStatus(anyLong(), any(SyncStatus.class));
		verify(syncJobRepository, times(0)).completeJob(anyLong(), eq(SyncStatus.COMPLETED), any(LocalDateTime.class));
		verify(syncJobRepository, times(1)).completeJob(anyLong(), eq(SyncStatus.FAILED), any(LocalDateTime.class));
	}
}