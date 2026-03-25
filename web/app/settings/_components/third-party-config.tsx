"use client";

import { useAuth } from "@/components/contexts/auth-context";
import { useEffect, useState } from "react";

import { removeConnection } from "@/components/api/user-api";
import { LoadingDiv } from "@/components/loading-skeletons/loading-page";
import ThirdPartyLoginButton from "@/app/settings/_components/third-party-login-button";
import ThirdPartyConnection from "@/app/settings/_components/third-party-connection";
import { router } from "next/client";
import { useQueries } from "@/hooks/useQueries";
import { apiClient } from "@/lib/api-client";
import { userService } from "@/lib/services/user-service";
import { THIRD_PARTY_SERVICE_CONFIG } from "@/lib/third-party-services-config";

export default function ThirdPartyConfig() {
	const { user, token, logout } = useAuth();
	const [isRemovingService, setIsRemovingService] = useState(false);
	const { data, errors, isRunning, isError, isSuccess, refetch } = useQueries({
		user: () => userService.get(user!, token!),
		services: () => apiClient.get<string[]>("/config/services", token!),
	});

	useEffect(() => {
		if (
			errors?.user?.status === 401 ||
			errors?.user?.status === 403 ||
			errors?.services?.status === 401 ||
			errors?.services?.status === 403
		) {
			logout();
		}
	}, [errors, logout]);

	const removeService = (service: string) => {
		setIsRemovingService(true);
		removeConnection(service, user, token)
			.then((response) => {
				if (response.status === 401 || response.status === 403) {
					logout();
					throw new Error("Session expired or unauthorized");
				}
				if (response.status != 200) throw new Error(response.data ? response.data.message : `API error: ${response.status}`);
				console.debug(`${service} connection removed`);
				router.reload();
			})
			.catch((error) => {
				console.error(error.message);
			})
			.finally(() => {
				setIsRemovingService(false);
			});
	};

	if (isRunning) return <LoadingDiv className={"min-h-30"} />;

	if (!isSuccess) return null;
	const configuredServices = data.services!;
	const userResponse = data.user!;

	return (
		<div className={"w-full grid gap-4"}>
			<div className="grid columns-1 gap-2">
				{Object.entries(THIRD_PARTY_SERVICE_CONFIG).map(([key, config]) =>
					configuredServices.includes(key) &&
					!userResponse.connectedServices.includes(key) ? (
						<ThirdPartyLoginButton
							key={config.service.id}
							title={config.connectTitle}
							path={config.authPath}
							color={config.connectColor}
							service={config.service.id}
						/>
					) : null
				)}
			</div>
			<div className="grid columns-1 gap-2">
				{Object.entries(THIRD_PARTY_SERVICE_CONFIG).map(([key, config]) =>
					userResponse.connectedServices.includes(key) ? (
						<ThirdPartyConnection
							key={config.service.id}
							service={{ id: config.service.id, title: config.service.name }}
							types={config.types.map((t) => ({ id: t.id, title: t.name }))}
							removeConnection={removeService}
							isRemovingService={isRemovingService}
							username={user!}
							token={token}
							logout={logout}
						/>
					) : null
				)}
			</div>
		</div>
	);
}
