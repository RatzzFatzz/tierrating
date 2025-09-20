import type {TierlistEntry} from "@/model/types";
import {API_URL} from "@/components/global-config";

export const fetchData = async (token: string | null, username: string | null, service: string, type: string, logout: () => void): Promise<TierlistEntry[]> => {
    if (!token) {
        logout()
        throw new Error("No authentication token")
    }
    const headers = {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json",
    }
    try {
        const response = await fetch(`${API_URL}/data/${username}/${service}/${type}`, {headers});

        if (response.status === 401 || response.status === 403) {
            logout();
            throw new Error("Session expired");
        }

        if (!response.ok) {
            throw new Error(`API error: ${response.status}`);
        }

        const text = await response.text();
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
    } catch (error) {
        console.error("API request failed: ", error)
        throw error
    }
}