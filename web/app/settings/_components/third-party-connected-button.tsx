import { Button } from "@/components/ui/button";
import TierConfigModal from "@/app/settings/_components/tier-config-modal";
import React from "react";
import Link from "next/link";
import { ButtonGroup } from "@/components/ui/button-group";
import { getServiceConfig } from "@/lib/config/third-party-services-config";

export default function ThirdPartyConnectedButton({
	service,
	type,
	title,
	username,
}: {
	service: string;
	type: string;
	title: string;
	username: string;
}) {
	return (
		<ButtonGroup className={"w-full flex"}>
			<Button variant="outline" className={"grow"}>
				<Link href={`/user/${username}/${service}/${type}`} className="w-full">
					<div className="text-center">{title}</div>
				</Link>
			</Button>

			<TierConfigModal service={service} type={type} username={username} decimals={getServiceConfig(service)!.scoreDecimal} />
		</ButtonGroup>
	);
}
