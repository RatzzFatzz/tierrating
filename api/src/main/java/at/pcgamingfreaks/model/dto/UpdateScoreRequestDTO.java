package at.pcgamingfreaks.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateScoreRequestDTO {
	private long id;
	private float score;
}
