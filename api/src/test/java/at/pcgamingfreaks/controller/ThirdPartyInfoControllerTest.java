package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ThirdPartyInfoResponseDTO;
import at.pcgamingfreaks.service.thirdparty.info.ThirdPartyInfoFactory;
import at.pcgamingfreaks.service.thirdparty.info.ThirdPartyInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ThirdPartyInfoController Tests")
class ThirdPartyInfoControllerTest {

    @Mock
    private ThirdPartyInfoFactory thirdPartyInfoFactory;

    @Mock
    private ThirdPartyInfoService infoService;

    @InjectMocks
    private ThirdPartyInfoController thirdPartyInfoController;

    @Test
    @DisplayName("info() should return info for ANILIST service")
    void info_shouldReturnInfoForAnilistService() {
        ThirdPartyInfoResponseDTO responseDTO = new ThirdPartyInfoResponseDTO();
        responseDTO.setClientId("anilist-client-id");
        
        when(thirdPartyInfoFactory.getProvider(ThirdPartyService.ANILIST)).thenReturn(infoService);
        when(infoService.info()).thenReturn(responseDTO);

        ResponseEntity<ThirdPartyInfoResponseDTO> response = thirdPartyInfoController.info(ThirdPartyService.ANILIST);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("anilist-client-id", response.getBody().getClientId());
        verify(thirdPartyInfoFactory).getProvider(ThirdPartyService.ANILIST);
        verify(infoService).info();
    }

    @Test
    @DisplayName("info() should return info for TRAKT service")
    void info_shouldReturnInfoForTraktService() {
        ThirdPartyInfoResponseDTO responseDTO = new ThirdPartyInfoResponseDTO();
        responseDTO.setClientId("trakt-client-id");
        
        when(thirdPartyInfoFactory.getProvider(ThirdPartyService.TRAKT)).thenReturn(infoService);
        when(infoService.info()).thenReturn(responseDTO);

        ResponseEntity<ThirdPartyInfoResponseDTO> response = thirdPartyInfoController.info(ThirdPartyService.TRAKT);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("trakt-client-id", response.getBody().getClientId());
        verify(thirdPartyInfoFactory).getProvider(ThirdPartyService.TRAKT);
        verify(infoService).info();
    }

    @Test
    @DisplayName("info() should return null clientId when not configured")
    void info_shouldReturnNullClientIdWhenNotConfigured() {
        ThirdPartyInfoResponseDTO responseDTO = new ThirdPartyInfoResponseDTO();
        
        when(thirdPartyInfoFactory.getProvider(ThirdPartyService.ANILIST)).thenReturn(infoService);
        when(infoService.info()).thenReturn(responseDTO);

        ResponseEntity<ThirdPartyInfoResponseDTO> response = thirdPartyInfoController.info(ThirdPartyService.ANILIST);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getClientId());
    }
}
