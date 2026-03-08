package at.pcgamingfreaks.config;

import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationConfiguration Tests")
class ApplicationConfigurationTest {

    @Mock
    private UserRepository userRepository;

    private ApplicationConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new ApplicationConfiguration(userRepository);
    }

    @Test
    @DisplayName("userDetailsService() should return user when found")
    void userDetailsService_shouldReturnUserWhenFound() {
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetailsService service = configuration.userDetailsService();
        UserDetails result = service.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    @DisplayName("userDetailsService() should throw UsernameNotFoundException when user not found")
    void userDetailsService_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserDetailsService service = configuration.userDetailsService();

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unknown"));
    }

    @Test
    @DisplayName("passwordEncoder() should return BCryptPasswordEncoder")
    void passwordEncoder_shouldReturnBCryptEncoder() {
        BCryptPasswordEncoder encoder = configuration.passwordEncoder();

        assertNotNull(encoder);
        String encoded = encoder.encode("testpassword");
        assertTrue(encoder.matches("testpassword", encoded));
    }

    @Test
    @DisplayName("authenticationProvider() should return configured DaoAuthenticationProvider")
    void authenticationProvider_shouldReturnConfiguredProvider() {
        AuthenticationProvider provider = configuration.authenticationProvider();

        assertNotNull(provider);
    }
}
