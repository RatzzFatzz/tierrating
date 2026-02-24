package at.pcgamingfreaks.service.thirdparty.data;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ListEntryDTO;

import java.util.List;

public interface DataService {
    ThirdPartyService getService();
    ContentType getContentType();

    List<ListEntryDTO> fetch(String username);
    void pull(String username);

    void update(long id, float score, User user);
    void push(String username);
}
