package at.pcgamingfreaks.model.dto;

import at.pcgamingfreaks.model.ThirdPartyService;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserDTO {
	private UUID id;

	private String username;
	private String bio;

	private Set<ThirdPartyService> connectedServices;
}