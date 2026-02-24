package at.pcgamingfreaks.service.thirdparty.data.provider;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ListEntryDTO;

import java.util.List;

public interface DataProviderService {
    ThirdPartyService getService();
    ContentType getContentType();

    List<ListEntryDTO> fetch(String username);
    void pull(String username);
}
