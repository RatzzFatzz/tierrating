package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ThirdPartyAuthRequestDTO;

public interface ThirdPartyAuthenticatorService {
	ThirdPartyService getService();

	void auth(String username, ThirdPartyAuthRequestDTO request);
}
