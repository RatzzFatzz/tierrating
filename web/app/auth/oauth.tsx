"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "@/components/contexts/auth-context";
import React, { useEffect, useRef } from "react";
import { authorizeOAuth, info } from "@/components/api/auth-api";
import LoadingPage from "@/components/loading-skeletons/loading-page";
import { CLIENT_ID_PLACEHOLDER, REDIRECT_URL_PLACEHOLDER } from "@/components/global-config";

export function Oauth({ service, authUrl }: { service: string; authUrl: string }) {
	const searchParams = useSearchParams();
	const router = useRouter();
	const host = window.location.protocol + "//" + window.location.host;
	const authUrlRef = useRef(authUrl);
	const { user, token, logout } = useAuth();

	useEffect(() => {
		if (searchParams.has("code")) {
			const code = searchParams.get("code");
			authorizeOAuth(service, user, token, code)
				.then((response) => {
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
		} else {
			info(service, token)
				.then((response) => {
					if (response.status == 401 || response.status == 403) logout();
					if (response.error) throw new Error(response.error);
					if (!response.data) throw new Error("Faulty response");
					authUrlRef.current = authUrlRef.current
						.replace(REDIRECT_URL_PLACEHOLDER, host)
						.replace(CLIENT_ID_PLACEHOLDER, response.data.clientId);
					router.push(authUrlRef.current);
				})
				.catch((err) => {
					console.error(err);
					router.push(`/user/${user}`);
				});
		}
	}, [user, token, logout, router, searchParams, service, host]);

	return <LoadingPage />;
}
