package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.*;
import at.pcgamingfreaks.service.AuthService;
import at.pcgamingfreaks.service.thirdparty.auth.ThirdPartyAuthenticatorFactory;
import at.pcgamingfreaks.service.thirdparty.auth.ThirdPartyAuthenticatorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private ThirdPartyAuthenticatorFactory thirdPartyAuthenticatorFactory;

    @Mock
    private ThirdPartyAuthenticatorService authenticatorService;

    @InjectMocks
    private AuthController authController;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_TOKEN = "test-jwt-token";

    @Test
    @DisplayName("login() should return token for valid credentials")
    void login_shouldReturnTokenForValidCredentials() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUsername(TEST_USERNAME);
        request.setPassword(TEST_PASSWORD);

        LoginResponseDTO responseDTO = new LoginResponseDTO(TEST_TOKEN);
        when(authService.authenticate(TEST_USERNAME, TEST_PASSWORD)).thenReturn(responseDTO);

        ResponseEntity<LoginResponseDTO> response = authController.login(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(TEST_TOKEN, response.getBody().getToken());
        verify(authService).authenticate(TEST_USERNAME, TEST_PASSWORD);
    }

    @Test
    @DisplayName("signup() should create user and return success")
    void signup_shouldCreateUserAndReturnSuccess() {
        SignupRequestDTO request = new SignupRequestDTO();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        SignupResponseDTO responseDTO = new SignupResponseDTO();
        responseDTO.setSignupSuccess(true);
        responseDTO.setUsernameTaken(false);
        responseDTO.setEmailTaken(false);

        when(authService.signup(any(SignupRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<SignupResponseDTO> response = authController.signup(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSignupSuccess());
        assertFalse(response.getBody().isUsernameTaken());
        assertFalse(response.getBody().isEmailTaken());
        verify(authService).signup(any(SignupRequestDTO.class));
    }

    @Test
    @DisplayName("refresh() should return new token for valid refresh token")
    void refresh_shouldReturnNewToken() {
        RefreshRequestDTO request = new RefreshRequestDTO();
        request.setToken(TEST_TOKEN);

        LoginResponseDTO responseDTO = new LoginResponseDTO("new-token");
        when(authService.refreshToken(TEST_TOKEN)).thenReturn(responseDTO);

        ResponseEntity<LoginResponseDTO> response = authController.refresh(request);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("new-token", response.getBody().getToken());
        verify(authService).refreshToken(TEST_TOKEN);
    }

    @Test
    @DisplayName("changePassword() should change password successfully")
    void changePassword_shouldChangePasswordSuccessfully() {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setUsername(TEST_USERNAME);
        request.setOldPassword("oldPass123");
        request.setNewPassword("newPass123");

        doNothing().when(authService).changePassword(any(ChangePasswordRequestDTO.class));

        authController.changePassword(request);

        verify(authService).changePassword(any(ChangePasswordRequestDTO.class));
    }

    @Test
    @DisplayName("deleteAccount() should delete account successfully")
    void deleteAccount_shouldDeleteAccountSuccessfully() {
        AccountDeletionRequestDTO request = new AccountDeletionRequestDTO();
        request.setUsername(TEST_USERNAME);

        doNothing().when(authService).deleteAccount(any(AccountDeletionRequestDTO.class));

        authController.deleteAccount(request);

        verify(authService).deleteAccount(any(AccountDeletionRequestDTO.class));
    }

    @Test
    @DisplayName("authThirdPartyAccount() should authenticate third party account")
    void authThirdPartyAccount_shouldAuthenticateThirdPartyAccount() {
        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        when(thirdPartyAuthenticatorFactory.getProvider(ThirdPartyService.ANILIST)).thenReturn(authenticatorService);
        doNothing().when(authenticatorService).auth(eq(TEST_USERNAME), any(ThirdPartyAuthRequestDTO.class));

        authController.authThirdPartyAccount(ThirdPartyService.ANILIST, TEST_USERNAME, request);

        verify(thirdPartyAuthenticatorFactory).getProvider(ThirdPartyService.ANILIST);
        verify(authenticatorService).auth(eq(TEST_USERNAME), any(ThirdPartyAuthRequestDTO.class));
    }

    @Test
    @DisplayName("authThirdPartyAccount() should work with TRAKT service")
    void authThirdPartyAccount_shouldWorkWithTraktService() {
        ThirdPartyAuthRequestDTO request = new ThirdPartyAuthRequestDTO();
        request.setCode("auth-code");

        when(thirdPartyAuthenticatorFactory.getProvider(ThirdPartyService.TRAKT)).thenReturn(authenticatorService);
        doNothing().when(authenticatorService).auth(eq(TEST_USERNAME), any(ThirdPartyAuthRequestDTO.class));

        authController.authThirdPartyAccount(ThirdPartyService.TRAKT, TEST_USERNAME, request);

        verify(thirdPartyAuthenticatorFactory).getProvider(ThirdPartyService.TRAKT);
        verify(authenticatorService).auth(eq(TEST_USERNAME), any(ThirdPartyAuthRequestDTO.class));
    }
}
