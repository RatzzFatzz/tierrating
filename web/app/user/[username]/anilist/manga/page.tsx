import TierListPage from "@/components/tier-list-page";
import {fetchData} from "@/components/anilist-functions";

export default async function AniListManga({params}) {
    const ratingItems = await fetchData( (await params).username, "manga");
    return <TierListPage itemList={ratingItems} title={"AniList Manga Tier List"} />;
}