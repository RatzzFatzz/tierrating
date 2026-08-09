package at.pcgamingfreaks.service.auth;

import at.pcgamingfreaks.model.enums.MediaSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThirdPartyAuthenticatorFactory {
	private final Map<MediaSource, ThirdPartyOAuthAuthenticatorService> oauthProviders;
	private final Map<MediaSource, ThirdPartyOpenIdAuthenticatorService> openIdProviders;

	@Autowired
	public ThirdPartyAuthenticatorFactory(List<ThirdPartyOAuthAuthenticatorService> oauthProviders,
										  List<ThirdPartyOpenIdAuthenticatorService> openIdProviders) {
		this.oauthProviders = oauthProviders.stream()
				.collect(Collectors.toMap(ThirdPartyOAuthAuthenticatorService::getMediaSource, provider -> provider));
		this.openIdProviders = openIdProviders.stream()
				.collect(Collectors.toMap(ThirdPartyOpenIdAuthenticatorService::getMediaSource, provider -> provider));
	}

	public ThirdPartyOAuthAuthenticatorService getOauthProvider(MediaSource service) {
		ThirdPartyOAuthAuthenticatorService provider = oauthProviders.get(service);
		if (provider == null) {
			throw new IllegalArgumentException("Third party service not found: " + service);
		}
		return provider;
	}

	public ThirdPartyOpenIdAuthenticatorService getOpenIdProvider(MediaSource service) {
		ThirdPartyOpenIdAuthenticatorService provider = openIdProviders.get(service);
		if (provider == null) {
			throw new IllegalArgumentException("Third party service not found: " + service);
		}
		return provider;
	}
}
