package at.pcgamingfreaks.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThirdPartyOAuthRequestDTO {
	@NotBlank
	private String code;
}
