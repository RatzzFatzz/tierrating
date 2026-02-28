import {ProtectedRoute} from "@/components/contexts/route-accessibility";
import React, {Suspense} from "react";
import {Auth} from "@/app/auth/auth";
import {CLIENT_ID_PLACEHOLDER, REDIRECT_URL_PLACEHOLDER} from "@/components/global-config";

export default function AuthTrakt() {
    return (
        <ProtectedRoute>
            <Suspense>
                <Auth service={"trakt"}
                      authUrl={`https://api.trakt.tv/oauth/authorize?response_type=code&client_id=${CLIENT_ID_PLACEHOLDER}&redirect_uri=${REDIRECT_URL_PLACEHOLDER}/auth/trakt`}>
                </Auth>
            </Suspense>
        </ProtectedRoute>
    );
}