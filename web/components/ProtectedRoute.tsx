"use client"

import {useRouter} from "next/navigation"
import {useEffect} from "react"
import {useAuth} from "@/contexts/AuthContext";

export default function ProtectedRoute({children}: { children: React.ReactNode }) {
    const {isAuthenticated, isLoading} = useAuth()
    const router = useRouter()

    useEffect(() => {
        console.debug(`ProtectedRoute - isAuthenticated: ${isAuthenticated}; isLoading ${isLoading}`)
        if (!isLoading && !isAuthenticated) {
            router.push("/login")
        }
    }, [isAuthenticated, isLoading, router])

    if (isLoading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="animate-pulse text-muted-foreground">Loading...</div>
            </div>
        )
    }

    if (!isAuthenticated) {
        return null
    }

    return <>{children}</>
}