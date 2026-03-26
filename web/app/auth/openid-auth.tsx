"use client";

import { ProtectedRoute } from "@/contexts/route-accessibility";
import React, { Suspense, useEffect } from "react";
import { REDIRECT_URL_PLACEHOLDER } from "@/lib/global-config";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
import { LoadingPage } from "@/components/loading-skeletons/loading-page";
import { authorizeOpenId } from "@/components/api/auth-api";

export default function OpenIdAuth({ service, openidUrl, returnToUrl }: { service: string; openidUrl: string; returnToUrl: string }) {
	const searchParams = useSearchParams();
	const router = useRouter();
	const { user, token, logout } = useAuth();


	useEffect(() => {
		if (searchParams.size == 0) {
			const params = new URLSearchParams({
				"openid.ns": "http://specs.openid.net/auth/2.0",
				"openid.mode": "checkid_setup",
				"openid.return_to": returnToUrl.replace(REDIRECT_URL_PLACEHOLDER, window.location.protocol + "//" + window.location.host),
				"openid.identity": "http://specs.openid.net/auth/2.0/identifier_select",
				"openid.claimed_id": "http://specs.openid.net/auth/2.0/identifier_select",
			});

			router.push(`${openidUrl}?${params.toString()}`);
		} else {
			const params: Record<string, string> = {};
			searchParams.forEach((value, name) => {
				console.debug(`params for ${name}: ${value}`);
				params[name] = value;
			});
			authorizeOpenId(service, user, token, params)
				.then((response) => {
					if (response.status == 401 || response.status == 403) logout();
					if (response.error) throw new Error(response.error);
					if (response.data) {
						return Promise.reject(response.data.message);
					}
				})
				.catch((err) => {
					console.error(err);
				})
				.finally(() => {
					router.push(`/user/${user}`);
				});
		}
	}, [user, token, logout, router, searchParams, returnToUrl, openidUrl, service]);

	return (
		<ProtectedRoute>
			<Suspense>
				<LoadingPage />
			</Suspense>
		</ProtectedRoute>
	);
}