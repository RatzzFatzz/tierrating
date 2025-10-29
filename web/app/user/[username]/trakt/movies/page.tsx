import {ProtectedRoute} from "@/contexts/route-accessibility";
import TierListPage from "@/components/tierlist/tier-list-page";

export default function TraktMovies() {
    return (
        <ProtectedRoute>
            <TierListPage title={"Trakt Movies Tier List"} provider={"trakt-movies"}/>
        </ProtectedRoute>
    );
}