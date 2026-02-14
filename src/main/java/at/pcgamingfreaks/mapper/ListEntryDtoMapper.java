package at.pcgamingfreaks.mapper;

import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntryScore;
import at.pcgamingfreaks.model.thirdparty.anilist.external.AniListListEntry;
import at.pcgamingfreaks.model.thirdparty.anilist.external.AniListMediaCoverImage;
import at.pcgamingfreaks.model.thirdparty.anilist.external.AniListMediaTitle;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.service.TmdbCoverFinder;
import com.uwetrottmann.trakt5.entities.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListEntryDtoMapper {
    private final TmdbCoverFinder tmdbCoverFinder;

    public ListEntryDTO map(AniListEntryScore entryScore) {
        ListEntryDTO dto = new ListEntryDTO();
        dto.setId(entryScore.getEntry().getId());
        dto.setScore(entryScore.getScore());
        dto.setTitle(Strings.isNotBlank(entryScore.getEntry().getTitle()) ? entryScore.getEntry().getTitle() : entryScore.getEntry().getTitleRomaji());
        dto.setCover(entryScore.getEntry().getCover());
        return dto;
    }

    public ListEntryDTO map(Object object) {
        return new ListEntryDTO();
    }

    public ListEntryDTO map(BaseRatedEntity entry) {
        if (entry instanceof RatedMovie) return map((RatedMovie) entry);
        if (entry instanceof RatedSeason) return map((RatedSeason) entry);
        if (entry instanceof RatedShow) return map((RatedShow) entry);
        throw new RuntimeException("Invalid entry type");
    }

    public ListEntryDTO map(RatedMovie entry) {
        ListEntryDTO dto = new ListEntryDTO();
        dto.setId(entry.movie.ids.trakt);
        dto.setTitle(entry.movie.title);
        dto.setCover(tmdbCoverFinder.findMovie(entry.movie.ids.tmdb));
        dto.setScore(entry.rating.value);
        return dto;
    }

    public ListEntryDTO map(BaseMovie entry) {
        ListEntryDTO dto = new ListEntryDTO();
        dto.setId(entry.movie.ids.trakt);
        dto.setTitle(entry.movie.title);
        dto.setCover(tmdbCoverFinder.findMovie(entry.movie.ids.tmdb));
        dto.setScore(0);
        return dto;
    }

    public ListEntryDTO map(RatedShow entry) {
        ListEntryDTO dto = new ListEntryDTO();
        dto.setId(entry.show.ids.trakt);
        dto.setTitle(entry.show.title);
        dto.setCover(tmdbCoverFinder.findShow(entry.show.ids.tmdb));
        dto.setScore(entry.rating.value);
        return dto;
    }

    public ListEntryDTO map(BaseShow entry) {
        ListEntryDTO dto = new ListEntryDTO();
        dto.setId(entry.show.ids.trakt);
        dto.setTitle(entry.show.title);
        dto.setCover(tmdbCoverFinder.findShow(entry.show.ids.tmdb));
        dto.setScore(0);
        return dto;
    }

    private ListEntryDTO map(RatedSeason entry) {
        ListEntryDTO dto = new ListEntryDTO();
        dto.setId(entry.season.ids.trakt);
        dto.setTitle(String.format("%s %s", entry.show.title, entry.season.title));
        dto.setCover(tmdbCoverFinder.findSeason(entry.show.ids.tmdb, entry.season.number));
        dto.setScore(entry.rating.value);
        return dto;
    }
}
