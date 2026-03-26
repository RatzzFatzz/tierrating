import { API_URL } from "@/lib/global-config";
import { ErrorResponseDTO } from "@/types/response-types";
import { ApiRequestError } from "@/types/api-request-error";

type HttpMethod = "GET" | "POST" | "PUT" | "DELETE" | "PATCH";

export interface ApiRequestOptions {
	method?: HttpMethod;
	body?: unknown;
	token?: string;
}

export async function apiClient<T>(endpoint: string, options: ApiRequestOptions = {}): Promise<T> {
	const { method = "GET", body, token } = options;

	const headers: HeadersInit = {
		"Content-Type": "application/json",
	};

	if (token) {
		headers["Authorization"] = `Bearer ${token}`;
	}

	try {
		const response = await fetch(`${API_URL}/api${endpoint}`, {
			method,
			headers,
			body: body ? JSON.stringify(body) : undefined,
		});

		// Handle non-OK responses
		if (!response.ok) {
			let backendErrorMessage: string | undefined;

			try {
				const errorResponse: ErrorResponseDTO = await response.json();
				backendErrorMessage = errorResponse.error;
			} catch {
				backendErrorMessage = response.statusText || "Unknown error";
			}

			throw new ApiRequestError(response.status, backendErrorMessage || response.statusText, backendErrorMessage);
		}

		if (response.status === 204) {
			return undefined as T;
		}

		const text = await response.text();
		return text ? JSON.parse(text) : (undefined as T);
	} catch (error) {
		if (error instanceof ApiRequestError) {
			throw error;
		}
		throw new ApiRequestError(0, error instanceof Error ? error.message : "Network error");
	}
}
