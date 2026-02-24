package at.pcgamingfreaks.service.thirdparty.data.trakt;

import at.pcgamingfreaks.config.ThirdPartyConfig;
import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.model.ThirdPartyService;
import at.pcgamingfreaks.model.auth.User;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.model.repo.TraktEntryScoreRepository;
import at.pcgamingfreaks.model.repo.UserRepository;
import at.pcgamingfreaks.model.thirdparty.trakt.TraktEntryScore;
import at.pcgamingfreaks.service.TmdbCoverFinder;
import at.pcgamingfreaks.service.thirdparty.data.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class TraktDataService implements DataService {
    protected final UserRepository userRepository;
    protected final TraktEntryScoreRepository entryScoreRepository;
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

    @Override
    public void pull(String username) {
        if (!thirdPartyConfig.getTrakt().isValid()) throw new RuntimeException("Trakt config is invalid");
        long duration = System.currentTimeMillis();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        entryScoreRepository.saveAll(pull(user));

        log.info("Fetched {} {} for {} in {}s",
                getService(),
                getContentType(),
                username,
                (System.currentTimeMillis() - duration) / 1000);
    }

    protected abstract List<TraktEntryScore> pull(User user);

    abstract protected List<?> pullRated(User user);

    abstract protected List<?> pullWatched(User user);

    @Override
    public void push(String username) {

    }
}
