package at.pcgamingfreaks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class AsyncConfig {

	@Bean
	public ScheduledExecutorService syncExecutorService() {
		return Executors.newSingleThreadScheduledExecutor();
	}

}
