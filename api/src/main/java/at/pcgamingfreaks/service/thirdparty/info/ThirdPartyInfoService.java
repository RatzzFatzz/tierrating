package at.pcgamingfreaks.service.thirdparty.info;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ThirdPartyInfoResponseDTO;

public interface ThirdPartyInfoService {
	ThirdPartyService getService();

	ThirdPartyInfoResponseDTO info();
}
