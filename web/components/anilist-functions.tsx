import type {RatingItem} from "@/model/types";

export async function fetchData(username: string, type: string): Promise<RatingItem[]> {
    try {
        const response = await fetch(`http://localhost:8080/anilist/${username}/${type}`);
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