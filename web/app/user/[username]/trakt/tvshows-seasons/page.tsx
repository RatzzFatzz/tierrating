import {ProtectedRoute} from "@/contexts/route-accessibility";
import TierListPage from "@/components/tierlist/tier-list-page";

export default function TraktTvShowsSeasons() {
    return (
        <ProtectedRoute>
            <TierListPage title={"Trakt TV Shows Tier List"} provider={"trakt-tvshows-seasons"}/>
        </ProtectedRoute>
    );
}