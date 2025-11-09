import {ProtectedRoute} from "@/contexts/route-accessibility";
import React, {Suspense} from "react";
import {Auth} from "@/app/auth/auth";
import {TRAKT_CLIENT_ID} from "@/components/global-config";

export default function AuthTrakt() {
    return (
        <ProtectedRoute>
            <Suspense>
                <Auth service={"trakt"}
                      authUrl={`https://api.trakt.tv/oauth/authorize?response_type=code&client_id=${TRAKT_CLIENT_ID}&redirect_uri=REDIRECT_URL/auth/trakt`}>
                </Auth>
            </Suspense>
        </ProtectedRoute>
    );
}