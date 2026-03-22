import React, { Suspense } from "react";
import { ProtectedRoute } from "@/components/contexts/route-accessibility";
import { Oauth } from "@/app/auth/oauth";
import { CLIENT_ID_PLACEHOLDER, REDIRECT_URL_PLACEHOLDER } from "@/lib/global-config";

export default function AuthAniList() {
	return (
		<ProtectedRoute>
			<Suspense>
				<Oauth
					service={"anilist"}
					authUrl={`https://anilist.co/api/v2/oauth/authorize?client_id=${CLIENT_ID_PLACEHOLDER}&redirect_uri=${REDIRECT_URL_PLACEHOLDER}/auth/anilist&response_type=code`}
				></Oauth>
			</Suspense>
		</ProtectedRoute>
	);
}
