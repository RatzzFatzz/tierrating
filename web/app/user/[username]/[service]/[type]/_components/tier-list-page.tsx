"use client";

import TierList from "@/app/user/[username]/[service]/[type]/_components/tier-list";
import TmdbDisclaimer from "@/components/disclaimer/tmdb-disclaimer";
import { useState } from "react";
import { Toggle } from "@/components/ui/toggle";
import { ArrowLeftFromLine, ArrowRightFromLine } from "lucide-react";
import { cn } from "@/lib/utils";
import { ButtonGroup } from "@/components/ui/button-group";
import { useAuth } from "@/components/contexts/auth-context";
import { useTiers } from "@/lib/services/tiers-service";
import { useScoreMutation, useTierlistEntries } from "@/lib/services/data-service";
import { LoadingPage } from "@/components/loading-skeletons/loading-page";
import { getDefaultTiers } from "@/lib/default-tiers";
import { ServerResponse } from "@/types/api-response";
import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";

export default function TierListPage({ title, username, service, type }: { title: string, username: string; service: string; type: string }) {
	const { user } = useAuth();
	const [isFullWidth, setIsFullWidth] = useState<boolean>(false);
	const [isPullRunning, setIsPullRunning] = useState<boolean>(false);

	const modificationEnabled: boolean = user == username;


	const pullUpdate = () => {
		setIsPullRunning(true);
		// dataService
		// 	.pullUpdate(username, service, type, token!)
		// 	.then((response) => {
		// 		if (!response.ok) throw new Error(response.error);
		// 		entriesMutate();
		// 	})
		// 	.catch((error) => {
		// 		toast.error(error.message);
		// 	})
		// 	.finally(() => {
		// 		setIsPullRunning(false);
		// 	});
	}

	// if (isError) {
	// 	if (errors.entries?.status === 404) notFound();
	// 	if (errors.entries?.status === 401 || errors.tiers?.status === 401) return <div>private profile</div>;
	// 	if (errors.entries?.status === 500) return <div>{errors.entries?.error}</div>;
	// 	if (errors.tiers?.status === 500) return <div>{errors.tiers?.error}</div>;
	// }

	return (
		<div className={cn("max-w-full px-4")}>
			<div className={"grid grid-cols-2 w-[1514px] m-auto"}>
				<h1 className="text-3xl font-bold mb-6">{title}</h1>
				<div className={"w-full flex justify-end items-end pb-2"}>
					<ButtonGroup>
						<ButtonGroup>
							{modificationEnabled && (
								<Button variant="outline" disabled={isPullRunning} onClick={pullUpdate}>
									{isPullRunning ? "Pulling" : "Pull"}
									{isPullRunning && <Spinner data-icon="inline-start" />}
								</Button>
							)}
						</ButtonGroup>
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
			<div className={cn("m-auto transition-all duration-400 ease-in-out", isFullWidth ? "w-full" : "w-[1514px] ")}>
				<TierList
					username={username}
					service={service}
					type={type}
					modificationEnabled={modificationEnabled && !isPullRunning}
					fullWidth={isFullWidth}
				/>
			</div>
			{service.startsWith("trakt") && <TmdbDisclaimer />}
		</div>
	);
}
