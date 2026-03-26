import { ProtectedRoute } from "@/contexts/route-accessibility";
import React, { Suspense } from "react";
import { Oauth } from "@/app/auth/oauth";
import { CLIENT_ID_PLACEHOLDER, REDIRECT_URL_PLACEHOLDER } from "@/lib/global-config";

export default function AuthTrakt() {
	return (
		<ProtectedRoute>
			<Suspense>
				<Oauth
					service={"trakt"}
					authUrl={`https://api.trakt.tv/oauth/authorize?response_type=code&client_id=${CLIENT_ID_PLACEHOLDER}&redirect_uri=${REDIRECT_URL_PLACEHOLDER}/auth/trakt`}
				></Oauth>
			</Suspense>
		</ProtectedRoute>
	);
}
