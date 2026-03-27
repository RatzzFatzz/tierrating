"use client";

import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "@/contexts/auth-context";
import React, { useEffect, useRef } from "react";
import { LoadingPage } from "@/components/loading-skeletons/loading-page";
import { CLIENT_ID_PLACEHOLDER, REDIRECT_URL_PLACEHOLDER } from "@/lib/config/global-config";
import { toast } from "sonner";
import { useOauth } from "@/lib/services/auth-service";
import { useThirdPartyServiceInfo } from "@/lib/services/third-party-info-service";

export function Oauth({ service, authUrl }: { service: string; authUrl: string }) {
	const { user, token } = useAuth();
	const searchParams = useSearchParams();
	const code = searchParams.get("code");

	const { trigger: authorizeOAuth } = useOauth(user!, service, token!)
	const { data: thirdPartyInfo, error, isValidating } = useThirdPartyServiceInfo(!!code, service, token!);

	const router = useRouter();
	const host = window.location.protocol + "//" + window.location.host;

	useEffect(() => {
		if (code) {
			authorizeOAuth({ code })
				.catch((err) => {
					toast.error("Error occurred. Please try again later.");
				})
				.finally(() => {
					router.push(`/settings`);
				});
		} else {
			if (error) {
				toast.error("Error occurred. Please try again later.");
				router.push(`/settings`);
			}
			if (thirdPartyInfo) {
				router.push(authUrl.replace(REDIRECT_URL_PLACEHOLDER, host).replace(CLIENT_ID_PLACEHOLDER, thirdPartyInfo.clientId));
			}
		}
	}, [router, host, code, authUrl, authorizeOAuth, thirdPartyInfo, error, isValidating]);

	return <LoadingPage />;
}
