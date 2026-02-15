package at.pcgamingfreaks.service.thirdparty.connector;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ThirdPartyRemovalResponseDTO;

public interface ThirdPartyConnectorService {
    ThirdPartyService getService();
    ThirdPartyRemovalResponseDTO removeConnection(User user);
}
