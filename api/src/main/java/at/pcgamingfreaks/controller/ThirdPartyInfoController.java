package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.config.ThirdPartyServiceConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ThirdPartyInfoResponseDTO;
import at.pcgamingfreaks.service.thirdparty.info.ThirdPartyInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("info")
@RequiredArgsConstructor
public class ThirdPartyInfoController {
	private final ThirdPartyInfoFactory thirdPartyInfoFactory;
	private final ThirdPartyConfig thirdPartyConfig;
	private final Pattern configValidPattern = Pattern.compile("get(?<service>.*)");

	@GetMapping("{service}")
	public ResponseEntity<ThirdPartyInfoResponseDTO> info(@PathVariable ThirdPartyService service) {
		return ResponseEntity.ok(thirdPartyInfoFactory.getProvider(service).info());
	}

	@GetMapping("services")
	public ResponseEntity<List<String>> getAvailableServices() throws InvocationTargetException, IllegalAccessException {
		List<ThirdPartyService> services = new ArrayList<>();
		Method[] methods = thirdPartyConfig.getClass().getMethods();
		for (Method method : methods) {
			if (method.getReturnType().equals(ServiceConfig.class)) {
				ThirdPartyServiceConfig serviceConfig = (ThirdPartyServiceConfig) method.invoke(thirdPartyConfig);
				Matcher matcher = configValidPattern.matcher(method.getName());
				if (serviceConfig.isValid() && matcher.find()) {
					services.add(ThirdPartyService.from(matcher.group("service")));
				}
			}
		}
		services.remove(ThirdPartyService.TMDB); // only cover image provider and should therefore not be included
		return ResponseEntity.ok(services.stream().map(ThirdPartyService::name).toList());
	}
}
