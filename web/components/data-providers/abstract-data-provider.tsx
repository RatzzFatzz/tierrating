import {DataProvider} from "@/components/data-providers/data-provider";
import {Tier, TierlistEntry} from "@/model/types";
import {fetchTiers} from "@/components/api/tier-api";
import {fetchData, updateData} from "@/components/api/data-api";
import {UpdateScoreResponse} from "@/model/response-types";

export abstract class AbstractDataProvider implements DataProvider {

    abstract getServiceName(): string;

    abstract getTypeName(): string;

    async fetchData(token: string | null, username: string, logout: () => void): Promise<TierlistEntry[]> {
        return await fetchData(token, username, this.getServiceName(), this.getTypeName())
            .then(response => {
                if (response.status === 401 || response.status === 403) {
                    logout();
                    throw new Error("Session expired");
                }

                if (response.error) throw new Error(`API error: ${response.status}`);
                if (!response.data) throw new Error("Faulty response");

                return response.data;
            });
    }

    async fetchTierlist(token: string | null, username: string, logout: () => void): Promise<Tier[]> {
        return await fetchTiers(token, username, this.getServiceName(), this.getTypeName())
            .then(response => {
                if (response.status === 401 || response.status === 403) {
                    logout();
                    throw new Error("Session expired");
                }

                if (response.status === 404) throw new Error("User not found or user doesn't have requested service connected");
                if (response.error) throw new Error(`API error status: ${response.status}`);
                if (!response.data) throw new Error("Faulty response");

                return response.data;
            });
    }

    async updateData(id: string, score: number, token: string | null, username: string, logout: () => void): Promise<UpdateScoreResponse> {
        return updateData(id, score, this.getServiceName(), token, username)
            .then(response => {
                console.log(response.status)
                if (response.status === 401 || response.status === 403) {
                    logout()
                    throw new Error("Session expired or unauthorized");
                }
                if (response.status === 404) throw new Error("User not found or user doesn't have requested service connected");
                if (response.error) throw new Error(`API error: ${response.status}`);
                if (!response.data) throw new Error("Faulty response");

                return response.data;
            });
    }
}