package at.pcgamingfreaks.config;

import lombok.Getter;
import lombok.Setter;

import static io.micrometer.common.util.StringUtils.isNotBlank;

@Getter
@Setter
public class ServiceConfig implements ThirdPartyServiceConfig{
	private String key;
	private String secret;
	private String redirectUrl;

	public boolean isValid() {
		return isNotBlank(key) && isNotBlank(secret) && isNotBlank(redirectUrl);
	}

	public void setUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
