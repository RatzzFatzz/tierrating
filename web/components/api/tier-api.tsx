import {API_URL} from "@/components/global-config";

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

        return await response.json();
    } catch (error) {
        console.error("API request failed: ", error)
        throw error
    }
}