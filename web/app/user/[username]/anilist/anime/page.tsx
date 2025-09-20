"use client"

import ProtectedRoute from "@/components/ProtectedRoute";
import TierListPage from "@/components/tier-list-page";

export default function AniListAnime() {
    return (
        <ProtectedRoute>
            <TierListPage title={"AniList Anime Tier List"} provider={"anilist-anime"}/>
        </ProtectedRoute>
    );
}
