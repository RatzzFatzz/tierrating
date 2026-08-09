package at.pcgamingfreaks.service.auth;

import at.pcgamingfreaks.model.enums.MediaSource;
import at.pcgamingfreaks.model.dto.ThirdPartyOpenIdAuthRequestDTO;

public interface ThirdPartyOpenIdAuthenticatorService {
	MediaSource getMediaSource();

	void auth(String username, ThirdPartyOpenIdAuthRequestDTO request);
}
