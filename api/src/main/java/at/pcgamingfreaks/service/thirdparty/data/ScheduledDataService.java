package at.pcgamingfreaks.service.thirdparty.data;

import at.pcgamingfreaks.model.repo.ThirdPartyConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledDataService {
	private final ThirdPartyConnectionRepository thirdPartyConnectionRepository;
	private final DataFactory dataFactory;

	@Scheduled(cron = "${sync.interval}")
	public void pull() {
		thirdPartyConnectionRepository.findAllByAutoImportSyncIsTrue().forEach(thirdPartyConnection -> {
			dataFactory.getProvider(thirdPartyConnection.getService()).values().forEach(service ->{
				service.pull(thirdPartyConnection.getUser().getUsername());
			});
		});
	}
}
