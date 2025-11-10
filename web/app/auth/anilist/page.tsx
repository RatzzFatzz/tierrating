import React, {Suspense} from "react";
import {ProtectedRoute} from "@/contexts/route-accessibility";
import {Auth} from "@/app/auth/auth";
import {CLIENT_ID_PLACEHOLDER, REDIRECT_URL_PLACEHOLDER} from "@/components/global-config";

export default function AuthAniList() {
    return (
        <ProtectedRoute>
            <Suspense>
                <Auth service={"anilist"}
                      authUrl={`https://anilist.co/api/v2/oauth/authorize?client_id=${CLIENT_ID_PLACEHOLDER}&redirect_uri=${REDIRECT_URL_PLACEHOLDER}/auth/anilist&response_type=code`}>
                </Auth>
            </Suspense>
        </ProtectedRoute>
    );
}
