package at.pcgamingfreaks.model.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtPayload Tests")
class JwtPayloadTest {

    @Test
    @DisplayName("getters and setters should work correctly")
    void gettersAndSetters_shouldWork() {
        JwtPayload payload = new JwtPayload();
        payload.setAud(1L);
        payload.setJti("jti-value");
        payload.setIat(1000L);
        payload.setNbf(1000L);
        payload.setExp(9999L);
        payload.setUserId(42L);

        assertEquals(1L, payload.getAud());
        assertEquals("jti-value", payload.getJti());
        assertEquals(1000L, payload.getIat());
        assertEquals(1000L, payload.getNbf());
        assertEquals(9999L, payload.getExp());
        assertEquals(42L, payload.getUserId());
    }
}
