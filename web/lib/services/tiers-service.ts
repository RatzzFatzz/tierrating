import { apiClient } from "@/lib/api-client";
import { Tier } from "@/types/types";

export const tiersService = {
	get: (username: string, service: string, type: string, token?: string) => apiClient.get<Tier[]>(`/tiers/${username}/${service}/${type}`, token),
};
