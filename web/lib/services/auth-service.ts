import { useApiMutation } from "@/lib/use-api";

export function useOauth(username: string, service: string, token: string) {
	return useApiMutation<void, { code: string }>(`/auth/oauth/${service}/${username}`, { token });
}

export function useOpenId(username: string, service: string, token: string) {
	return useApiMutation<void, { params: Record<string, string> }>(`/auth/openid/${service}/${username}`, { token });
}