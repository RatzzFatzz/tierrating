"use client";

import { useAuth } from "@/contexts/auth-context";
import { useState } from "react";
import { LoadingDiv } from "@/components/loading-skeletons/loading-page";
import ThirdPartyLoginButton from "@/app/settings/_components/third-party-login-button";
import ThirdPartyConnection from "@/app/settings/_components/third-party-connection";
import { useRemoveThirdPartyService, useUser } from "@/lib/services/user-service";
import { THIRD_PARTY_SERVICE_CONFIG } from "@/lib/config/third-party-services-config";
import { toast } from "sonner";
import { useThirdPartyServices } from "@/lib/services/third-party-info-service";

export default function ThirdPartyConfig() {
	const { user, token, logout } = useAuth();
	const [isRemovingService, setIsRemovingService] = useState(false);
	const { data: userData, error: userError, isValidating: isValidatingUser, mutate: reloadUser } = useUser(user!, token!);
	const {
		data: services,
		error: servicesError,
		isValidating: isValidatingService,
	} = useThirdPartyServices(token!);
	const { trigger: removeConnection, error: removalError, isMutating } = useRemoveThirdPartyService(user!, token!);
	const isValidating: boolean = (isValidatingUser || isValidatingService) && (!userData || !services);

	const removeService = (service: string) => {
		removeConnection({ service })
			.then(() => {
				reloadUser();
			})
			.catch((error) => {
				toast.error(`Error occurred deleting ${service} connection. Please try again later.`);
			});
	};

	if (isValidating) return <LoadingDiv className={"min-h-30"} />;

	if (userError || servicesError) return <div>error occurred</div>;

	return (
		<div className={"w-full grid gap-4"}>
			<div className="grid columns-1 gap-2">
				{Object.entries(THIRD_PARTY_SERVICE_CONFIG).map(([key, service]) =>
					services!.includes(key) && !userData!.connectedServices.includes(key) ? (
						<ThirdPartyLoginButton
							key={service.id}
							title={`Connect ${service.name}`}
							path={`/auth/${service.id}`}
							service={service.id}
						/>
					) : null
				)}
			</div>
			<div className="grid columns-1 gap-2">
				{Object.entries(THIRD_PARTY_SERVICE_CONFIG).map(([key, service]) =>
					userData!.connectedServices.includes(key) ? (
						<ThirdPartyConnection
							key={service.id}
							service={{ id: service.id, title: service.name }}
							types={service.types.map((type) => ({ id: type.id, title: type.name }))}
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
