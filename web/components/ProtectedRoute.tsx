"use client"

import {useRouter} from "next/navigation"
import {useEffect} from "react"
import {useAuth} from "@/contexts/AuthContext";
import LoadingPage from "@/components/loading-page";

export default function ProtectedRoute({children}: { children: React.ReactNode }) {
    const {isAuthenticated, isLoading, isExpired} = useAuth()
    const router = useRouter()

    useEffect(() => {
        console.debug(`ProtectedRoute - isAuthenticated: ${isAuthenticated}; isLoading ${isLoading}; isExpired: ${isExpired}`);
        if (!isLoading && (!isAuthenticated || isExpired)) {
            router.push("/login")
        }
    }, [isAuthenticated, isLoading, isExpired, router])

    if (isLoading) {
        return (
            <LoadingPage />
        )
    }

    if (!isAuthenticated) {
        return null
    }

    return <>{children}</>
}