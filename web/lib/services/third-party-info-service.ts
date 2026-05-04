import { useApi } from "@/lib/use-api";
import { ThirdPartyInfoResponse } from "@/types/api-responses";

export function useThirdPartyServiceInfo(disabled: boolean, service: string, token?: string) {
	return useApi<ThirdPartyInfoResponse>(!disabled ? `/info/${service}` : null, { token });
}

export function useThirdPartyServices(token?: string) {
	return useApi<string[]>(`/info/services`, { token });
}
