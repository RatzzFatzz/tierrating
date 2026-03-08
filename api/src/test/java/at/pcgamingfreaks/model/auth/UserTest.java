package at.pcgamingfreaks.model.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Model Tests")
class UserTest {

    @Test
    @DisplayName("User should implement UserDetails correctly")
    void user_shouldImplementUserDetailsCorrectly() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@test.com");

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
        assertNotNull(user.getAuthorities());
        assertTrue(user.getAuthorities().isEmpty());
    }

    @Test
    @DisplayName("User getUsername should return username")
    void user_shouldReturnUsername() {
        User user = new User();
        user.setUsername("myuser");
        assertEquals("myuser", user.getUsername());
    }

    @Test
    @DisplayName("User getPassword should return password")
    void user_shouldReturnPassword() {
        User user = new User();
        user.setPassword("secret");
        assertEquals("secret", user.getPassword());
    }

    @Test
    @DisplayName("User bio should be gettable and settable")
    void user_shouldGetAndSetBio() {
        User user = new User();
        user.setBio("My bio");
        assertEquals("My bio", user.getBio());
    }
}
