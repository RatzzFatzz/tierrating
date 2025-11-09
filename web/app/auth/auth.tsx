"use client"

import {useRouter, useSearchParams} from "next/navigation";
import {useAuth} from "@/contexts/auth-context";
import React, {useEffect} from "react";
import authorize from "@/components/api/auth-api";
import LoadingPage from "@/components/loading-page";

export function Auth({service, authUrl}: {service: string, authUrl: string}) {
    const searchParams = useSearchParams()
    const router = useRouter()
    const host = window.location.protocol + "//" + window.location.host;
    const {user, token} = useAuth()

    authUrl = authUrl.replace("REDIRECT_URL", host);
    console.info(host)
    console.info(authUrl)

    useEffect(() => {
        if (searchParams.has("code")) {
            const code = searchParams.get("code")
            authorize(service, user, token, code)
                .then(response => {
                    if (response.error) throw new Error(response.error);
                    if (!response.data) throw new Error("Faulty response")
                    if (response.data.message) {
                        return Promise.reject(response.data.message)
                    }
                })
                .catch(err => {
                    console.error(err);
                })
                .finally(() => {
                    router.push(`/user/${user}`)
                });
        } else {
            router.push(authUrl)
        }
    }, [user, token, router, searchParams, service, authUrl])

    return <LoadingPage/>
}