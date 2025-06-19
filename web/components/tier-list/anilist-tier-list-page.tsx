import React, {useEffect, useState} from "react";
import type {RatingItem} from "@/model/types";
import {useAuth} from "@/contexts/AuthContext";
import TierListPage from "@/components/tier-list-page";
import {fetchWithApi} from "@/components/api/anilist-api";

export default function AniListTierListPage({username, type, title}: {username: string, type: string, title: string}) {
    const [ratingItems, setRatingItems] = useState<RatingItem[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { token, logout } = useAuth();

    useEffect(() => {
        const fetchRatingItems = async () => {
            if (!token || !username) return;

            try {
                setIsLoading(true);
                const items = await fetchWithApi(token, username, type, logout);
                setRatingItems(items);
                setError(null);
            } catch (err) {
                console.error("Failed to fetch rating items:", err);
                setError("Failed to load data. Please try again later.");
            } finally {
                setIsLoading(false);
            }
        };

        fetchRatingItems();
    }, [token, username, type, logout]);


    return (
        <>
            {isLoading ? (
                <div className="flex items-center justify-center min-h-screen">
                    <div className="animate-pulse text-muted-foreground">Loading...</div>
                </div>
            ) : error ? (
                <div className="flex justify-center items-center min-h-screen">
                    <div className="text-destructive">{error}</div>
                </div>
            ) : (
                <TierListPage itemList={ratingItems} title={title} />
            )}
        </>
    );
}