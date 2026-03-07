package at.pcgamingfreaks.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ClientConfig Tests")
class ClientConfigTest {

    @Test
    @DisplayName("isValid() should return true when key and secret are set")
    void isValid_shouldReturnTrueWhenKeyAndSecretAreSet() {
        ClientConfig config = new ClientConfig();
        config.setKey("test-key");
        config.setSecret("test-secret");

        assertTrue(config.isValid());
    }

    @Test
    @DisplayName("isValid() should return false when key is null")
    void isValid_shouldReturnFalseWhenKeyIsNull() {
        ClientConfig config = new ClientConfig();
        config.setKey(null);
        config.setSecret("test-secret");

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("isValid() should return false when secret is null")
    void isValid_shouldReturnFalseWhenSecretIsNull() {
        ClientConfig config = new ClientConfig();
        config.setKey("test-key");
        config.setSecret(null);

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("isValid() should return false when key is blank")
    void isValid_shouldReturnFalseWhenKeyIsBlank() {
        ClientConfig config = new ClientConfig();
        config.setKey("   ");
        config.setSecret("test-secret");

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("isValid() should return false when secret is blank")
    void isValid_shouldReturnFalseWhenSecretIsBlank() {
        ClientConfig config = new ClientConfig();
        config.setKey("test-key");
        config.setSecret("   ");

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("isValid() should return false when both are null")
    void isValid_shouldReturnFalseWhenBothAreNull() {
        ClientConfig config = new ClientConfig();

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("isValid() should return false when both are blank")
    void isValid_shouldReturnFalseWhenBothAreBlank() {
        ClientConfig config = new ClientConfig();
        config.setKey("  ");
        config.setSecret("  ");

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("getters and setters should work correctly")
    void gettersAndSetters_shouldWork() {
        ClientConfig config = new ClientConfig();
        config.setKey("my-key");
        config.setSecret("my-secret");

        assertEquals("my-key", config.getKey());
        assertEquals("my-secret", config.getSecret());
    }
}
