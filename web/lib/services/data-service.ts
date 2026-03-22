import { apiClient } from "@/lib/api-client";
import { TierlistEntry } from "@/types/types";
import { DataUpdateRequest } from "@/types/data-types";

export const dataService = {
	fetchEntries: (username: string, service: string, type: string, token?: string) =>
		apiClient.get<TierlistEntry[]>(`/data/fetch/${username}/${service}/${type}`, token),

	updateEntry: (token: string, body: DataUpdateRequest) => apiClient.put(`/data/update/`, token, body),

	pullUpdate: (username: string, service: string, type: string, token: string) =>
		apiClient.get(`/data/pull/${username}/${service}/${type}`, token),
};
