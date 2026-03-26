import { UserResponse } from "@/types/response-types";
import { useApi, useApiMutation } from "@/lib/use-api";
import { LoginResponse } from "@/types/api-responses";
import { LoginRequest } from "@/types/api-requests";

export function useUser(username: string, token?: string) {
	return useApi<UserResponse>(`/user/${username}`, { token });
}

export function useLogin() {
	return useApiMutation<LoginResponse, LoginRequest>(`/auth/login`)
}