package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.model.dto.UpdateScoreRequestDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.service.thirdparty.data.DataFactory;
import at.pcgamingfreaks.service.thirdparty.data.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static at.pcgamingfreaks.model.ThirdPartyService.hasUserConnection;

@Slf4j
@RestController
@RequestMapping("data")
@CrossOrigin
@RequiredArgsConstructor
public class DataController {
	private final DataFactory dataFactory;
	private final UserRepository userRepository;

	/**
	 * Fetch data for username, service and type.
	 * If no data is synced yet and auto sync is active, pull data before returning result.
	 *
	 * @return mapped third-party data ordered by score descending
	 */
	@GetMapping("fetch/{username}/{service}/{type}")
	public ResponseEntity<List<ListEntryDTO>> fetch(@PathVariable String username,
													@PathVariable ThirdPartyService service,
													@PathVariable ContentType type) {
		DataService dataService = dataFactory.getProvider(service, type);
		if (dataService == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(
				dataService.fetch(username).stream()
						.sorted(Comparator.comparing(ListEntryDTO::getScore).reversed())
						.toList()
		);
	}

	/**
	 * Update score for resource.
	 * If auto sync is active, send changes directly to third-party service.
	 *
	 * @param request
	 */
	@PostMapping("update/{username}/{service}/{type}")
	@PreAuthorize("authentication.principal.username == #username")
	public void update(@PathVariable String username,
					   @PathVariable ThirdPartyService service,
					   @PathVariable ContentType type,
					   @RequestBody UpdateScoreRequestDTO request) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		if (!hasUserConnection(user, service))
			throw new ThirdPartyUnconfiguredException(service);
		dataFactory.getProvider(service, type).update(request.getId(), request.getScore(), user);
	}

	/**
	 * Pull data from third-party service for user and type. Overwriting existing scores.
	 *
	 * @param username
	 * @param service
	 * @param type
	 */
	@GetMapping("pull/{username}/{service}/{type}")
	public void pull(@PathVariable String username, @PathVariable ThirdPartyService service, @PathVariable ContentType type) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		if (!hasUserConnection(user, service)) throw new ThirdPartyUnconfiguredException(service);
		dataFactory.getProvider(service, type).pull(username);
	}

	/**
	 * Push score changes for user and type to third-party service.
	 *
	 * @param username
	 * @param service
	 * @param type
	 */
	@GetMapping("push/{username}/{service}/{type}")
	public void push(@PathVariable String username, @PathVariable ThirdPartyService service, @PathVariable ContentType type) {

	}

}
