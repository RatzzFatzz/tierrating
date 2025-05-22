package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.anilist.AniListListEntry;
import at.pcgamingfreaks.model.anilist.AniListPage;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RestController
@RequestMapping("data")
@CrossOrigin
public class DataController {

    @GetMapping("anilist/{username}/{type}")
    public List<AniListListEntry> getDataFromAnilist(@PathVariable String username, @PathVariable String type) {
        String url = "https://graphql.anilist.co";
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

        WebClient webClient = WebClient.create(url);
        HttpGraphQlClient graphQlClient = HttpGraphQlClient.create(webClient);

        List<AniListListEntry> result = new ArrayList<>();

        AniListPage page;
        int currentPage = 1;
        do {
             page = graphQlClient
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

        return result;
    }
}
