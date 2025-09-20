"use client"

import ProtectedRoute from "@/components/ProtectedRoute";
import TierListPage from "@/components/tier-list-page";

export default function AniListManga() {
    return (
        <ProtectedRoute>
            <TierListPage title={"AniList Manga Tier List"} provider={"anilist-manga"}/>
        </ProtectedRoute>
    );
}