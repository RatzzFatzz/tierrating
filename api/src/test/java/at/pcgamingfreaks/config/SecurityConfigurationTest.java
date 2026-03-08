package at.pcgamingfreaks.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfiguration Tests")
class SecurityConfigurationTest {

    @Mock
    private AuthenticationProvider authenticationProvider;

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private SecurityConfiguration securityConfiguration;

    @BeforeEach
    void setUp() {
        securityConfiguration = new SecurityConfiguration(authenticationProvider, jwtAuthenticationFilter);
    }

    private CorsConfiguration getCorsConfig() {
        CorsConfigurationSource source = securityConfiguration.corsConfigurationSource();
        assertNotNull(source);
        assertTrue(source instanceof UrlBasedCorsConfigurationSource);
        Map<String, CorsConfiguration> configs = ((UrlBasedCorsConfigurationSource) source).getCorsConfigurations();
        assertFalse(configs.isEmpty());
        return configs.values().iterator().next();
    }

    @Test
    @DisplayName("corsConfigurationSource() should return configured source with correct origins")
    void corsConfigurationSource_shouldReturnCorrectOrigins() {
        CorsConfiguration config = getCorsConfig();
        assertNotNull(config);
        assertTrue(config.getAllowedOrigins().contains("http://localhost:3000"));
        assertTrue(config.getAllowedOrigins().contains("http://localhost:8080"));
    }

    @Test
    @DisplayName("corsConfigurationSource() should include correct methods")
    void corsConfigurationSource_shouldIncludeCorrectMethods() {
        CorsConfiguration config = getCorsConfig();
        assertNotNull(config);
        assertTrue(config.getAllowedMethods().contains("GET"));
        assertTrue(config.getAllowedMethods().contains("POST"));
    }

    @Test
    @DisplayName("corsConfigurationSource() should include correct headers")
    void corsConfigurationSource_shouldIncludeCorrectHeaders() {
        CorsConfiguration config = getCorsConfig();
        assertNotNull(config);
        assertTrue(config.getAllowedHeaders().contains("Authorization"));
        assertTrue(config.getAllowedHeaders().contains("Content-Type"));
    }
}
