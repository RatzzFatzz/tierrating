import {AnilistDataProvider} from "@/components/data-providers/anilist/anilist-data-provider";

export class AnilistMangaProvider extends AnilistDataProvider {
    getTypeName(): string {
        return "manga";
    }
}