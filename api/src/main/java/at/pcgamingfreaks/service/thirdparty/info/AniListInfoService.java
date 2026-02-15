package at.pcgamingfreaks.service.thirdparty.info;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.dto.ThirdPartyInfoResponseDTO;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AniListInfoService implements ThirdPartyInfoService {
    private final ThirdPartyConfig thirdPartyConfig;

    @Override
    public ThirdPartyService getService() {
        return ThirdPartyService.ANILIST;
    }

    public ThirdPartyInfoResponseDTO info() {
        if (!thirdPartyConfig.getAnilist().isValid()) throw new ThirdPartyUnconfiguredException(getService());
        ThirdPartyInfoResponseDTO response = new ThirdPartyInfoResponseDTO();
        response.setClientId(thirdPartyConfig.getAnilist().getClient().getKey());
        return response;
    }
}
