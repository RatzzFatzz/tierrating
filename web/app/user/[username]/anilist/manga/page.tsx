import {ProtectedRoute} from "@/components/contexts/route-accessibility";
import TierListPage from "@/components/tierlist/tier-list-page";

export default function AniListManga() {
    return (
        <ProtectedRoute>
            <TierListPage title={"AniList Manga Tier List"} provider={"anilist-manga"}/>
        </ProtectedRoute>
    );
}