package at.pcgamingfreaks.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private static final String SECRET_KEY = "test-secret-key-for-jwt-service-testing";
    private static final long EXPIRATION_MINUTES = 60;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET_KEY, EXPIRATION_MINUTES);
    }

    @Test
    @DisplayName("create() should generate valid JWT token")
    void create_shouldGenerateValidToken() {
        String username = "testuser";
        String token = jwtService.create(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        String decodedUsername = JWT.decode(token).getSubject();
        assertEquals(username, decodedUsername);
    }

    @Test
    @DisplayName("create() should set issuedAt to current time")
    void create_shouldSetIssuedAtToCurrentTime() {
        Instant beforeCreation = Instant.now();
        String token = jwtService.create("testuser");
        Instant afterCreation = Instant.now();

        Date issuedAt = JWT.decode(token).getIssuedAt();
        Instant issuedAtInstant = issuedAt.toInstant();

        assertTrue(issuedAtInstant.isAfter(beforeCreation.minus(1, ChronoUnit.SECONDS)));
        assertTrue(issuedAtInstant.isBefore(afterCreation.plus(1, ChronoUnit.SECONDS)));
    }

    @Test
    @DisplayName("create() should set correct expiration time")
    void create_shouldSetCorrectExpirationTime() {
        Instant beforeCreation = Instant.now();
        String token = jwtService.create("testuser");

        Date expiresAt = JWT.decode(token).getExpiresAt();
        Instant expectedExpiration = beforeCreation.plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);
        Instant actualExpiration = expiresAt.toInstant();

        long difference = Math.abs(ChronoUnit.SECONDS.between(expectedExpiration, actualExpiration));
        assertTrue(difference < 2);
    }

    @Test
    @DisplayName("extractUsername() should extract correct username from token")
    void extractUsername_shouldExtractCorrectUsername() {
        String username = "testuser";
        String token = jwtService.create(username);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("extractExpiration() should extract correct expiration date")
    void extractExpiration_shouldExtractCorrectExpiration() {
        String token = jwtService.create("testuser");
        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("isTokenExpired() should return false for fresh token")
    void isTokenExpired_shouldReturnFalseForFreshToken() {
        String token = jwtService.create("testuser");
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    @DisplayName("isTokenExpired() should return true for expired token")
    void isTokenExpired_shouldReturnTrueForExpiredToken() {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        String expiredToken = JWT.create()
                .withSubject("testuser")
                .withIssuedAt(Date.from(Instant.now().minus(2, ChronoUnit.HOURS)))
                .withExpiresAt(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
                .sign(algorithm);

        assertTrue(jwtService.isTokenExpired(expiredToken));
    }

    @Test
    @DisplayName("valid() should return true for valid token and matching user")
    void valid_shouldReturnTrueForValidTokenAndMatchingUser() {
        String username = "testuser";
        String token = jwtService.create(username);
        UserDetails userDetails = new User(username, "password", Collections.emptyList());

        assertTrue(jwtService.valid(token, userDetails));
    }

    @Test
    @DisplayName("valid() should return false for valid token but non-matching user")
    void valid_shouldReturnFalseForNonMatchingUser() {
        String token = jwtService.create("testuser");
        UserDetails userDetails = new User("differentuser", "password", Collections.emptyList());

        assertFalse(jwtService.valid(token, userDetails));
    }

    @Test
    @DisplayName("valid() should return false for expired token")
    void valid_shouldReturnFalseForExpiredToken() {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        String expiredToken = JWT.create()
                .withSubject("testuser")
                .withIssuedAt(Date.from(Instant.now().minus(2, ChronoUnit.HOURS)))
                .withExpiresAt(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
                .sign(algorithm);

        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        assertFalse(jwtService.valid(expiredToken, userDetails));
    }

    @Test
    @DisplayName("create() should create different tokens for different users")
    void create_shouldCreateDifferentTokensForDifferentUsers() {
        String token1 = jwtService.create("user1");
        String token2 = jwtService.create("user2");

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("create() should create different tokens for same user at different times")
    void create_shouldCreateDifferentTokensForSameUserAtDifferentTimes() throws InterruptedException {
        String token1 = jwtService.create("testuser");
        Thread.sleep(1000);
        String token2 = jwtService.create("testuser");

        assertNotEquals(token1, token2);
    }
}
