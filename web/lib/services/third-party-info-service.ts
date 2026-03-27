import { useApi } from "@/lib/use-api";
import { ThirdPartyInfoResponse } from "@/types/api-responses";

export function useThirdPartyServiceInfo(service: string, token?: string) {
	return useApi<ThirdPartyInfoResponse>(`/info/${service}`, { token });
}

export function useThirdPartyServices(token?: string) {
	return useApi<string[]>(`/info/services`, { token });
}