package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ThirdPartyOpenIdAuthRequestDTO;

public interface ThirdPartyOpenIdAuthenticatorService {
	ThirdPartyService getService();

	void auth(String username, ThirdPartyOpenIdAuthRequestDTO request);
}
