import type { RatingItem } from "@/model/types"
import {transformScoreToTier} from "@/components/score-to-tier-converter";
import TierListPage from "@/components/tier-list-page";


interface AniListItem {
    score: number;
    media: {
        id: number
        title: {
            romaji: string;
            english: string;
        };
        coverImage: {
            large: string;
            extraLarge: string;
        };
    };
}

function convertAniListToRatingItem(src: AniListItem): RatingItem {
    return {
        id: String(src.media.id),
        score: src.score,
        tier: transformScoreToTier(src.score),
        title: src.media.title.english || src.media.title.romaji ,
        cover: src.media.coverImage.large || src.media.coverImage.extraLarge
    } as RatingItem;
}

async function fetchData(type: string): Promise<RatingItem[]> {
    try {
        const response = await fetch("http://localhost:8080/data/anilist/ratzzfatzz/" + type);
        const data = await response.json();
        return await data.map((item: AniListItem)=> convertAniListToRatingItem(item));
    } catch (error) {
        console.error("Error fetching data:", error);
        return [];
    }
}

export default async function AniListAnime() {
    const ratingItems = await fetchData("anime");
    return <TierListPage itemList={ratingItems} title={"AniList Anime Tier List"} />;
}
