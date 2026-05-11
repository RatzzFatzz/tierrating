package at.pcgamingfreaks.service.thirdparty.data.trakt;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.model.exceptions.EntryNotFoundException;
import at.pcgamingfreaks.model.exceptions.ThirdPartySyncException;
import at.pcgamingfreaks.model.exceptions.ThirdPartyUnconfiguredException;
import at.pcgamingfreaks.model.repo.TraktEntryRepository;
import at.pcgamingfreaks.model.repo.TraktEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntry;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import at.pcgamingfreaks.service.TmdbCoverFinder;
import at.pcgamingfreaks.service.thirdparty.data.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class TraktDataService implements DataService {
	protected static final String TRAKT_API = "https://api.trakt.tv";

	protected final UserRepository userRepository;
	protected final TraktEntryScoreRepository entryScoreRepository;
	protected final TraktEntryRepository entryRepository;
	protected final TmdbCoverFinder coverFinder;
	protected final ThirdPartyConfig thirdPartyConfig;
	protected final ListEntryDtoMapper listEntryDtoMapper;

	@Override
	public ThirdPartyService getService() {
		return ThirdPartyService.TRAKT;
	}

	@Override
	public List<ListEntryDTO> fetch(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
		Set<TraktEntryScore> existingScores = entryScoreRepository.findAllByUserAndEntry_TypeOrderByScoreDesc(user, getContentType());

		if (existingScores.isEmpty()) {
			pull(username);
			existingScores = entryScoreRepository.findAllByUserAndEntry_TypeOrderByScoreDesc(user, getContentType()); // TODO: improve?
		}

		return existingScores.stream().map(listEntryDtoMapper::map).toList();
	}

	@Transactional
	@Override
	public void pull(String username) {
		if (!thirdPartyConfig.getTrakt().isValid()) throw new ThirdPartySyncException("Trakt config is invalid");
		long duration = System.currentTimeMillis();
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

		List<TraktEntryScore> remoteScores = pull(user);

		// Collect all remote IDs from this sync for batch lookup
		Set<Long> remoteIds = remoteScores.stream()
				.map(s -> s.getEntry().getId())
				.collect(Collectors.toSet());

		// Fetch all existing entries and scores in bulk to avoid N+1 queries
		Map<Long, TraktEntry> existingEntries = entryRepository
				.findAllByIdIn(remoteIds)
				.stream()
				.collect(Collectors.toMap(TraktEntry::getId, Function.identity()));

		Map<Long, TraktEntryScore> existingScores = entryScoreRepository
				.findAllByUserAndEntryIdIn(user, remoteIds)
				.stream()
				.collect(Collectors.toMap(s -> s.getEntry().getId(), Function.identity()));

		List<TraktEntry> entriesToSave = new ArrayList<>();
		List<TraktEntryScore> scoresToSave = new ArrayList<>();

		for (TraktEntryScore remoteScore : remoteScores) {
			TraktEntry remoteEntry = remoteScore.getEntry();
			long entryId = remoteEntry.getId();

			// Update existing entry or use the new one
			TraktEntry entry = existingEntries.getOrDefault(entryId, new TraktEntry());
			if (!entry.equals(remoteEntry)) {
				entry.setId(remoteEntry.getId());
				entry.setTitle(remoteEntry.getTitle());
				entry.setCover(remoteEntry.getCover());
				entry.setSeason(remoteEntry.getSeason());
				entry.setType(remoteEntry.getType());
				entriesToSave.add(entry);
			}

			// Update existing score or create a new one
			TraktEntryScore entryScore = existingScores.getOrDefault(entryId, new TraktEntryScore());
			if (entryScore.getScore() != remoteScore.getScore() || entryScore.getId() == null) {
				entryScore.setScore(remoteScore.getScore());
				entryScore.setUser(user);
				entryScore.setEntry(entry);
				scoresToSave.add(entryScore);
			}
		}

		entryRepository.saveAll(entriesToSave);
		entryScoreRepository.saveAll(scoresToSave);

		log.info("Synced {} {} for {} in {}s",
				getService(),
				getContentType(),
				username,
				(System.currentTimeMillis() - duration) / 1000);
	}

	protected abstract List<TraktEntryScore> pull(User user);

	abstract protected List<?> pullRated(User user);

	abstract protected List<?> pullWatched(User user);

	@Transactional
	@Override
	public void update(long id, float score, User user) {
		if (!thirdPartyConfig.getTrakt().isValid()) throw new ThirdPartyUnconfiguredException(getService());

		TraktEntryScore entryScore = entryScoreRepository.findByUserAndEntry_Id(user, id).orElseThrow(() -> new EntryNotFoundException(getContentType(), id));
		entryScore.setScore((int) score);
		entryScoreRepository.save(entryScore);

		if (user.getConnections().get(getService()).isAutoUpdateSync()) pushSingleChange(id, score, user);
	}

	abstract protected void pushSingleChange(long id, float score, User user);

	@Override
	public void push(String username) {

	}
}
