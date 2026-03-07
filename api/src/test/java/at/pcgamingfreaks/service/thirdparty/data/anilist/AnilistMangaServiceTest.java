package at.pcgamingfreaks.service.thirdparty.data.anilist;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ContentType;
import at.pcgamingfreaks.model.repo.AniListEntryRepository;
import at.pcgamingfreaks.model.repo.AniListEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnilistMangaService Tests")
class AnilistMangaServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AniListEntryScoreRepository aniListEntryScoreRepository;

    @Mock
    private AniListEntryRepository aniListEntryRepository;

    @Mock
    private ThirdPartyConfig thirdPartyConfig;

    @Mock
    private ListEntryDtoMapper listEntryDtoMapper;

    @Test
    @DisplayName("getContentType() should return MANGA")
    void getContentType_shouldReturnManga() {
        AnilistMangaService service = new AnilistMangaService(
                userRepository, aniListEntryScoreRepository, aniListEntryRepository,
                thirdPartyConfig, listEntryDtoMapper);

        assertEquals(ContentType.MANGA, service.getContentType());
    }
}
