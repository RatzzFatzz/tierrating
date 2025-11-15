import {ProtectedRoute} from "@/components/contexts/route-accessibility";
import TierListPage from "@/components/tierlist/tier-list-page";

export default function AniListAnime() {
    return (
        <ProtectedRoute>
            <TierListPage title={"AniList Anime Tier List"} provider={"anilist-anime"}/>
        </ProtectedRoute>
    );
}
