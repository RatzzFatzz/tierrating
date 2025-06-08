package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.mapper.ListEntryDtoMapper;
import at.pcgamingfreaks.mapper.UserDtoMapper;
import at.pcgamingfreaks.model.dto.ListEntryDTO;
import at.pcgamingfreaks.model.dto.UserDTO;
import at.pcgamingfreaks.model.anilist.AniListListEntry;
import at.pcgamingfreaks.model.anilist.AniListPage;
import at.pcgamingfreaks.model.anilist.AniListUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RestController
@RequestMapping("anilist")
@CrossOrigin
public class AniListController {
    private final String ANILIST_API_URL = "https://graphql.anilist.co";

    @GetMapping("{username}")
    public UserDTO userExists(@PathVariable String username) {
        String query = """
                query ($name: String) {
                  User(name: $name) {
                    id
                    name
                  }
                }
                """;

        AniListUser user = createGraphQlClient()
                .document(query)
                .variable("name", username)
                .retrieveSync("User")
                .toEntity(AniListUser.class);

        return user != null ? UserDtoMapper.map(user) : new UserDTO();
    }

    @GetMapping("{username}/{type}")
    public List<ListEntryDTO> getData(@PathVariable String username, @PathVariable String type) {
        String query = """
                query ($userName: String, $type: MediaType, $status: MediaListStatus, $page: Int, $perPage: Int) {
                    Page(page: $page, perPage: $perPage) {
                        pageInfo {
                            currentPage
                            hasNextPage
                            perPage
                        }
                        mediaList(userName: $userName, type: $type, status: $status) {
                            score(format: POINT_10)
                            media {
                                id
                                title {
                                    romaji
                                    english
                                    native
                                }
                                coverImage {
                                    large
                                    extraLarge
                                }
                            }
                        }
                    }
                }
                """;

        List<AniListListEntry> result = new ArrayList<>();

        long timerStart = System.currentTimeMillis();
        AniListPage page;
        int currentPage = 1;
        do {
             page = createGraphQlClient()
                    .document(query)
                    .variable("userName", username)
                    .variable("type", type.toUpperCase())
                    .variable("status", "COMPLETED")
                    .variable("page", currentPage++)
                    .variable("perPage", 50)
                    .retrieveSync("Page")
                    .toEntity(AniListPage.class);
             result.addAll(page.getMediaList());
        } while (page.getPageInfo().isHasNextPage());
        log.info("Getting data in {}ms", System.currentTimeMillis() - timerStart);

        return result.stream().map(ListEntryDtoMapper::map).toList();
    }

    private HttpGraphQlClient createGraphQlClient() {
        WebClient webClient = WebClient.create(ANILIST_API_URL);
        return HttpGraphQlClient.create(webClient);
    }
}
