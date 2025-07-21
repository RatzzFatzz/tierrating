"use client"

import {useRouter, useSearchParams} from "next/navigation";
import {useAuth} from "@/contexts/AuthContext";
import ProtectedRoute from "@/components/ProtectedRoute";
import React, {useEffect} from "react";
import {authorize} from "@/components/api/anilist-api";
import LoadingPage from "@/components/loading-page";

export const dynamic = 'force-dynamic'

export default function AuthAniList() {
    const searchParams = useSearchParams()
    const router = useRouter()
    const {user, token} = useAuth()

    useEffect(() => {
        if (searchParams.has("code")) {
            const code = searchParams.get("code")
            authorize(user, token, code)
            .then(data => {
                if (data.message) {
                    return Promise.reject(data.message)
                }
            }).catch(err => {
                console.error(err);
            }).finally(() => router.push(`/user/${user}`))
        } else {
            router.push("https://anilist.co/api/v2/oauth/authorize?client_id=27404&redirect_uri=http://localhost:3001/auth/anilist&response_type=code")
        }
    }, [user, token, router, searchParams])

    return (
        <ProtectedRoute>
            <LoadingPage />
        </ProtectedRoute>
    );
}
