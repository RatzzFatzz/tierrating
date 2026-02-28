package at.pcgamingfreaks.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    private final Algorithm algorithm;
    private final long expirationMinutes;

    public JwtService(
            @Value("${security.jwt.secret-key}") String key,
            @Value("${security.jwt.expiration-minutes:60}") long expirationMinutes) {
        this.algorithm = Algorithm.HMAC256(key);
        this.expirationMinutes = expirationMinutes;
    }

    public String create(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
                .sign(algorithm);
    }

    public String extractUsername(String token) {
        return JWT.decode(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return JWT.decode(token).getExpiresAt();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean valid(String token, UserDetails user) {
        return user.getUsername().equals(extractUsername(token)) && !isTokenExpired(token);
    }
}
