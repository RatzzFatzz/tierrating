import { UserResponse } from "@/types/response-types";
import { apiClient } from "@/lib/api-client";

export const userService = {
	get: (username: string, token?: string) => apiClient.get<UserResponse>(`/user/${username}`, token),
};