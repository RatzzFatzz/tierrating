import {DataProvider} from "@/components/data-providers/data-provider";
import {Tier, TierlistEntry, UpdateResponse} from "@/model/types";
import {fetchTiers} from "@/components/api/tier-api";
import {fetchData, updateData} from "@/components/api/data-api";

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

    async updateData(id: string, score: number, token: string | null, username: string | null): Promise<UpdateResponse> {
        return await updateData(id, score, this.getServiceName(), token, username);
    }
}