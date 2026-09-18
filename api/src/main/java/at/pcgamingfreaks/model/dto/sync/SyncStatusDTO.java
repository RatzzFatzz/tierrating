package at.pcgamingfreaks.model.dto.sync;

import at.pcgamingfreaks.model.enums.SyncStatus;
import at.pcgamingfreaks.model.enums.SyncType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyncStatusDTO {
	private SyncType type;
	private SyncStatus status;
	private LocalDateTime startedAt;
	private LocalDateTime completedAt;
}
