import {TierlistEntry, Tier} from "@/model/types";
import {AnilistAnimeProvider} from "@/components/data-providers/anilist-anime-data-provider";
import {AnilistMangaProvider} from "@/components/data-providers/anilist-manga-data-provider";
import {UpdateScoreResponse} from "@/model/response-types";
import {TraktMovieDataProvider} from "@/components/data-providers/trakt-movie-data-provider";
import {TraktTvshowDataProvider} from "@/components/data-providers/trakt-tvshow-data-provider";

export interface DataProvider {
    getServiceName: () => string;
    getTypeName: () => string;
    fetchData: (token: string | null, username: string, logout: () => void) => Promise<TierlistEntry[]>
    fetchTierlist: (token: string | null, username: string, logout: () => void) => Promise<Tier[]>
    updateData: (id: string, rating: number, token: string | null, username: string, logout: () => void) => Promise<UpdateScoreResponse>
}

const providers: Record<string, DataProvider> = {
    'anilist-anime': new AnilistAnimeProvider(),
    'anilist-manga': new AnilistMangaProvider(),
    'trakt-movies': new TraktMovieDataProvider(),
    'trakt-tvshows': new TraktTvshowDataProvider(),
}

export function getProviderByName(name: string) : DataProvider {
    const provider = providers[name];
    if (!provider) throw new Error(`Invalid provider: ${name}`);
    return provider;
}