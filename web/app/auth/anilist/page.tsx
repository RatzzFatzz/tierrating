"use client"

import {useRouter, useSearchParams} from "next/navigation";
import {useAuth} from "@/contexts/AuthContext";
import ProtectedRoute from "@/components/ProtectedRoute";
import {useEffect} from "react";
import {API_URL} from "@/components/global-config";

export default function AuthAniList() {
    const searchParams = useSearchParams()
    const router = useRouter()
    const {user, token, isLoading, isAuthenticated} = useAuth()

    useEffect(() => {
        if (!isLoading && isAuthenticated) {
            console.log('AuthAnilist - code execution pending')
            if (searchParams.has("code")) {
                const code = searchParams.get("code")
                console.log(code)
                fetch(`${API_URL}/anilist/auth/${user}`, {
                    method: 'POST',
                    headers: {
                        "Authorization": "Bearer " + token,
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({code})
                }).then(res => res.json())
                .then(data => {
                    if (data.message) {
                        return Promise.reject(data.message)
                    }
                }).catch(err => {
                    console.log(err);
                }).finally(() => router.push(`/user/${user}`))
            } else {
                router.push("https://anilist.co/api/v2/oauth/authorize?client_id=27404&redirect_uri=http://localhost:3001/auth/anilist&response_type=code")
            }
        }
    }, [user, token, isAuthenticated, isLoading, router, searchParams])

    return (
        <ProtectedRoute>
            <div className="flex justify-center items-center min-h-screen">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-primary"></div>
            </div>
        </ProtectedRoute>
    );
}
