package at.pcgamingfreaks.service.thirdparty.auth;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.ThirdPartyConnection;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.AuthTokenResponseDTO;
import at.pcgamingfreaks.model.dto.ThirdPartyAuthRequestDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyAuthenticationException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.ThirdPartyConnectionRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.util.JwtPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AniListAuthenticatorService implements ThirdPartyAuthenticatorService {
    private final UserRepository userRepository;
    private final ThirdPartyConnectionRepository thirdPartyConnectionRepository;
    private final ObjectMapper objectMapper;
    private final ThirdPartyConfig thirdPartyConfig;

    @Override
    public ThirdPartyService getService() {
        return ThirdPartyService.ANILIST;
    }

    @Override
    public void auth(String username, ThirdPartyAuthRequestDTO request) {
        if (!thirdPartyConfig.getAnilist().isValid()) throw new ThirdPartyUnconfiguredException(ThirdPartyService.ANILIST);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        if (user.getConnections().get(ThirdPartyService.ANILIST) != null) throw new ThirdPartyAuthenticationException("Already authenticated");

        try {

            AuthTokenResponseDTO tokenResponse = auth(request.getCode());

            ThirdPartyConnection connection = new ThirdPartyConnection();
            connection.setService(ThirdPartyService.ANILIST);
            connection.setAccessToken(tokenResponse.getAccessToken());
            connection.setRefreshToken(tokenResponse.getRefreshToken());
            connection.setExpiresOn(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn()));
            connection.setThirdPartyUserId(String.valueOf(extractUserIdFrom(connection.getAccessToken())));
            connection.setUser(user);
            thirdPartyConnectionRepository.save(connection);
        } catch (Exception e) {
            throw new ThirdPartyAuthenticationException(e);
        }
    }

    private AuthTokenResponseDTO auth(String code) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", "authorization_code");
        requestBody.put("client_id", thirdPartyConfig.getAnilist().getClient().getKey());
        requestBody.put("client_secret", thirdPartyConfig.getAnilist().getClient().getSecret());
        requestBody.put("redirect_uri", thirdPartyConfig.getAnilist().getRedirectUrl());
        requestBody.put("code", code);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<AuthTokenResponseDTO> tokenResponse = restTemplate.exchange(
                "https://anilist.co/api/v2/oauth/token",
                HttpMethod.POST,
                entity,
                AuthTokenResponseDTO.class
        );

        if (!tokenResponse.hasBody() || tokenResponse.getBody() == null)
            throw new RuntimeException("AniList OAuth responded with empty body");

        return tokenResponse.getBody();
    }

    private long extractUserIdFrom(String jwt) throws IOException {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = jwt.split("\\.");
        return objectMapper.readValue(decoder.decode(chunks[1]), JwtPayload.class).getUserId();
    }
}
