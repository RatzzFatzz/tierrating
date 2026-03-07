import { Button } from "@/components/ui/button";
import TierConfigModal from "@/components/tiers/tier-config-modal";
import { Tier } from "@/components/model/types";
import React from "react";
import { updateTiers } from "@/components/api/tier-api";
import Link from "next/link";
import { ButtonGroup } from "@/components/ui/button-group";

function getDecimals(service: string): string {
	if (service === "anilist") return "0.01";
	if (service === "trakt") return "1.00";
	return "1.00";
}

export default function ThirdPartyConnectedButton({
	service,
	type,
	title,
	username,
	token,
	logout,
}: {
	service: string;
	type: string;
	title: string;
	username: string;
	token: string | null;
	logout: () => void;
}) {
	const saveTiers = (type: string, tiers: Tier[]) => {
		updateTiers(token, username, service, type, tiers).then((response) => {
			if (response.status === 401 || response.status === 403) {
				logout();
				throw new Error("Session expired");
			}

			if (response.status === 404) throw new Error("User not found or user doesn't have requested service connected");
			if (response.error) throw new Error(`API error: ${response.status}`);
		});
	};

	return (
		<ButtonGroup className={"w-full flex"}>
			<Button variant="outline" className={"grow"}>
				<Link href={`/user/${username}/${service}/${type}`} className="w-full">
					<div className="text-center">{title}</div>
				</Link>
			</Button>

			<TierConfigModal
				service={service}
				type={type}
				onSave={(tiers: Tier[]) => saveTiers(type, tiers)}
				username={username}
				decimals={getDecimals(service)}
			/>
		</ButtonGroup>
	);
}
