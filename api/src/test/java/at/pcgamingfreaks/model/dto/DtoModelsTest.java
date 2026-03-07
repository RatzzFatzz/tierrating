package at.pcgamingfreaks.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTO Model Tests")
class DtoModelsTest {

    @Test
    @DisplayName("TmdbInfoRequest getters and setters work correctly")
    void tmdbInfoRequest_gettersSetters() {
        TmdbInfoRequest request = new TmdbInfoRequest();
        request.setPosterPath("/path/to/poster.jpg");
        assertEquals("/path/to/poster.jpg", request.getPosterPath());
    }

    @Test
    @DisplayName("TmdbInfoRequest default posterPath is null")
    void tmdbInfoRequest_defaultIsNull() {
        TmdbInfoRequest request = new TmdbInfoRequest();
        assertNull(request.getPosterPath());
    }

    @Test
    @DisplayName("AuthTokenResponseDTO getters and setters work correctly")
    void authTokenResponseDTO_gettersSetters() {
        AuthTokenResponseDTO dto = new AuthTokenResponseDTO();
        dto.setExpiresIn(3600);
        dto.setAccessToken("access-token");
        dto.setRefreshToken("refresh-token");

        assertEquals(3600, dto.getExpiresIn());
        assertEquals("access-token", dto.getAccessToken());
        assertEquals("refresh-token", dto.getRefreshToken());
    }

    @Test
    @DisplayName("AuthTokenResponseDTO defaults are zero/null")
    void authTokenResponseDTO_defaults() {
        AuthTokenResponseDTO dto = new AuthTokenResponseDTO();
        assertEquals(0, dto.getExpiresIn());
        assertNull(dto.getAccessToken());
        assertNull(dto.getRefreshToken());
    }
}
