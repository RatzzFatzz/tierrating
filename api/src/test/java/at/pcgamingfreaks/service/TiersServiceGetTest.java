package at.pcgamingfreaks.service;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.Tier;
import at.pcgamingfreaks.model.TierList;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.TierDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.TierListsRepository;
import at.pcgamingfreaks.model.repo.TiersRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TiersService getTierlist Tests")
class TiersServiceGetTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TierListsRepository tierListsRepository;

    @Mock
    private TiersRepository tiersRepository;

    @InjectMocks
    private TiersService tiersService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        Map<ThirdPartyService, ThirdPartyConnection> connections = new HashMap<>();
        connections.put(ThirdPartyService.ANILIST, new ThirdPartyConnection());
        testUser.setConnections(connections);
    }

    @Test
    @DisplayName("getTierlist() should throw UsernameNotFoundException when user not found")
    void getTierlist_shouldThrowWhenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> tiersService.getTierlist("unknown", ThirdPartyService.ANILIST, ContentType.ANIME));
    }

    @Test
    @DisplayName("getTierlist() should throw ThirdPartyUnconfiguredException when no connection")
    void getTierlist_shouldThrowWhenNoConnection() {
        User userWithNoConnections = new User();
        userWithNoConnections.setConnections(new HashMap<>());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userWithNoConnections));

        assertThrows(ThirdPartyUnconfiguredException.class,
                () -> tiersService.getTierlist("testuser", ThirdPartyService.ANILIST, ContentType.ANIME));
    }

    @Test
    @DisplayName("getTierlist() should return empty list when no tierlist found")
    void getTierlist_shouldReturnEmptyListWhenNoTierlist() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(tierListsRepository.findByUserAndServiceAndType(testUser, ThirdPartyService.ANILIST, ContentType.ANIME))
                .thenReturn(Optional.empty());

        List<TierDTO> result = tiersService.getTierlist("testuser", ThirdPartyService.ANILIST, ContentType.ANIME);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getTierlist() should return sorted tiers when tierlist found")
    void getTierlist_shouldReturnSortedTiers() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Tier tierA = new Tier(UUID.randomUUID(), "#FF0000", "A", 8, 8);
        Tier tierS = new Tier(UUID.randomUUID(), "#FFD700", "S", 10, 10);
        Tier tierB = new Tier(UUID.randomUUID(), "#00FF00", "B", 6, 6);

        TierList tierList = new TierList();
        tierList.setUser(testUser);
        tierList.setTiers(List.of(tierA, tierS, tierB));

        when(tierListsRepository.findByUserAndServiceAndType(testUser, ThirdPartyService.ANILIST, ContentType.ANIME))
                .thenReturn(Optional.of(tierList));

        List<TierDTO> result = tiersService.getTierlist("testuser", ThirdPartyService.ANILIST, ContentType.ANIME);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(10, result.get(0).getScore());
        assertEquals(8, result.get(1).getScore());
        assertEquals(6, result.get(2).getScore());
    }
}
