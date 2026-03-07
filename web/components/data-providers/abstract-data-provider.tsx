import { DataProvider } from "@/components/data-providers/data-provider";
import { Tier, TierlistEntry } from "@/components/model/types";
import { fetchTiers } from "@/components/api/tier-api";
import { fetchData, pullData, updateData } from "@/components/api/data-api";
import { getDefaultTiers } from "@/components/model/defaults";

export abstract class AbstractDataProvider implements DataProvider {
	abstract getServiceName(): string;

	abstract getTypeName(): string;

	async fetchData(token: string | null, username: string, logout: () => void): Promise<TierlistEntry[]> {
		return await fetchData(token, username, this.getServiceName(), this.getTypeName()).then((response) => {
			if (response.status === 401 || response.status === 403) {
				logout();
				throw new Error("Session expired");
			}

			if (response.status === 404) throw new Error("User not found or user doesn't have requested service connected");
			if (response.error) throw new Error(`API error: ${response.status}`);
			if (!response.data) throw new Error("Faulty response");

			return response.data;
		});
	}

	async fetchTierlist(token: string | null, username: string, logout: () => void): Promise<Tier[]> {
		return await fetchTiers(token, username, this.getServiceName(), this.getTypeName()).then((response) => {
			if (response.status === 401 || response.status === 403) {
				logout();
				throw new Error("Session expired");
			}

			if (response.status === 404) return getDefaultTiers();
			if (response.error) throw new Error(`API error status: ${response.status}`);
			if (!response.data) throw new Error("Faulty response");

			return response.data;
		});
	}

	async updateData(id: string, score: number, token: string | null, username: string, logout: () => void): Promise<void> {
		return updateData(id, score, this.getServiceName(), this.getTypeName(), token, username).then((response) => {
			if (response.status === 401 || response.status === 403) {
				logout();
				throw new Error("Session expired or unauthorized");
			}
			if (response.status != 200) throw new Error(response.data ? response.data.message : `API error: ${response.status}`);

			return;
		});
	}

	async pullData(token: string | null, username: string, logout: () => void): Promise<void> {
		return pullData(token, username, this.getServiceName(), this.getTypeName()).then((response) => {
			if (response.status === 401 || response.status === 403) {
				logout();
				throw new Error("Session expired");
			}
			if (response.status != 200) throw new Error(response.data ? response.data.message : `API error: ${response.status}`);

			return;
		});
	}
}
