package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.model.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThirdPartyAuthenticatorFactory {
	private final Map<ThirdPartyService, ThirdPartyOAuthAuthenticatorService> oauthProviders;
	private final Map<ThirdPartyService, ThirdPartyOpenIdAuthenticatorService> openIdProviders;

	@Autowired
	public ThirdPartyAuthenticatorFactory(List<ThirdPartyOAuthAuthenticatorService> oauthProviders,
										  List<ThirdPartyOpenIdAuthenticatorService> openIdProviders) {
		this.oauthProviders = oauthProviders.stream()
				.collect(Collectors.toMap(ThirdPartyOAuthAuthenticatorService::getService, provider -> provider));
		this.openIdProviders = openIdProviders.stream()
				.collect(Collectors.toMap(ThirdPartyOpenIdAuthenticatorService::getService, provider -> provider));
	}

	public ThirdPartyOAuthAuthenticatorService getOauthProvider(ThirdPartyService service) {
		ThirdPartyOAuthAuthenticatorService provider = oauthProviders.get(service);
		if (provider == null) {
			throw new IllegalArgumentException("Third party service not found: " + service);
		}
		return provider;
	}

	public ThirdPartyOpenIdAuthenticatorService getOpenIdProvider(ThirdPartyService service) {
		ThirdPartyOpenIdAuthenticatorService provider = openIdProviders.get(service);
		if (provider == null) {
			throw new IllegalArgumentException("Third party service not found: " + service);
		}
		return provider;
	}
}
