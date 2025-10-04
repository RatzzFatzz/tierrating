"use client"

import {usePathname, useRouter} from "next/navigation"
import {useEffect} from "react"
import {useAuth} from "@/contexts/AuthContext";
import LoadingPage from "@/components/loading-page";

export function ProtectedRoute({children}: { children: React.ReactNode }) {
    const {user, isAuthenticated, isLoading, isExpired} = useAuth()
    const router = useRouter()
    const currentPath = usePathname()

    useEffect(() => {
        console.debug(`User ${user} isAuthenticated: ${isAuthenticated}; isLoading ${isLoading}; isExpired: ${isExpired}`);
        if (!isLoading && (!isAuthenticated || isExpired)) {
            router.push("/login");
            console.debug(`Redirected from ${currentPath} to /login`);
        }
    }, [user, isAuthenticated, isLoading, isExpired, router, currentPath])

    if (isLoading || !isAuthenticated
        || (!isLoading && (!isAuthenticated || isExpired))) {
        return <LoadingPage />
    }

    return <>{children}</>
}

export function AnonymousAllowedRoute({children}: {children: React.ReactNode}) {
    const {user, isAuthenticated, isLoading, isExpired} = useAuth()
    const router = useRouter()
    const currentPath = usePathname()

    useEffect(() => {
        console.debug(`User ${user} isAuthenticated: ${isAuthenticated}; isLoading ${isLoading}; isExpired: ${isExpired}`);
        if (!isLoading && isAuthenticated && (currentPath == "/login" || currentPath == "/signup")) {
            router.push(`/user/${user}`)
            console.debug(`Redirected from /login to /user/${user}`)
        }
    }, [user, isAuthenticated, isLoading, isExpired, router, currentPath])

    if (isLoading
        || (!isLoading && isAuthenticated && (currentPath == "/login" || currentPath == "/signup")) ) {
        return <LoadingPage/>
    }

    return <>{children}</>
}