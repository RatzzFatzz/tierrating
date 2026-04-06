package at.pcgamingfreaks.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import static io.micrometer.common.util.StringUtils.isNotBlank;

@Getter
@Setter
public class ServiceConfig implements ThirdPartyServiceConfig{
	@Value("${client.key}")
	private String key;
	@Value("${client.secret}")
	private String secret;
	private String redirectUrl;

	public boolean isValid() {
		return isNotBlank(key) && isNotBlank(secret) && isNotBlank(redirectUrl);
	}

	public void setUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
