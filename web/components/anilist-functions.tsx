import type {RatingItem} from "@/model/types";

export async function fetchData(username: string, type: string): Promise<RatingItem[]> {
    try {
        const API_URL = process.env.API_URL || 'http://localhost:8080';
        const response = await fetch(`${API_URL}/anilist/${username}/${type}`);
        const data = await response.json();
        const items = Array.isArray(data) ? data : (data.items || []);

        return items.map((item: any) => ({
            id: item.id.toString(),
            score: item.score,
            title: item.title,
            cover: item.cover,
            tier: item.tier,
        }));
    } catch (error) {
        console.error("Error fetching data:", error);
        return [];
    }
}