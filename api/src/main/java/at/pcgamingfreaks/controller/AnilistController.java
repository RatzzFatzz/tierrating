package at.pcgamingfreaks.controller;

import at.pcgamingfreaks.model.anilist.AnilistMedia;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RestController
@RequestMapping("data")
public class AnilistController {

    @GetMapping("anilist/manga")
    public List<AnilistMedia> getManga() {
        String url = "https://graphql.anilist.co";
        String query = """
                query ExampleQuery($sort: [MediaSort], $type: MediaType, $popularityGreater: Int, $page: Int, $perPage: Int) {
                    Page(page: $page, perPage: $perPage) {
                        pageInfo {
                            currentPage
                            hasNextPage
                            perPage
                        }
                        media(sort: $sort, type: $type, popularity_greater: $popularityGreater) {
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
                """;

        WebClient webClient = WebClient.create(url);
        HttpGraphQlClient graphQlClient = HttpGraphQlClient.create(webClient);

        return graphQlClient
                .document(query)
                .variable("sort", "POPULARITY_DESC")
                .variable("type", "MANGA")
                .variable("popularityGreater", 90097)
                .variable("page", 1)
                .variable("perPage", 20)
                .retrieveSync("Page.media")
                .toEntityList(AnilistMedia.class);
    }
}
