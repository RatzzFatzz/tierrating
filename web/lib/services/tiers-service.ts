import { Tier } from "@/types/types";
import { useApi } from "@/lib/use-api";

export function useTiers(username: string, service: string, type: string, token?: string) {
	return useApi<Tier[]>(`/tiers/${username}/${service}/${type}`, { token });
}