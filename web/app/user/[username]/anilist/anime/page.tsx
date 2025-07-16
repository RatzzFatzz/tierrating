"use client"

import ProtectedRoute from "@/components/ProtectedRoute";
import AniListTierListPage from "@/components/tier-list/anilist-tier-list-page";
import {useParams} from "next/navigation";

export default function AniListAnime() {
    const params = useParams<{username: string}>();
    const username: string = params.username;

    return (
        <ProtectedRoute>
            <AniListTierListPage username={username} type={"anime"} title={"AniList Anime Tier List"}/>
        </ProtectedRoute>
    );
}
