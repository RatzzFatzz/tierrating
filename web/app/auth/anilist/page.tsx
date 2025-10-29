"use client"

import React, {Suspense} from "react";
import {ProtectedRoute} from "@/contexts/route-accessibility";
import {Auth} from "@/app/auth/auth";

export default function AuthAniList() {
    return (
        <ProtectedRoute>
            <Suspense>
                <Auth service={"anilist"}
                      authUrl={"https://anilist.co/api/v2/oauth/authorize?client_id=27404&redirect_uri=http://localhost:3000/auth/anilist&response_type=code"}>
                </Auth>
            </Suspense>
        </ProtectedRoute>
    );
}
