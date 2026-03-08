package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.config.ServiceConfig;
import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.config.ClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigController Tests")
class ConfigControllerTest {

    @Mock
    private ThirdPartyConfig thirdPartyConfig;

    @InjectMocks
    private ConfigController configController;

    private ServiceConfig validServiceConfig;
    private ServiceConfig invalidServiceConfig;

    @BeforeEach
    void setUp() {
        validServiceConfig = new ServiceConfig();
        ClientConfig validClient = new ClientConfig();
        validClient.setKey("client-key");
        validClient.setSecret("client-secret");
        validServiceConfig.setClient(validClient);
        validServiceConfig.setUrl("http://redirect.url");

        invalidServiceConfig = new ServiceConfig();
        ClientConfig invalidClient = new ClientConfig();
        invalidServiceConfig.setClient(invalidClient);
    }

    @Test
    @DisplayName("getAvailableServices() should return list of configured services")
    void getAvailableServices_shouldReturnListOfConfiguredServices() throws InvocationTargetException, IllegalAccessException {
        when(thirdPartyConfig.getAnilist()).thenReturn(validServiceConfig);
        when(thirdPartyConfig.getTrakt()).thenReturn(invalidServiceConfig);

        ResponseEntity<List<String>> response = configController.getAvailableServices();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("anilist"));
        assertFalse(response.getBody().contains("trakt"));
    }

    @Test
    @DisplayName("getAvailableServices() should return empty list when no services configured")
    void getAvailableServices_shouldReturnEmptyListWhenNoServicesConfigured() throws InvocationTargetException, IllegalAccessException {
        when(thirdPartyConfig.getAnilist()).thenReturn(invalidServiceConfig);
        when(thirdPartyConfig.getTrakt()).thenReturn(invalidServiceConfig);

        ResponseEntity<List<String>> response = configController.getAvailableServices();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("getAvailableServices() should return all services when all configured")
    void getAvailableServices_shouldReturnAllServicesWhenAllConfigured() throws InvocationTargetException, IllegalAccessException {
        when(thirdPartyConfig.getAnilist()).thenReturn(validServiceConfig);
        when(thirdPartyConfig.getTrakt()).thenReturn(validServiceConfig);

        ResponseEntity<List<String>> response = configController.getAvailableServices();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains("anilist"));
        assertTrue(response.getBody().contains("trakt"));
    }
}
