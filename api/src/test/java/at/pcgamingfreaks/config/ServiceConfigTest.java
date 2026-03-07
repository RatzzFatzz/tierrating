package at.pcgamingfreaks.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceConfig Tests")
class ServiceConfigTest {

    @Test
    @DisplayName("isValid() should return true when client is valid and redirectUrl is set")
    void isValid_shouldReturnTrueWhenClientIsValidAndRedirectUrlSet() {
        ServiceConfig config = new ServiceConfig();
        ClientConfig client = new ClientConfig();
        client.setKey("test-key");
        client.setSecret("test-secret");
        config.setClient(client);
        config.setUrl("https://example.com/callback");

        assertTrue(config.isValid());
    }

    @Test
    @DisplayName("isValid() should throw NullPointerException when client is null")
    void isValid_shouldThrowWhenClientIsNull() {
        ServiceConfig config = new ServiceConfig();
        config.setClient(null);
        config.setUrl("https://example.com/callback");

        assertThrows(NullPointerException.class, config::isValid);
    }

    @Test
    @DisplayName("isValid() should return false when redirectUrl is null")
    void isValid_shouldReturnFalseWhenRedirectUrlIsNull() {
        ServiceConfig config = new ServiceConfig();
        ClientConfig client = new ClientConfig();
        client.setKey("test-key");
        client.setSecret("test-secret");
        config.setClient(client);
        config.setRedirectUrl(null);

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("isValid() should return false when redirectUrl is blank")
    void isValid_shouldReturnFalseWhenRedirectUrlIsBlank() {
        ServiceConfig config = new ServiceConfig();
        ClientConfig client = new ClientConfig();
        client.setKey("test-key");
        client.setSecret("test-secret");
        config.setClient(client);
        config.setRedirectUrl("   ");

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("isValid() should return false when client is invalid")
    void isValid_shouldReturnFalseWhenClientIsInvalid() {
        ServiceConfig config = new ServiceConfig();
        ClientConfig client = new ClientConfig();
        client.setKey(null);
        client.setSecret(null);
        config.setClient(client);
        config.setUrl("https://example.com/callback");

        assertFalse(config.isValid());
    }

    @Test
    @DisplayName("setUrl() should set redirectUrl")
    void setUrl_shouldSetRedirectUrl() {
        ServiceConfig config = new ServiceConfig();
        config.setUrl("https://example.com/callback");

        assertEquals("https://example.com/callback", config.getRedirectUrl());
    }

    @Test
    @DisplayName("getters and setters should work correctly")
    void gettersAndSetters_shouldWork() {
        ServiceConfig config = new ServiceConfig();
        ClientConfig client = new ClientConfig();
        config.setClient(client);
        config.setRedirectUrl("https://test.com");

        assertEquals(client, config.getClient());
        assertEquals("https://test.com", config.getRedirectUrl());
    }
}
