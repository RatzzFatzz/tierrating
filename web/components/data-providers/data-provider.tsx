import {TierlistEntry, Tier} from "@/model/types";
import {AnilistAnimeProvider} from "@/components/data-providers/anilist-anime-data-provider";
import {AnilistMangaProvider} from "@/components/data-providers/anilist-manga-data-provider";

export interface DataProvider {
    getServiceName: () => string;
    getTypeName: () => string;
    fetchData: (token: string | null, username: string | null, logout: () => void) => Promise<TierlistEntry[]>
    fetchTierlist: (token: string | null, username: string | null, logout: () => void) => Promise<Tier[]>
}

const providers: Record<string, DataProvider> = {
    'anilist-anime': new AnilistAnimeProvider(),
    'anilist-manga': new AnilistMangaProvider(),
}

export function getProviderByName(name: string) : DataProvider | undefined {
    return providers[name];
}