import { ServerResponse } from "@/types/api-response";
import { API_URL } from "@/components/global-config";

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

async function request<T>(method: HttpMethod, endpoint: string, token: string | null, body?: unknown): Promise<ServerResponse<T>> {
	if (!token) {
		return { ok: false, status: 401, error: "No authentication token" };
	}

	try {
		const response = await fetch(`${API_URL}${endpoint}`, {
			method,
			headers: {
				"Content-Type": "application/json",
				Authorization: `Bearer ${token}`,
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
	get: <T>(token: string | null, endpoint: string) => request<T>("GET", endpoint, token),

	post: <T>(token: string | null, endpoint: string, body?: unknown) => request<T>("POST", endpoint, token, body),

	put: <T>(token: string | null, endpoint: string, body?: unknown) => request<T>("PUT", endpoint, token, body),

	patch: <T>(token: string | null, endpoint: string, body?: unknown) => request<T>("PATCH", endpoint, token, body),

	delete: <T>(token: string | null, endpoint: string) => request<T>("DELETE", endpoint, token),
};
