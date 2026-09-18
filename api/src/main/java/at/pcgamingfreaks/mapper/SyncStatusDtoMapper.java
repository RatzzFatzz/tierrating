package at.pcgamingfreaks.mapper;

import at.pcgamingfreaks.model.db.SyncJob;
import at.pcgamingfreaks.model.dto.sync.SyncStatusDTO;
import org.springframework.stereotype.Component;

@Component
public class SyncStatusDtoMapper {
	public SyncStatusDTO map(SyncJob job) {
		SyncStatusDTO dto = new SyncStatusDTO();
		dto.setType(job.getType());
		dto.setStatus(job.getStatus());
		dto.setStartedAt(job.getStartedAt());
		dto.setCompletedAt(job.getCompletedAt());
		return dto;
	}
}
