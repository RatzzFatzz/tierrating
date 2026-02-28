"use server"

import {ServerResponse} from "@/components/model/response-types";
import {API_URL} from "@/components/global-config";

export const fetchConfiguredServices = async (token: string | null): Promise<ServerResponse<string[]>> => {
    if (!token) throw new Error("No authentication token");

    try {
        const response = await fetch(`${API_URL}/config/services`, {
            method: 'GET',
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
            }
        });

        const data = await response.json().catch(() => null)
        return {data, status: response.status};
    } catch (error) {
        console.error('API proxy error: ', error);
        return {error: 'Server unavailable', status: 500}
    }
}