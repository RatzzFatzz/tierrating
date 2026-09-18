package at.pcgamingfreaks.exceptions.handlers;

import at.pcgamingfreaks.exceptions.MediaSyncConflictException;
import at.pcgamingfreaks.model.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(1)
public class SyncExceptionHandler {

	@ExceptionHandler(MediaSyncConflictException.class)
	public ResponseEntity<ErrorResponseDTO> handleMediaSyncConflictException(MediaSyncConflictException e) {
		log.warn("Sync conflict");
		return ResponseEntity.status(409).body(new ErrorResponseDTO("Sync conflict")); // TODO: improve message
	}
}
