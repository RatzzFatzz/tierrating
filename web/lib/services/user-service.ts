import { UserResponse } from "@/types/response-types";
import { useApi } from "@/lib/use-api";

export function useUser(username: string, token?: string) {
	return useApi<UserResponse>(`/user/${username}`, { token });
}