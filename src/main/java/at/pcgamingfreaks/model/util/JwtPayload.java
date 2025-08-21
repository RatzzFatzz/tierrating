package at.pcgamingfreaks.model.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtPayload {
    private long aud;
    private String jti;
    private long iat;
    private long nbf;
    private long exp;
    @JsonProperty("sub")
    private long userId;
}
