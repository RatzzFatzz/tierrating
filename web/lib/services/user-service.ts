import { useApi, useApiMutation } from "@/lib/use-api";
import { LoginResponse, SignupResponse, UserResponse } from "@/types/api-responses";
import { ChangePasswordReqest, LoginRequest, SignupRequest } from "@/types/api-requests";
import useSWRMutation from "swr/mutation";
import { ApiRequestError } from "@/types/api-request-error";
import { apiClient } from "@/lib/api-client";

export function useUser(username: string, token?: string) {
	return useApi<UserResponse>(`/user/${username}`, { token });
}

export function useLogin() {
	return useApiMutation<LoginResponse, LoginRequest>(`/auth/login`);
}

export function useSignup() {
	return useApiMutation<SignupResponse, SignupRequest>(`/auth/signup`);
}

export function useRemoveThirdPartyService(username: string, token: string) {
	// direct usage of useSWRMutation to allow the usage of a dynamic url
	return useSWRMutation<void, ApiRequestError, string, { service: string }>(`/user/${username}/remove`, async (url, { arg }) => {
		return apiClient<void>(`${url}/${arg.service}`, {
			method: "DELETE",
			token,
		});
	});
}

export function useChangePassword(token: string) {
	return useApiMutation<void, ChangePasswordReqest>("/auth/change-password", { token });
}

export function useAccountDeletion(token: string) {
	// direct usage of useSWRMutation to allow the usage of a dynamic url
	return useSWRMutation<void, ApiRequestError, string, { username: string }>(`/auth/delete-account`, async (url, { arg }) => {
		return apiClient<void>(`${url}/${arg.username}`, {
			method: "DELETE",
			token,
		});
	});
}
