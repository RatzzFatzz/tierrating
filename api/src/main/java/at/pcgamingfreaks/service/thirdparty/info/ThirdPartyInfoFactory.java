package at.pcgamingfreaks.service.thirdparty.info;

import at.pcgamingfreaks.model.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThirdPartyInfoFactory {
    private final Map<ThirdPartyService, ThirdPartyInfoService> providers;

    @Autowired
    public ThirdPartyInfoFactory(List<ThirdPartyInfoService> providerList) {
        providers = providerList.stream()
                .collect(Collectors.toMap(ThirdPartyInfoService::getService, provider -> provider));
    }

    public ThirdPartyInfoService getProvider(ThirdPartyService service) {
        ThirdPartyInfoService provider = providers.get(service);
        if (provider == null) {
            throw new IllegalArgumentException("Third party service not found: " + service);
        }
        return provider;
    }
}
