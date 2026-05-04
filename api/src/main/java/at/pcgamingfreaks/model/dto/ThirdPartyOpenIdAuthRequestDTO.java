package at.pcgamingfreaks.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ThirdPartyOpenIdAuthRequestDTO {
	@NotEmpty
	Map<String, String> params;
}
