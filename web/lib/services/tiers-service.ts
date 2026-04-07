import { Tier } from "@/types/types";
import { useApi, useApiMutation } from "@/lib/use-api";

export function useTiersOnDemand(disabled: boolean, username: string, service: string, type: string, token?: string) {
	return useApi<Tier[]>(!disabled ? `/tiers/${username}/${service}/${type}` : null, { token });
}

export function useTiers(username: string, service: string, type: string, token?: string) {
	return useTiersOnDemand(false, username, service, type, token);
}

export function useTiersUpdate(username: string, service: string, type: string, token: string) {
	return useApiMutation<void, { tiers: Tier[] }>(`/tiers/${username}/${service}/${type}`, { token });
}