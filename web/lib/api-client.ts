import { ServerResponse } from "@/types/api-response";
import { API_URL } from "@/lib/global-config";

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

function getAuthHeaders(token?: string): Record<string, string> {
	return token ? { Authorization: `Bearer ${token}` } : {};
}

async function request<T>(method: HttpMethod, endpoint: string, token?: string, body?: unknown): Promise<ServerResponse<T>> {
	try {
		const response = await fetch(`${API_URL}/api${endpoint}`, {
			method,
			headers: {
				"Content-Type": "application/json",
				Accept: "application/json",
				...getAuthHeaders(token),
			},
			body: body ? JSON.stringify(body) : undefined,
		});

		if (response.status === 204) {
			return { ok: true, status: 204, data: undefined as T };
		}

		const json = await response.json().catch(() => null);
		console.log(json);

		if (response.ok) {
			return { ok: true, status: response.status, data: json as T };
		}

		return {
			ok: false,
			status: response.status,
			error: json?.message || json?.error || `Request failed (${response.status})`,
		};
	} catch {
		return { ok: false, status: 0, error: "Server unavailable" };
	}
}

export const apiClient = {
	get: <T>(endpoint: string, token?: string) => request<T>("GET", endpoint, token),

	post: <T>(endpoint: string, token?: string, body?: unknown) => request<T>("POST", endpoint, token, body),

	put: <T>(endpoint: string, token?: string, body?: unknown) => request<T>("PUT", endpoint, token, body),

	patch: <T>(endpoint: string, token?: string, body?: unknown) => request<T>("PATCH", endpoint, token, body),

	delete: <T>(endpoint: string, token?: string) => request<T>("DELETE", endpoint, token),
};
