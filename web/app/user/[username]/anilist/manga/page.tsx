"use client"

import ProtectedRoute from "@/components/ProtectedRoute";
import {use} from "react";
import AniListTierListPage from "@/components/tier-list/anilist-tier-list-page";

export default function AniListManga({params}) {
    const resolvedParams = use(params);
    const username: string = resolvedParams.username;

    return (
        <ProtectedRoute>
            <AniListTierListPage username={username} type={"manga"} title={"AniList Manga Tier List"}/>
        </ProtectedRoute>
    );
}