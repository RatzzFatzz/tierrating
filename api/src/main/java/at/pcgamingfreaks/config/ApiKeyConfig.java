package at.pcgamingfreaks.config;

import lombok.Getter;
import lombok.Setter;

import static io.micrometer.common.util.StringUtils.isNotBlank;

@Getter
@Setter
public class ApiKeyConfig implements ThirdPartyServiceConfig {
	private String key;

	public boolean isValid() {
		return isNotBlank(key);
	}
}
