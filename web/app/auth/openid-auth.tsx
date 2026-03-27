"use client";

import React, { useEffect } from "react";
import { REDIRECT_URL_PLACEHOLDER } from "@/lib/config/global-config";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
import { LoadingPage } from "@/components/loading-skeletons/loading-page";
import { useOpenId } from "@/lib/services/auth-service";
import { toast } from "sonner";

export default function OpenIdAuth({ service, openidUrl, returnToUrl }: { service: string; openidUrl: string; returnToUrl: string }) {
	const { user, token, logout } = useAuth();
	const searchParams = useSearchParams();

	const { trigger: authorizeOpenId } = useOpenId(user!, service, token!);

	const router = useRouter();

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
			authorizeOpenId({ params })
				.catch((err) => {
					toast.error("Error occurred. Please try again later.");
				})
				.finally(() => {
					router.push(`/settings`);
				});
		}
	}, [user, token, logout, router, searchParams, returnToUrl, openidUrl, authorizeOpenId]);

	return <LoadingPage />;
}