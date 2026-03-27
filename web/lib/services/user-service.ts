import { UserResponse } from "@/types/response-types";
import { useApi, useApiMutation } from "@/lib/use-api";
import { LoginResponse, SignupResponse } from "@/types/api-responses";
import { LoginRequest, SignupRequest } from "@/types/api-requests";
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

export function useRemoveThirdPartyService(username: string, token:  string)  {
	// direct usage os useSWRMutation to allow the usage of a dynamic url
	return useSWRMutation<void, ApiRequestError, string, { service: string }>(`/user/${username}/remove`, async (url, { arg }) => {
		return apiClient<void>(`${url}/${arg.service}`, {
			method: "DELETE",
			token
		});
	});
}