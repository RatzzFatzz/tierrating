package at.pcgamingfreaks.model.dto.sync;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncStatusResponseDTO {
	private SyncStatusDTO current;
	private SyncStatusDTO lastPull;
	private SyncStatusDTO lastPush;
}
