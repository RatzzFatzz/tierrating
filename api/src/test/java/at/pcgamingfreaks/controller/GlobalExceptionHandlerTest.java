package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ErrorResponseDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyAuthenticationException;
import at.pcgamingfreaks.model.exceptions.ThirdPartySyncException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("handleUnknownException() should return 500 with generic message")
    void handleUnknownException_shouldReturn500WithGenericMessage() {
        Exception exception = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleUnknownException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred", response.getBody().getError());
    }

    @Test
    @DisplayName("handleUsernameNotFoundException() should return 400 with user not found message")
    void handleUsernameNotFoundException_shouldReturn400WithUserNotFoundMessage() {
        UsernameNotFoundException exception = new UsernameNotFoundException("testuser");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleUsernameNotFoundException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getError());
    }

    @Test
    @DisplayName("handleAuthenticationException() should return 401 with invalid credentials message")
    void handleAuthenticationException_shouldReturn401WithInvalidCredentialsMessage() {
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleAuthenticationException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid credentials", response.getBody().getError());
    }

    @Test
    @DisplayName("handleTokenExpiredException() should return 401 with token expired message")
    void handleTokenExpiredException_shouldReturn401WithTokenExpiredMessage() {
        CredentialsExpiredException exception = new CredentialsExpiredException("Token expired");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleTokenExpiredException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Token has expired", response.getBody().getError());
    }

    @Test
    @DisplayName("handleThirdPartyAuthenticationException() should return 500 with third-party auth failed message")
    void handleThirdPartyAuthenticationException_shouldReturn500WithThirdPartyAuthFailedMessage() {
        ThirdPartyAuthenticationException exception = new ThirdPartyAuthenticationException("Auth failed");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleThirdPartyAuthenticationException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Third-party authentication failed", response.getBody().getError());
    }

    @Test
    @DisplayName("handleThirdPartyUnconfiguredException() should return 404")
    void handleThirdPartyUnconfiguredException_shouldReturn404() {
        ThirdPartyUnconfiguredException exception = new ThirdPartyUnconfiguredException(ThirdPartyService.ANILIST);

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleThirdPartyUnconfiguredException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("handleThirdPartySyncException() should return 500 with sync failed message")
    void handleThirdPartySyncException_shouldReturn500WithSyncFailedMessage() {
        ThirdPartySyncException exception = new ThirdPartySyncException("Sync failed");

        ResponseEntity<ErrorResponseDTO> response = globalExceptionHandler.handleThirdPartySyncException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Third-party synchronization failed", response.getBody().getError());
    }
}
