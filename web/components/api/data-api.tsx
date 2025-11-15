"use server"

import {API_URL} from "@/components/global-config";
import {ServerResponse, UpdateScoreResponse} from "@/components/model/response-types";
import {TierlistEntry} from "@/components/model/types";

export const fetchData = async (token: string | null, username: string, service: string, type: string): Promise<ServerResponse<TierlistEntry[]>> => {
    if (!token) throw new Error("No authentication token");

    try {
        const response = await fetch(`${API_URL}/data/${username}/${service}/${type}`, {
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

export async function updateData(id: string, score: number, service: string, type: string, token: string | null, username: string): Promise<ServerResponse<UpdateScoreResponse>> {
    if (!token) throw new Error("No authentication token");

    try {
        const response = await fetch(`${API_URL}/data/update`, {
            method: 'POST',
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                id: id,
                score: score,
                username: username,
                service: service,
                type: type,
            })
        });

        const data = await response.json().catch(() => null)
        return {data, status: response.status};
    } catch (error) {
        console.error('API proxy error: ', error);
        return {error: 'Server unavailable', status: 500}
    }
}