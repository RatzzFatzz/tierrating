import {ProtectedRoute} from "@/contexts/route-accessibility";
import React, {Suspense} from "react";
import {Auth} from "@/app/auth/auth";

export default function AuthTrakt() {
    return (
        <ProtectedRoute>
            <Suspense>
                <Auth service={"trakt"}
                      authUrl={`https://api.trakt.tv/oauth/authorize?response_type=code&client_id=271129879907506896d543ce9469f48f89cc0f839faf3e11c29e7ced0608bf4e&redirect_uri=REDIRECT_URL/auth/trakt`}>
                </Auth>
            </Suspense>
        </ProtectedRoute>
    );
}