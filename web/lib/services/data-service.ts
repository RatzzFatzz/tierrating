import { TierlistEntry } from "@/types/types";
import { useApi, useApiMutation } from "@/lib/use-api";

export function useTierlistEntries(username: string, service: string, type: string, token?: string) {
	return useApi<TierlistEntry[]>(`/data/fetch/${username}/${service}/${type}`, { token });
}

export function useScoreMutation(username: string, service: string, type: string, token: string) {
	return useApiMutation<void, { id: string; score: number }>(
		`/data/update/${username}/${service}/${type}`,
		{
			token,
		}
	);
}

export function useThirdPartyDataPull(username: string, service: string, type: string, token: string) {
	return useApiMutation<void, void>(`/data/pull/${username}/${service}/${type}`, { token });
}
