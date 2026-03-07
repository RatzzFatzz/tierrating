package at.pcgamingfreaks.service.thirdparty.data;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataFactory {
	private final Map<ThirdPartyService, Map<ContentType, DataService>> providers;

	@Autowired
	public DataFactory(List<DataService> providerList) {
		Map<ThirdPartyService, List<DataService>> providersByService = providerList.stream()
				.collect(Collectors.groupingBy(DataService::getService));
		providers = providersByService.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
						.collect(Collectors.toMap(DataService::getContentType, provider -> provider))));
	}

	public DataService getProvider(ThirdPartyService service, ContentType contentType) {
		DataService provider = providers.containsKey(service) ? providers.get(service).get(contentType) : null;
		if (provider == null) {
			throw new IllegalArgumentException("No provider found for service: " + service);
		}
		return provider;
	}
}
