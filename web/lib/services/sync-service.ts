import { SyncStatusResponse, SyncType } from "@/types/responses/sync-responses";
import { useApi, useApiMutation } from "@/lib/use-api";
import { SWRConfiguration } from "swr";

export function useSyncStatus(source: string, type: string, swrConfig: SWRConfiguration, token?: string) {
	return useApi<SyncStatusResponse>(`/sync/${source}/${type}/status`, { token }, swrConfig);
}

export function useEnqueueSync(source: string, type: string, token?: string) {
	return useApiMutation<void, {type: SyncType}>(`/sync/${source}/${type}`, { token });
}
