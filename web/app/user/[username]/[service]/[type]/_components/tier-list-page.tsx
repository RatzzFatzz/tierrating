"use client";

import TierList from "@/app/user/[username]/[service]/[type]/_components/tier-list";
import TmdbDisclaimer from "@/components/disclaimer/tmdb-disclaimer";
import { useState } from "react";
import { Toggle } from "@/components/ui/toggle";
import { ArrowLeftFromLine, ArrowRightFromLine } from "lucide-react";
import { cn } from "@/lib/utils";
import { ButtonGroup } from "@/components/ui/button-group";
import { useAuth } from "@/contexts/auth-context";
import PushPullButtonGroup from "@/app/user/[username]/[service]/[type]/_components/PushPullButtonGroup";

export default function TierListPage({
	title,
	username,
	service,
	type,
}: {
	title: string;
	username: string;
	service: string;
	type: string;
}) {
	const { user, token } = useAuth();
	const [isFullWidth, setIsFullWidth] = useState<boolean>(false);

	const modificationEnabled: boolean = user == username;

	return (
		<div className={cn("max-w-full px-4")}>
			<div className={"grid grid-cols-2 2xl:w-[1514px] m-auto"}>
				<h1 className="text-3xl font-bold mb-6">{title}</h1>
				<div className={"w-full flex justify-end items-end pb-2"}>
					<ButtonGroup>
						{modificationEnabled && (
							<PushPullButtonGroup service={service} type={type} />
						)}
						<ButtonGroup>
							<Toggle variant="outline" aria-label={"Toggle full width"} onPressedChange={() => setIsFullWidth(!isFullWidth)}>
								<ArrowLeftFromLine />
								Full Width
								<ArrowRightFromLine />
							</Toggle>
						</ButtonGroup>
					</ButtonGroup>
				</div>
			</div>
			<div className={cn("m-auto transition-all duration-400 ease-in-out", isFullWidth ? "w-full" : "2xl:w-[1514px]")}>
				<TierList
					username={username}
					service={service}
					type={type}
					modificationEnabled={modificationEnabled}
				/>
			</div>
			{service.startsWith("trakt") && <TmdbDisclaimer />}
		</div>
	);
}
