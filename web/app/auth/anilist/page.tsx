import React, {Suspense} from "react";
import {ProtectedRoute} from "@/contexts/route-accessibility";
import {Auth} from "@/app/auth/auth";
import {ANILIST_CLIENT_ID} from "@/components/global-config";

export default function AuthAniList() {
    return (
        <ProtectedRoute>
            <Suspense>
                <Auth service={"anilist"}
                      authUrl={`https://anilist.co/api/v2/oauth/authorize?client_id=${ANILIST_CLIENT_ID}&redirect_uri=REDIRECT_URL/auth/anilist&response_type=code`}>
                </Auth>
            </Suspense>
        </ProtectedRoute>
    );
}
