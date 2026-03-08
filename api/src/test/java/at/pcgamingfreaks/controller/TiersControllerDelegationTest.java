package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.TierDTO;
import at.pcgamingfreaks.service.TiersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TiersController Delegation Tests")
class TiersControllerDelegationTest {

    @Mock
    private TiersService tiersService;

    @InjectMocks
    private TiersController tiersController;

    @Test
    @DisplayName("getTierlist() should return tier list from service")
    void getTierlist_shouldReturnTierListFromService() {
        TierDTO tier = new TierDTO(UUID.randomUUID(), "#FF0000", "S", 10, 10);
        when(tiersService.getTierlist("user", ThirdPartyService.ANILIST, ContentType.ANIME))
                .thenReturn(List.of(tier));

        ResponseEntity<List<TierDTO>> response = tiersController.getTierlist(
                "user", ThirdPartyService.ANILIST, ContentType.ANIME);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(tiersService).getTierlist("user", ThirdPartyService.ANILIST, ContentType.ANIME);
    }

    @Test
    @DisplayName("getTierlist() should return empty list when no tiers")
    void getTierlist_shouldReturnEmptyList() {
        when(tiersService.getTierlist("user", ThirdPartyService.TRAKT, ContentType.MOVIES))
                .thenReturn(List.of());

        ResponseEntity<List<TierDTO>> response = tiersController.getTierlist(
                "user", ThirdPartyService.TRAKT, ContentType.MOVIES);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("setTierlist() should delegate to service")
    void setTierlist_shouldDelegateToService() {
        List<TierDTO> tiers = List.of(new TierDTO(UUID.randomUUID(), "#FF0000", "S", 10, 10));
        doNothing().when(tiersService).updateTierlist("user", ThirdPartyService.ANILIST, ContentType.ANIME, tiers);

        assertDoesNotThrow(() ->
                tiersController.setTierlist("user", ThirdPartyService.ANILIST, ContentType.ANIME, tiers));

        verify(tiersService).updateTierlist("user", ThirdPartyService.ANILIST, ContentType.ANIME, tiers);
    }
}
