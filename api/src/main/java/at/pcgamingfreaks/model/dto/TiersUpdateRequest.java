package at.pcgamingfreaks.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TiersUpdateRequest {
	List<TierDTO> tiers;
}
