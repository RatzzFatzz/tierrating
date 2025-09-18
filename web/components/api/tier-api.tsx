import {API_URL} from "@/components/global-config";
import {Tier} from "@/model/types";
import {extractJwtData} from "@/components/jwt-decoder";

export const fetchTiers = async (token: string | null, username: string, service: string, type: string, logout: () => void) => {
    if (!token) {
        throw new Error("No authentication token")
    }
    const headers = {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
    }
    try {
        const response = await fetch(`${API_URL}/tiers/${username}/${service}/${type}`, {headers});

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

        console.info(response)
        return await response.json();
    } catch (error) {
        console.error("API request failed: ", error)
        throw error
    }
}

export const updateTiers =  async (token: string | null, service: string, type: string, tiers: Tier[], logout: () => void) => {
    if (!token) {
        throw new Error("No authentication token")
    }
    const jwtData = extractJwtData(token);
    if (!jwtData) {
        throw new Error("No authentication token")
    }
    try {
        console.info("saving")
        console.info(tiers)
        const response = await fetch(`${API_URL}/tiers/${jwtData.username}/${service}/${type}`, {
            method: 'POST',
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(tiers)
        })

        if (response.status === 401 || response.status === 403) {
            // logout();
            throw new Error("Session expired");
        }

        if (response.status === 404) {
            throw new Error("User not found or user doesn't have requested service connected");
        }

        if (!response.ok) {
            throw new Error(`API error: ${response.status}`);
        }
        await response;
    } catch (error) {
        console.error("API request failed: ", error)
        throw error
    }
}