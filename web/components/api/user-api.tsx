"use server";
import { API_URL } from "@/lib/config/global-config";
import { ErrorResponseDTO, ServerResponse, UserResponse } from "@/types/response-types";
import { LoginResponse } from "@/types/api-responses";


export async function refreshToken(token: string | null): Promise<ServerResponse<LoginResponse>> {
	if (!token) throw new Error("No authentication token");

	try {
		const response = await fetch(`${API_URL}/auth/refresh`, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
				Authorization: `Bearer ${token}`,
			},
			body: JSON.stringify({ token }),
		});

		const data = await response.json().catch(() => null);
		return { data, status: response.status };
	} catch (error) {
		console.error("API proxy error: ", error);
		return { error: "Server unavailable", status: 500 };
	}
}

export async function changePassword(
	oldPassword: string,
	newPassword: string,
	username: string | null,
	token: string | null
): Promise<ServerResponse<ErrorResponseDTO>> {
	if (!username || !token) throw new Error("Invalid user or authentication token");

	try {
		const response = await fetch(`${API_URL}/auth/change-password`, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
				Authorization: `Bearer ${token}`,
			},
			body: JSON.stringify({ oldPassword, newPassword, username }),
		});

		const data = await response.json().catch(() => null);
		return { data, status: response.status };
	} catch (error) {
		console.error("API proxy error: ", error);
		return { error: "Server unavailable", status: 500 };
	}
}

export async function deleteAccount(username: string | null, token: string | null): Promise<ServerResponse<ErrorResponseDTO>> {
	if (!username || !token) throw new Error("Invalid user or authentication token");

	try {
		const response = await fetch(`${API_URL}/auth/delete-account`, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
				Authorization: `Bearer ${token}`,
			},
			body: JSON.stringify({ username }),
		});

		const data = await response.json().catch(() => null);
		return { data, status: response.status };
	} catch (error) {
		console.error("API proxy error: ", error);
		return { error: "Server unavailable", status: 500 };
	}
}
