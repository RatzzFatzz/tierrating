package at.pcgamingfreaks.controller;

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

@Slf4j
@RestController
@RequestMapping("info")
@RequiredArgsConstructor
public class ThirdPartyInfoController {
	private final ThirdPartyInfoFactory thirdPartyInfoFactory;

	@GetMapping("{service}")
	public ResponseEntity<ThirdPartyInfoResponseDTO> info(@PathVariable ThirdPartyService service) {
		return ResponseEntity.ok(thirdPartyInfoFactory.getProvider(service).info());
	}
}
