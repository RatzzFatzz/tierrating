import {TierlistEntry, Tier} from "@/model/types";
import {AnilistAnimeProvider} from "@/components/data-providers/anilist/anilist-anime-data-provider";
import {AnilistMangaProvider} from "@/components/data-providers/anilist/anilist-manga-data-provider";
import {UpdateScoreResponse} from "@/model/response-types";
import {TraktMoviesDataProvider} from "@/components/data-providers/trakt/trakt-movies-data-provider";
import {TraktTvShowsDataProvider} from "@/components/data-providers/trakt/trakt-tv-shows-data-provider";
import {TraktTvShowsSeasonsDataProvider} from "@/components/data-providers/trakt/trakt-tvshows-seasons-data-provider";

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
    'trakt-movies': new TraktMoviesDataProvider(),
    'trakt-tvshows': new TraktTvShowsDataProvider(),
    'trakt-tvshows-seasons': new TraktTvShowsSeasonsDataProvider(),
}

export function getProviderByName(name: string) : DataProvider {
    const provider = providers[name];
    if (!provider) throw new Error(`Invalid provider: ${name}`);
    return provider;
}