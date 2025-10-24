"use server"

import {API_URL} from "@/components/global-config";
import {Tier} from "@/model/types";
import {ServerResponse} from "@/model/response-types";

export const fetchTiers = async (token: string | null, username: string, service: string, type: string): Promise<ServerResponse<Tier[]>> => {
    if (!token) throw new Error("No authentication token")

    try {
        const response = await fetch(`${API_URL}/tiers/${username}/${service}/${type.toLowerCase()}`, {
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`,
            }
        });

        const data = await response.json().catch(() => null)
        return {data, status: response.status};
    } catch (error) {
        console.error('API proxy error: ', error);
        return {error: 'Server unavailable', status: 500}
    }
}

export async function updateTiers(token: string | null, username: string, service: string, type: string, tiers: Tier[]): Promise<ServerResponse<any>> {
    if (!token)  throw new Error("No authentication token")

    try {
        const response = await fetch(`${API_URL}/tiers/${username}/${service}/${type}`, {
            method: 'POST',
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(tiers)
        });

        const data = await response.json().catch(() => null)
        return {data, status: response.status};
    } catch (error) {
        console.error('API proxy error: ', error);
        return {error: 'Server unavailable', status: 500}
    }

}