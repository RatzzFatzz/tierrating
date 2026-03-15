package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ThirdPartyOAuthRequestDTO;

public interface ThirdPartyOAuthAuthenticatorService {
	ThirdPartyService getService();

	void auth(String username, ThirdPartyOAuthRequestDTO request);
}
