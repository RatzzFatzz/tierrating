package at.pcgamingfreaks.service.thirdparty.data.update.trakt;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.service.thirdparty.data.update.DataUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public abstract class TraktUpdateService implements DataUpdateService {
    @Override
    public ThirdPartyService getService() {
        return ThirdPartyService.TRAKT;
    }
}
