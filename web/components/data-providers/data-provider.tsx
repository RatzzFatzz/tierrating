import {TierlistEntry, Tier} from "@/model/types";
import {AnilistAnimeProvider} from "@/components/data-providers/anilist-anime-data-provider";
import {AnilistMangaProvider} from "@/components/data-providers/anilist-manga-data-provider";
import {UpdateScoreResponse} from "@/model/response-types";

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
}

export function getProviderByName(name: string) : DataProvider | undefined {
    return providers[name];
}