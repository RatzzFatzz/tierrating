"use server"

import {API_URL} from "@/components/global-config";
import {Tier} from "@/model/types";
import {extractJwtData} from "@/components/auth/jwt-decoder";

export const fetchTiers = async (token: string | null, service: string, type: string, logout: () => void) => {
    if (!token) {
        throw new Error("No authentication token")
    }

    const jwtData = extractJwtData(token);
    if (!jwtData) {
        throw new Error("No authentication token")
    }
    const headers = {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
    }
    return fetch(`${API_URL}/tiers/${jwtData.username}/${service}/${type.toLowerCase()}`, {headers})
        .then(response => {
            if (response.status === 401 || response.status === 403) {
                logout();
                throw new Error("Session expired");
            }

            if (response.status === 404) {
                throw new Error("User not found or user doesn't have requested service connected");
            }

            if (!response.ok) {
                throw new Error(`API error status: ${response.status}`);
            }

            return response.json();
        });
}

export async function updateTiers(token: string | null, service: string, type: string, tiers: Tier[], logout: () => void): Promise<any> {
    if (!token) {
        throw new Error("No authentication token")
    }
    const jwtData = extractJwtData(token);
    if (!jwtData) {
        throw new Error("No authentication token")
    }
    return fetch(`${API_URL}/tiers/${jwtData.username}/${service}/${type}`, {
        method: 'POST',
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(tiers)
    }).then(response => {
        if (response.status === 401 || response.status === 403) {
            logout();
            throw new Error("Session expired");
        }

        if (response.status === 404) {
            throw new Error("User not found or user doesn't have requested service connected");
        }

        if (!response.ok) {
            throw new Error(`API error: ${response.status}`);
        }
        return response;
    });
}