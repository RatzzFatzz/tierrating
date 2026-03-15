package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.*;
import at.pcgamingfreaks.service.AuthService;
import at.pcgamingfreaks.service.thirdparty.auth.ThirdPartyAuthenticatorFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;
	private final ThirdPartyAuthenticatorFactory thirdPartyAuthenticatorFactory;

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
		log.debug("Login request from {}", request.getUsername());
		return ResponseEntity.ok(authService.authenticate(request.getUsername(), request.getPassword()));
	}

	@PostMapping("/signup")
	public ResponseEntity<SignupResponseDTO> signup(@Valid @RequestBody SignupRequestDTO request) {
		log.debug("Signup request from {}", request.getUsername());
		return ResponseEntity.ok(authService.signup(request));
	}

	@PostMapping("/refresh")
	public ResponseEntity<LoginResponseDTO> refresh(@RequestBody RefreshRequestDTO request) {
		return ResponseEntity.ok(authService.refreshToken(request.getToken()));
	}

	@PostMapping("/change-password")
	@PreAuthorize("authentication.principal.username == #request.username")
	public void changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
		log.debug("Change password request from {}", request.getUsername());
		authService.changePassword(request);
	}

	@PostMapping("/delete-account")
	@PreAuthorize("authentication.principal.username == #request.username")
	public void deleteAccount(@RequestBody AccountDeletionRequestDTO request) {
		log.debug("Account deletion request from {}", request.getUsername());
		authService.deleteAccount(request);
	}

	@PostMapping("/oauth/{service}/{username}")
	@PreAuthorize("authentication.principal.username == #username")
	@Validated
	@CrossOrigin
	public void authThirdPartyOAuthAccount(
			@PathVariable ThirdPartyService service,
			@PathVariable String username,
			@RequestBody ThirdPartyOAuthRequestDTO request
	) {
		log.info("Auth request for {} from {}", service, username);
		thirdPartyAuthenticatorFactory.getOauthProvider(service).auth(username, request);
	}

	@PostMapping("/openid/{service}/{username}")
	@PreAuthorize("authentication.principal.username == #username")
	@Validated
	@CrossOrigin
	public void authThirdPartyOpenIdAccount(
			@PathVariable ThirdPartyService service,
			@PathVariable String username,
			@RequestBody ThirdPartyOpenIdAuthRequestDTO request
	) {
		log.info("Auth request for {} from {}", service, username);
		thirdPartyAuthenticatorFactory.getOpenIdProvider(service).auth(username, request);
	}
}
