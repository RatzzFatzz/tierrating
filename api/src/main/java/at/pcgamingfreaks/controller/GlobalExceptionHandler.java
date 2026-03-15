package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.dto.ErrorResponseDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyAuthenticationException;
import at.pcgamingfreaks.model.exceptions.ThirdPartySyncException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleUnknownException(Exception e) {
		log.error("Unexpected error occurred", e);
		return ResponseEntity.internalServerError().body(new ErrorResponseDTO("An unexpected error occurred"));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ErrorResponseDTO> handleUsernameNotFoundException(UsernameNotFoundException ex) {
		log.info("User '{}' not found", ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("User not found"));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Invalid credentials"));
	}

	@ExceptionHandler(CredentialsExpiredException.class)
	public ResponseEntity<ErrorResponseDTO> handleTokenExpiredException(CredentialsExpiredException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDTO("Token has expired"));
	}

	@ExceptionHandler(ThirdPartyAuthenticationException.class)
	public ResponseEntity<ErrorResponseDTO> handleThirdPartyAuthenticationException(ThirdPartyAuthenticationException ex) {
		log.warn("Third-party authentication error: {}", ex.getMessage());
		return ResponseEntity.internalServerError().body(new ErrorResponseDTO("Third-party authentication failed"));
	}

	@ExceptionHandler(ThirdPartyUnconfiguredException.class)
	public ResponseEntity<ErrorResponseDTO> handleThirdPartyUnconfiguredException(ThirdPartyUnconfiguredException ex) {
		log.warn("Third-party service {} unconfigured", ex.getMessage());
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(ThirdPartySyncException.class)
	public ResponseEntity<ErrorResponseDTO> handleThirdPartySyncException(ThirdPartySyncException ex) {
		log.warn("Third-party sync error: {}", ex.getMessage());
		return ResponseEntity.internalServerError().body(new ErrorResponseDTO("Third-party synchronization failed"));
	}
}
