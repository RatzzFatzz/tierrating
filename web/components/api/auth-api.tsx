"use server";

import { API_URL } from "@/lib/global-config";
import { ServerResponse, ThirdPartyAuthResponse, ThirdPartyInfoResponse } from "@/components/model/response-types";

export async function authorizeOAuth(
	service: string,
	username: string | null,
	token: string | null,
	code: string | null
): Promise<ServerResponse<ThirdPartyAuthResponse>> {
	if (!username && !token && !code) {
		throw new Error("Invalid username, token or code");
	}

	try {
		const response = await fetch(`${API_URL}/auth/oauth/${service}/${username}`, {
			method: "POST",
			headers: {
				Authorization: "Bearer " + token,
				"Content-Type": "application/json",
			},
			body: JSON.stringify({ code }),
		});

		const data = await response.json().catch(() => null);
		return { data, status: response.status };
	} catch (error) {
		console.error("API proxy error: ", error);
		return { error: "Server unavailable", status: 500 };
	}
}

export async function authorizeOpenId(
	service: string,
	username: string | null,
	token: string | null,
	params: Record<string, string>
): Promise<ServerResponse<ThirdPartyAuthResponse>> {
	if (!username && !token && !params) {
		throw new Error("Invalid username, token or code");
	}

	try {
		const response = await fetch(`${API_URL}/auth/openid/${service}/${username}`, {
			method: "POST",
			headers: {
				Authorization: "Bearer " + token,
				"Content-Type": "application/json",
			},
			body: JSON.stringify({ params: params}),
		});

		const data = await response.json().catch(() => null);
		return { data, status: response.status };
	} catch (error) {
		console.error("API proxy error: ", error);
		return { error: "Server unavailable", status: 500 };
	}
}

export async function fetchThirdPartyInfo(service: string, token: string | null): Promise<ServerResponse<ThirdPartyInfoResponse>> {
	if (!token) {
		throw new Error("Invalid token");
	}

	try {
		const response = await fetch(`${API_URL}/info/${service}`, {
			method: "GET",
			headers: {
				Authorization: "Bearer " + token,
				"Content-Type": "application/json",
			},
		});

		const data = await response.json().catch(() => null);
		return { data, status: response.status };
	} catch (error) {
		console.error("API proxy error: ", error);
		return { error: "Server unavailable", status: 500 };
	}
}
