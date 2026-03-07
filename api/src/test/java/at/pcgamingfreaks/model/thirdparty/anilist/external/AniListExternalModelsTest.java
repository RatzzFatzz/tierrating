package at.pcgamingfreaks.model.thirdparty.anilist.external;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AniList External Model Tests")
class AniListExternalModelsTest {

    @Test
    @DisplayName("AniListMediaTitle should store and return values")
    void aniListMediaTitle_shouldStoreValues() {
        AniListMediaTitle title = new AniListMediaTitle();
        title.setRomaji("Shingeki no Kyojin");
        title.setEnglish("Attack on Titan");

        assertEquals("Shingeki no Kyojin", title.getRomaji());
        assertEquals("Attack on Titan", title.getEnglish());
    }

    @Test
    @DisplayName("AniListMediaCoverImage should store and return values")
    void aniListMediaCoverImage_shouldStoreValues() {
        AniListMediaCoverImage cover = new AniListMediaCoverImage();
        cover.setLarge("https://large.jpg");
        cover.setExtraLarge("https://extralarge.jpg");

        assertEquals("https://large.jpg", cover.getLarge());
        assertEquals("https://extralarge.jpg", cover.getExtraLarge());
    }

    @Test
    @DisplayName("AniListMedia should store and return values")
    void aniListMedia_shouldStoreValues() {
        AniListMediaTitle title = new AniListMediaTitle();
        title.setEnglish("Naruto");
        AniListMediaCoverImage cover = new AniListMediaCoverImage();
        cover.setLarge("https://cover.jpg");

        AniListMedia media = new AniListMedia();
        media.setId(123L);
        media.setTitle(title);
        media.setCoverImage(cover);

        assertEquals(123L, media.getId());
        assertEquals("Naruto", media.getTitle().getEnglish());
        assertEquals("https://cover.jpg", media.getCoverImage().getLarge());
    }

    @Test
    @DisplayName("AniListListEntry should store and return values")
    void aniListListEntry_shouldStoreValues() {
        AniListMedia media = new AniListMedia();
        media.setId(42L);

        AniListListEntry entry = new AniListListEntry();
        entry.setScore(8.5f);
        entry.setMedia(media);

        assertEquals(8.5f, entry.getScore());
        assertEquals(42L, entry.getMedia().getId());
    }

    @Test
    @DisplayName("AniListPageInfo should store and return values")
    void aniListPageInfo_shouldStoreValues() {
        AniListPageInfo pageInfo = new AniListPageInfo();
        pageInfo.setHasNextPage(true);
        pageInfo.setCurrentPage(2);
        pageInfo.setPerPage(50);

        assertTrue(pageInfo.isHasNextPage());
        assertEquals(2, pageInfo.getCurrentPage());
        assertEquals(50, pageInfo.getPerPage());
    }

    @Test
    @DisplayName("AniListPage should store and return values")
    void aniListPage_shouldStoreValues() {
        AniListPageInfo pageInfo = new AniListPageInfo();
        pageInfo.setHasNextPage(false);

        AniListListEntry entry = new AniListListEntry();
        entry.setScore(7.0f);

        AniListPage page = new AniListPage();
        page.setPageInfo(pageInfo);
        page.setMediaList(List.of(entry));

        assertFalse(page.getPageInfo().isHasNextPage());
        assertEquals(1, page.getMediaList().size());
        assertEquals(7.0f, page.getMediaList().get(0).getScore());
    }

    @Test
    @DisplayName("AniListUser should store and return values")
    void aniListUser_shouldStoreValues() {
        AniListUser user = new AniListUser();
        user.id = 999L;
        user.name = "testuser";

        assertEquals(999L, user.id);
        assertEquals("testuser", user.name);
    }
}
