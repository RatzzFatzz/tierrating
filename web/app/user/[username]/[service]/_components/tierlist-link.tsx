import { ButtonGroup } from "@/components/ui/button-group";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import React from "react";
import Image from "next/image";

export function TierlistLink({ service, type, title, username }: { service: string; type: string; title: string; username: string }) {
	return (
		<ButtonGroup className={"w-full flex"}>
			<Button variant="outline" className={"grow"}>
				<Link href={`/user/${username}/${service}/${type}`} className="w-full flex justify-center">
					<div className="relative w-5 h-5 mr-auto">
						<Image src={`/icons/${service}.svg`} alt={`${service} icon`} fill={true} />
					</div>
					<div className="text-center absolute">{title}</div>
				</Link>
			</Button>
		</ButtonGroup>
	);
}
