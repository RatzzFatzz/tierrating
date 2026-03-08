package at.pcgamingfreaks.mapper;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserDtoMapper Tests")
class UserDtoMapperTest {

    @Test
    @DisplayName("map() should map user with bio to DTO")
    void map_shouldMapUserWithBio() {
        User user = new User();
        user.setUsername("testuser");
        user.setBio("Test bio");
        
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.ANILIST, new ThirdPartyConnection());
        user.setConnections(connections);

        UserDTO dto = UserDtoMapper.map(user);

        assertNotNull(dto);
        assertEquals("testuser", dto.getUsername());
        assertEquals("Test bio", dto.getBio());
        assertEquals(1, dto.getConnectedServices().size());
        assertTrue(dto.getConnectedServices().contains(ThirdPartyService.ANILIST));
    }

    @Test
    @DisplayName("map() should handle null bio as empty string")
    void map_shouldHandleNullBio() {
        User user = new User();
        user.setUsername("testuser");
        user.setBio(null);
        user.setConnections(new HashMap<>());

        UserDTO dto = UserDtoMapper.map(user);

        assertNotNull(dto);
        assertEquals("", dto.getBio());
    }

    @Test
    @DisplayName("map() should handle empty connections")
    void map_shouldHandleEmptyConnections() {
        User user = new User();
        user.setUsername("testuser");
        user.setBio("Bio");
        user.setConnections(new HashMap<>());

        UserDTO dto = UserDtoMapper.map(user);

        assertNotNull(dto);
        assertTrue(dto.getConnectedServices().isEmpty());
    }

    @Test
    @DisplayName("map() should handle multiple connections")
    void map_shouldHandleMultipleConnections() {
        User user = new User();
        user.setUsername("testuser");
        user.setBio("Bio");
        
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.ANILIST, new ThirdPartyConnection());
        connections.put(ThirdPartyService.TRAKT, new ThirdPartyConnection());
        user.setConnections(connections);

        UserDTO dto = UserDtoMapper.map(user);

        assertNotNull(dto);
        assertEquals(2, dto.getConnectedServices().size());
        assertTrue(dto.getConnectedServices().contains(ThirdPartyService.ANILIST));
        assertTrue(dto.getConnectedServices().contains(ThirdPartyService.TRAKT));
    }
}
