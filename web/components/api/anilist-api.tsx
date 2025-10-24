"use server"

import {API_URL} from "@/components/global-config";
import {ServerResponse, ThirdPartyAuthResponse} from "@/model/response-types";

export default async function authorize(username: string | null, token: string | null, code: string | null): Promise<ServerResponse<ThirdPartyAuthResponse>> {
    if (!username && !token && !code) {
        throw new Error("Invalid username, token or code")
    }

    try {
        const response = await fetch(`${API_URL}/anilist/auth/${username}`, {
            method: 'POST',
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({code})
        });

        const data = await response.json().catch(() => null)
        return {data, status: response.status};
    } catch (error) {
        console.error('API proxy error: ', error);
        return {error: 'Server unavailable', status: 500}
    }
}

