"use client"

import {useAuth} from "@/components/contexts/auth-context";
import {useRouter} from "next/navigation";
import {ProtectedRoute} from "@/components/contexts/route-accessibility";
import LoadingPage from "@/components/loading-skeletons/loading-page";
import {useEffect} from "react";

export default function RedirectToUser() {
    const {user, isLoading, isAuthenticated} = useAuth();
    const router = useRouter();

    useEffect(() => {
        if (!isLoading) {
            if (isAuthenticated) router.push(`/user/${user}`);
            else router.push("/login");
        }

    }, [isLoading, isAuthenticated, router, user])

    return (
        <ProtectedRoute>
            <LoadingPage/>
        </ProtectedRoute>
    )
}