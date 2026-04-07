import { useApiMutation } from "@/lib/use-api";
import { LoginResponse } from "@/types/api-responses";
import useSWRMutation from "swr/mutation";
import { ApiRequestError } from "@/types/api-request-error";
import { apiClient } from "@/lib/api-client";

export function useOauth(username: string, service: string, token: string) {
	return useApiMutation<void, { code: string }>(`/auth/oauth/${service}/${username}`, { token });
}

export function useOpenId(username: string, service: string, token: string) {
	return useApiMutation<void, { params: Record<string, string> }>(`/auth/openid/${service}/${username}`, { token });
}

export function useRefreshToken() {
	// direct usage of useSWRMutation to allow usage of token from args; hacky workaround solution until better auth is implemented
	return useSWRMutation<LoginResponse, ApiRequestError, string, { token: string }>(`/auth/refresh`, async (url, { arg }) => {
		return apiClient<LoginResponse>(url, {
			method: "POST",
			token: arg.token,
			body: arg,
		});
	});
}