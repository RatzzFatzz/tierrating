package at.pcgamingfreaks.service;

import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.*;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_TOKEN = "test-jwt-token";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword("encodedPassword");
    }

    @Test
    @DisplayName("authenticate() should return token for valid credentials")
    void authenticate_shouldReturnTokenForValidCredentials() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.create(TEST_USERNAME)).thenReturn(TEST_TOKEN);

        LoginResponseDTO response = authService.authenticate(TEST_USERNAME, TEST_PASSWORD);

        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).create(TEST_USERNAME);
    }

    @Test
    @DisplayName("signup() should create user when username and email are not taken")
    void signup_shouldCreateUserWhenUsernameAndEmailNotTaken() {
        SignupRequestDTO request = new SignupRequestDTO();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        SignupResponseDTO response = authService.signup(request);

        assertFalse(response.isUsernameTaken());
        assertFalse(response.isEmailTaken());
        assertTrue(response.isSignupSuccess());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("signup() should not create user when username is taken")
    void signup_shouldNotCreateUserWhenUsernameTaken() {
        SignupRequestDTO request = new SignupRequestDTO();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        SignupResponseDTO response = authService.signup(request);

        assertTrue(response.isUsernameTaken());
        assertFalse(response.isEmailTaken());
        assertFalse(response.isSignupSuccess());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("signup() should not create user when email is taken")
    void signup_shouldNotCreateUserWhenEmailTaken() {
        SignupRequestDTO request = new SignupRequestDTO();
        request.setUsername(TEST_USERNAME);
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        SignupResponseDTO response = authService.signup(request);

        assertFalse(response.isUsernameTaken());
        assertTrue(response.isEmailTaken());
        assertFalse(response.isSignupSuccess());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("refreshToken() should return new token for valid token")
    void refreshToken_shouldReturnNewTokenForValidToken() {
        String newToken = "new-token";
        
        when(jwtService.isTokenExpired(TEST_TOKEN)).thenReturn(false);
        when(jwtService.extractUsername(TEST_TOKEN)).thenReturn(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(jwtService.create(TEST_USERNAME)).thenReturn(newToken);

        LoginResponseDTO response = authService.refreshToken(TEST_TOKEN);

        assertNotNull(response);
        assertEquals(newToken, response.getToken());
    }

    @Test
    @DisplayName("changePassword() should update password for valid user")
    void changePassword_shouldUpdatePasswordForValidUser() {
        ChangePasswordRequestDTO request = new ChangePasswordRequestDTO();
        request.setUsername(TEST_USERNAME);
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");
        
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedPass");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertDoesNotThrow(() -> authService.changePassword(request));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("deleteAccount() should delete user successfully")
    void deleteAccount_shouldDeleteUserSuccessfully() {
        AccountDeletionRequestDTO request = new AccountDeletionRequestDTO();
        request.setUsername(TEST_USERNAME);
        
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(User.class));

        assertDoesNotThrow(() -> authService.deleteAccount(request));
        verify(userRepository).delete(testUser);
    }
}
