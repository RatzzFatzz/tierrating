"use server"

import type {TierlistEntry} from "@/model/types";
import {API_URL} from "@/components/global-config";
import {extractJwtData} from "@/components/auth/jwt-decoder";

export const fetchData = async (token: string | null, username: string | null, service: string, type: string, logout: () => void): Promise<TierlistEntry[]> => {
    if (!token) {
        logout()
        throw new Error("No authentication token")
    }
    const headers = {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
    }
    return fetch(`${API_URL}/data/${username}/${service}/${type}`, {headers})
        .then(response => {
            if (response.status === 401 || response.status === 403) {
                logout();
                throw new Error("Session expired");
            }

            if (!response.ok) {
                throw new Error(`API error: ${response.status}`);
            }
            return response.text();
        }).then(text => {
            if (!text) {
                return [];
            }

            const data = JSON.parse(text);
            const items = Array.isArray(data) ? data : (data.items || []);

            return items.map((item: TierlistEntry) => ({
                id: item.id.toString(),
                score: item.score,
                title: item.title,
                cover: item.cover,
                tier: item.tier,
            }));
        });
}

export async function updateData(id: string, score: number, service: string, token: string | null, username: string | null) {
    if (!token) {
        throw new Error("No authentication token")
    }
    const jwtData = extractJwtData(token);
    if (!jwtData) {
        throw new Error("No authentication token")
    }

    return fetch(`${API_URL}/data/update`, {
        method: 'POST',
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            id: id,
            score: score,
            username: username,
            service: service,
        })
    }).then(response => {
        console.log(response.status)
        if (response.status === 401 || response.status === 403) {
            throw new Error("Session expired or unauthorized");
        }

        if (response.status === 404) {
            throw new Error("User not found or user doesn't have requested service connected");
        }

        if (!response.ok) {
            throw new Error(`API error: ${response.status}`);
        }
        return response.json();
    });
}