"use client";

import TierList from "@/components/tierlist/tier-list";
import TmdbDisclaimer from "@/components/disclaimer/tmdb-disclaimer";
import { useCallback, useMemo, useState } from "react";
import { Toggle } from "@/components/ui/toggle";
import { ArrowLeftFromLine, ArrowRightFromLine } from "lucide-react";
import { cn } from "@/lib/utils";
import { ButtonGroup } from "@/components/ui/button-group";
import { useAuth } from "@/components/contexts/auth-context";
import { useQueries } from "@/hooks/useQueries";
import { tiersService } from "@/lib/services/tiers-service";
import { dataService } from "@/lib/services/data-service";
import { LoadingPage } from "@/components/loading-skeletons/loading-page";
import { getDefaultTiers } from "@/lib/default-tiers";
import { ServerResponse } from "@/types/api-response";
import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";
import { toast } from "sonner";

export default function TierListPage({ title, username, service, type }: { title: string, username: string; service: string; type: string }) {
	const { token, user, logout } = useAuth();
	const { data, errors, isRunning, isSuccess, isError, refetch } = useQueries(
		{
			tiers: () => tiersService.get(username, service, type, token!),
			entries: () => dataService.fetchEntries(username, service, type, token!),
		},
		[username, service, type, token]
	);
	const tiers = useMemo(() => (data.tiers?.length ? data.tiers : getDefaultTiers()), [data.tiers]);
	const entries = useMemo(() => data.entries ?? [], [data.entries]);

	const pushEntryUpdate = useCallback((id: string, score: number): Promise<ServerResponse<unknown>> => {
		return dataService.updateEntry(token!, {id, score, username, service, type});
	}, [token, username, service, type]);

	const [isFullWidth, setIsFullWidth] = useState<boolean>(false);
	const [isPullRunning, setIsPullRunning] = useState<boolean>(false);
	const modificationEnabled: boolean = user == username;

	const pullUpdate = () => {
		setIsPullRunning(true);
		dataService
			.pullUpdate(username, service, type, token!)
			.then((response) => {
				if (!response.ok) throw new Error(response.error);
				refetch();
			})
			.catch((error) => {
				toast.error(error.message);
			})
			.finally(() => {
				setIsPullRunning(false);
			});
	}

	if (isRunning && !data.tiers && !data.entries) return <LoadingPage />;

	return (
		<div
			className={cn(
				"max-w-full mx-auto content-center px-4",
				"transition-all duration-400 ease-in-out",
				isFullWidth ? "w-full" : "w-[1514px] "
			)}
		>
			<div className={"grid grid-cols-2"}>
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
			<TierList
				tiers={tiers}
				entries={entries}
				modificationEnabled={modificationEnabled && !isPullRunning}
				pushEntryUpdateAction={pushEntryUpdate}
			/>
			{service.startsWith("trakt") && <TmdbDisclaimer />}
		</div>
	);
}
