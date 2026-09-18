import { useEnqueueSync, useSyncStatus } from "@/lib/services/sync-service";
import { SyncStatusResponse, SyncType } from "@/types/responses/sync-responses";
import { toast } from "sonner";
import { useAuth } from "@/contexts/auth-context";
import { Spinner } from "@/components/ui/spinner";
import { ArrowDownFromLine, ArrowUpFromLine } from "lucide-react";
import { ButtonGroup } from "@/components/ui/button-group";
import { Tooltip, TooltipContent, TooltipTrigger } from "@/components/ui/tooltip";
import { Button } from "@/components/ui/button";

export default function PushPullButtonGroup({ service, type }: { service: string; type: string }) {
	const { token } = useAuth();

	const { trigger: enqueueSync, error: enqueueError, isMutating: isEnqueuing } = useEnqueueSync(service, type, token!);
	const {
		data: syncStatus,
		error: statusError,
		mutate: updateSyncStatus,
		isValidating: statusIsValidating,
	} = useSyncStatus(service, type, { refreshInterval: (latest: SyncStatusResponse) => (latest?.current ? 1000 : 0) }, token!);

	const isSyncInProgress = isEnqueuing || statusIsValidating || syncStatus?.current != null;
	const isPullRunning = isSyncInProgress && syncStatus?.current?.type == "PULL";
	const isPushRunning = isSyncInProgress && syncStatus?.current?.type == "PUSH";

	const enqueueSyncWithType = async (direction: SyncType) => {
		try {
			await enqueueSync({ type: direction });
		} catch (e) {
			toast.error(e instanceof Error ? e.message : String(e));
		}
		await updateSyncStatus();
	};

	const pullText = isPullRunning ? "Pulling" : "Pull";
	const PullIcon = isPullRunning ? Spinner : ArrowDownFromLine;
	const pushText = isPushRunning ? "Pushing" : "Push";
	const PushIcon = isPushRunning ? Spinner : ArrowUpFromLine;

	return (
		<ButtonGroup>
			<Tooltip>
				<TooltipTrigger asChild>
					<Button variant="outline" disabled={isSyncInProgress} onClick={() => enqueueSyncWithType("PUSH")} aria-label={pushText}>
						<PushIcon data-icon="inline-start" />
						{pushText}
					</Button>
				</TooltipTrigger>
				<TooltipContent>Pushing data to third-party service overwriting external scores.</TooltipContent>
			</Tooltip>

			<Tooltip>
				<TooltipTrigger asChild>
					<Button variant="outline" disabled={isSyncInProgress} onClick={() => enqueueSyncWithType("PULL")} aria-label={pullText}>
						<PullIcon data-icon="inline-start" />
						{pullText}
					</Button>
				</TooltipTrigger>
				<TooltipContent>Pulling data from third-party service adding new entries and overwriting local scores.</TooltipContent>
			</Tooltip>
		</ButtonGroup>
	);
}