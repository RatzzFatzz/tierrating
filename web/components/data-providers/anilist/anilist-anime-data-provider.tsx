import {AnilistDataProvider} from "@/components/data-providers/anilist/anilist-data-provider";

export class AnilistAnimeProvider extends AnilistDataProvider {
    getTypeName(): string {
        return "anime";
    }
}