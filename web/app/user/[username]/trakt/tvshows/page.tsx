import {ProtectedRoute} from "@/components/contexts/route-accessibility";
import TierListPage from "@/components/tierlist/tier-list-page";

export default function TraktTvshows() {
    return (
        <ProtectedRoute>
            <TierListPage title={"Trakt TV Shows Tier List"} provider={"trakt-tvshows"}/>
        </ProtectedRoute>
    );
}