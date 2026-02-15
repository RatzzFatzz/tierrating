package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.model.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThirdPartyAuthenticatorFactory {
    private final Map<ThirdPartyService, ThirdPartyAuthenticatorService> providers;

    @Autowired
    public ThirdPartyAuthenticatorFactory(List<ThirdPartyAuthenticatorService> providerList) {
        providers = providerList.stream()
                .collect(Collectors.toMap(ThirdPartyAuthenticatorService::getService, provider -> provider));
    }

    public ThirdPartyAuthenticatorService getProvider(ThirdPartyService service) {
        ThirdPartyAuthenticatorService provider = providers.get(service);
        if (provider == null) {
            throw new IllegalArgumentException("Third party service not found: " + service);
        }
        return provider;
    }
}
