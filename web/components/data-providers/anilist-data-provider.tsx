import {DataProvider} from "@/components/data-providers/data-provider";
import {Tier, TierlistEntry} from "@/model/types";
import {fetchTiers} from "@/components/api/tier-api";
import {fetchData} from "@/components/api/data-api";

export abstract class AnilistDataProvider implements DataProvider {
    getServiceName(): string {
        return "anilist";
    }

    abstract getTypeName(): string;

    async fetchData(token: string | null, username: string | null, logout: () => void): Promise<TierlistEntry[]> {
        return await fetchData(token, username, this.getServiceName(), this.getTypeName(), logout);
    }

    async fetchTierlist(token: string | null, username: string | null, logout: () => void): Promise<Tier[]> {
        return await fetchTiers(token, this.getServiceName(), this.getTypeName(), logout);
    }

}