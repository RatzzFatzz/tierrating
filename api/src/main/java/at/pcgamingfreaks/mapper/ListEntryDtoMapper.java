package at.pcgamingfreaks.mapper;

import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.model.thirdparty.anilist.AniListEntryScore;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListEntryDtoMapper {
	public ListEntryDTO map(AniListEntryScore entryScore) {
		ListEntryDTO dto = new ListEntryDTO();
		dto.setId(entryScore.getEntry().getId());
		dto.setScore(entryScore.getScore());
		dto.setTitle(Strings.isNotBlank(entryScore.getEntry().getTitle()) ? entryScore.getEntry().getTitle() : entryScore.getEntry().getTitleRomaji());
		dto.setCover(entryScore.getEntry().getCover());
		return dto;
	}

	public ListEntryDTO map(TraktEntryScore entryScore) {
		ListEntryDTO dto = new ListEntryDTO();
		dto.setId(entryScore.getEntry().getId());
		dto.setScore(entryScore.getScore());
		dto.setTitle(entryScore.getEntry().getTitle());
		dto.setCover(entryScore.getEntry().getCover());
		return dto;
	}
}
