package at.pcgamingfreaks.mapper;

import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntry;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntryScore;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntry;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListEntryDtoMapper Tests")
class ListEntryDtoMapperTest {

    @InjectMocks
    private ListEntryDtoMapper mapper;

    @Test
    @DisplayName("map(AniListEntryScore) should map all fields correctly")
    void map_shouldMapAniListEntryScoreToDto() {
        AniListEntry entry = mock(AniListEntry.class);
        when(entry.getId()).thenReturn(12345L);
        when(entry.getTitle()).thenReturn("Test Anime");
        when(entry.getCover()).thenReturn("https://example.com/cover.jpg");

        AniListEntryScore entryScore = mock(AniListEntryScore.class);
        when(entryScore.getEntry()).thenReturn(entry);
        when(entryScore.getScore()).thenReturn(8.5f);

        ListEntryDTO dto = mapper.map(entryScore);

        assertNotNull(dto);
        assertEquals(12345L, dto.getId());
        assertEquals(8.5f, dto.getScore());
        assertEquals("Test Anime", dto.getTitle());
        assertEquals("https://example.com/cover.jpg", dto.getCover());
    }

    @Test
    @DisplayName("map(AniListEntryScore) should use romaji title when title is blank")
    void map_shouldUseRomajiTitleWhenTitleIsBlank() {
        AniListEntry entry = mock(AniListEntry.class);
        when(entry.getId()).thenReturn(12345L);
        when(entry.getTitle()).thenReturn("");
        when(entry.getTitleRomaji()).thenReturn("Romaji Title");
        when(entry.getCover()).thenReturn("https://example.com/cover.jpg");

        AniListEntryScore entryScore = mock(AniListEntryScore.class);
        when(entryScore.getEntry()).thenReturn(entry);
        when(entryScore.getScore()).thenReturn(7.0f);

        ListEntryDTO dto = mapper.map(entryScore);

        assertNotNull(dto);
        assertEquals("Romaji Title", dto.getTitle());
    }

    @Test
    @DisplayName("map(AniListEntryScore) should use romaji title when title is null")
    void map_shouldUseRomajiTitleWhenTitleIsNull() {
        AniListEntry entry = mock(AniListEntry.class);
        when(entry.getId()).thenReturn(12345L);
        when(entry.getTitle()).thenReturn(null);
        when(entry.getTitleRomaji()).thenReturn("Romaji Title");
        when(entry.getCover()).thenReturn("https://example.com/cover.jpg");

        AniListEntryScore entryScore = mock(AniListEntryScore.class);
        when(entryScore.getEntry()).thenReturn(entry);
        when(entryScore.getScore()).thenReturn(7.0f);

        ListEntryDTO dto = mapper.map(entryScore);

        assertNotNull(dto);
        assertEquals("Romaji Title", dto.getTitle());
    }

    @Test
    @DisplayName("map(TraktEntryScore) should map all fields correctly")
    void map_shouldMapTraktEntryScoreToDto() {
        TraktEntry entry = mock(TraktEntry.class);
        when(entry.getId()).thenReturn(67890L);
        when(entry.getTitle()).thenReturn("Test Movie");
        when(entry.getCover()).thenReturn("https://example.com/movie.jpg");

        TraktEntryScore entryScore = mock(TraktEntryScore.class);
        when(entryScore.getEntry()).thenReturn(entry);
        when(entryScore.getScore()).thenReturn(9);

        ListEntryDTO dto = mapper.map(entryScore);

        assertNotNull(dto);
        assertEquals(67890L, dto.getId());
        assertEquals(9.0f, dto.getScore());
        assertEquals("Test Movie", dto.getTitle());
        assertEquals("https://example.com/movie.jpg", dto.getCover());
    }

    @Test
    @DisplayName("map(TraktEntryScore) should handle null string values gracefully")
    void map_shouldHandleNullTraktValues() {
        TraktEntry entry = mock(TraktEntry.class);
        when(entry.getId()).thenReturn(0L);
        when(entry.getTitle()).thenReturn(null);
        when(entry.getCover()).thenReturn(null);

        TraktEntryScore entryScore = mock(TraktEntryScore.class);
        when(entryScore.getEntry()).thenReturn(entry);
        when(entryScore.getScore()).thenReturn(0);

        ListEntryDTO dto = mapper.map(entryScore);

        assertNotNull(dto);
        assertEquals(0L, dto.getId());
        assertEquals(0f, dto.getScore());
        assertNull(dto.getTitle());
        assertNull(dto.getCover());
    }
}
