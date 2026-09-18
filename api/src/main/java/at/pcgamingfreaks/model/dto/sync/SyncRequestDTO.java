package at.pcgamingfreaks.model.dto.sync;

import at.pcgamingfreaks.model.enums.SyncType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncRequestDTO {
	private SyncType type;
}
