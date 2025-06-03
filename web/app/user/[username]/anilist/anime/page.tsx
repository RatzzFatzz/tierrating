import TierListPage from "@/components/tier-list-page";
import {fetchData} from "@/components/anilist-functions";

export default async function AniListAnime({params}) {
    const ratingItems = await fetchData((await params).username, "anime");
    return <TierListPage itemList={ratingItems} title={"AniList Anime Tier List"} />;
}
