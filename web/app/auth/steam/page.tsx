import { ProtectedRoute } from "@/contexts/route-accessibility";
import React, { Suspense } from "react";
import { REDIRECT_URL_PLACEHOLDER } from "@/lib/config/global-config";
import OpenIdAuth from "@/app/auth/openid-auth";

export default function AuthSteam() {
	return (
		<ProtectedRoute>
			<Suspense>
				<OpenIdAuth
					service={"steam"}
					openidUrl={"https://steamcommunity.com/openid/login"}
					returnToUrl={`${REDIRECT_URL_PLACEHOLDER}/auth/steam`}
				></OpenIdAuth>
			</Suspense>
		</ProtectedRoute>
	);
}
