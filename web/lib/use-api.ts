import useSWR, { SWRConfiguration } from "swr";
import useSWRMutation from "swr/mutation";
import { apiClient, ApiRequestError, ApiRequestOptions } from "./api-client";

export function useApi<T>(key: string | null, options: ApiRequestOptions = {}, swrConfig: SWRConfiguration = {}) {
	return useSWR<T, ApiRequestError>(
		key,
		async (url: string) => {
			return apiClient<T>(url, options);
		},
		{
			...swrConfig,
			onError: (error) => {
				console.error("API Error:", error.backendError || error.message);
			},
		}
	);
}

export function useApiMutation<TResult = void, TData = unknown>(endpoint: string, options: Omit<ApiRequestOptions, "body"> = {}) {
	return useSWRMutation<TResult, ApiRequestError, string, TData>(endpoint, async (url, { arg }) => {
		return apiClient<TResult>(url, {
			...options,
			method: options.method || "POST",
			body: arg,
		});
	});
}
