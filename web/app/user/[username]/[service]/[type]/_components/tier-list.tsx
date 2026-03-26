"use client";

import React, { startTransition, useEffect, useMemo, useState } from "react";
import { Tier, TierlistEntry } from "@/types/types";
import { TierlistEntrySkeleton } from "@/components/loading-skeletons/tier-container-skeleton";
import { TierlistEntryCard, TierlistEntryDraggable } from "@/app/user/[username]/[service]/[type]/_components/tierlist-entry-draggable";
import { DragDropProvider, DragOverlay } from "@dnd-kit/react";
import { assignTiersAndGroupEntriesByTier, groupBySingle, sortByName } from "@/lib/mapper/tier-mapper";
import TierContainerDroppable from "@/app/user/[username]/[service]/[type]/_components/tier-container-droppable";
import { toast } from "sonner";
import { useTiers } from "@/lib/services/tiers-service";
import { useScoreMutation, useTierlistEntries } from "@/lib/services/data-service";
import { useAuth } from "@/contexts/auth-context";
import { getDefaultTiers } from "@/lib/default-tiers";
import { LoadingPage } from "@/components/loading-skeletons/loading-page";

export default function TierList({
	username,
	service,
	type,
	modificationEnabled,
}: {
	username: string;
	service: string;
	type: string;
	modificationEnabled: boolean;
}) {
	const { token, user, logout } = useAuth();

	const { trigger: pushEntryUpdate, error, isMutating } = useScoreMutation(username, service, type, token!);
	const { data: tiersData, error: tiersError, isValidating: tiersIsLoading } = useTiers(username, service, type, token!);
	const {
		data: entriesData,
		error: entriesError,
		mutate: entriesMutate,
		isValidating: entriesIsLoading,
	} = useTierlistEntries(username, service, type, token!);
	const isLoading = tiersIsLoading || entriesIsLoading;
	const tiers = useMemo(() => (tiersData?.length ? tiersData : getDefaultTiers()), [tiersData]);
	const entries = useMemo(() => entriesData ?? [], [entriesData]);


	const tiersById = useMemo(() => groupBySingle(tiers, (tier) => tier.id), [tiers]);
	const tiersByName = useMemo(() => groupBySingle(tiers, (tier) => tier.name), [tiers]);
	const entriesById = useMemo(() => groupBySingle(entries, (entry) => entry.id), [entries]);

	const initialEntriesByTierId = useMemo(() => assignTiersAndGroupEntriesByTier(tiers, entries), [tiers, entries]);
	const [entriesByTierId, setEntriesByTierId] = useState<Map<string, TierlistEntry[]>>(new Map()); // mutated by user
	const mappingCompleted = entriesByTierId.size > 0;

	useEffect(() => {
		queueMicrotask(() => {
			setEntriesByTierId(initialEntriesByTierId);
		});
	}, [initialEntriesByTierId, entriesByTierId.size]);

	const onDragEnd = async (event: { canceled: any; operation: { source: any; target: any } }) => {
		if (event.canceled) return;

		if (!entriesByTierId || !tiersById || !tiersByName || !entriesById) {
			toast.error("Error occurred. Please refresh the page!");
			return;
		}

		const { source, target } = event.operation;
		const entryToChange = entriesById.get(source.id)!;
		const sourceTier = entryToChange.tier!;
		const targetTier = tiersById.get(target.id)!;

		if (!(entryToChange.tier && targetTier.name)) return;
		if (entryToChange.tier === targetTier) return; // entry already in desired tier

		updateEntry(entryToChange, targetTier, sourceTier);

		startTransition(() => {
			pushEntryUpdate({ id: entryToChange.id, score: targetTier.adjustedScore })
				.catch((error) => {
					toast.error(`Couldn't update ${entryToChange.title}. Reverted change.\n Error: ${error.message}`);
					updateEntry(entryToChange, sourceTier!, targetTier);
				});
		});
	};

	const updateEntry = (entryToChange: TierlistEntry, targetTier: Tier, sourceTier: Tier) => {
		console.debug(`${entryToChange.title}: ${sourceTier.name} -> ${targetTier.name}`);

		const updatedEntry = {
			...entryToChange,
			tier: targetTier,
			score: targetTier.adjustedScore,
		}

		setEntriesByTierId((prevMap) => {
			const newMap = new Map(prevMap);
			if (sourceTier.id !== targetTier.id) {
				// add entryToChange to new tier
				const targetEntries = [...newMap.get(targetTier.id)!, updatedEntry].sort(sortByName);
				newMap.set(targetTier.id, targetEntries);
				// remove entryToChange from its current tier
				const updatedEntries = [...newMap.get(sourceTier.id)!.filter((entry) => entry.id !== entryToChange.id)];
				newMap.set(sourceTier.id, updatedEntries);
				// update element to avoid stale data
				entriesById.set(updatedEntry.id, updatedEntry);
			}
			return newMap;
		});
	};

	if (isLoading && !entriesData && !tiersData) return <LoadingPage />;

	if (tiersError || entriesError) {
		if (tiersError?.status === 404 || entriesError?.status === 404) {
			return (
				<div>Tierlist of user does not exist or is private.</div>
			)
		}
		return (
			<div>Error occurred while fetching your data. Please try again later. If the error persists contact the server admin.</div>
		)
	}

	if (!mappingCompleted) {
		return tiers.map((tier) => <TierlistEntrySkeleton key={tier.id} color={tier.color} label={tier.name} />);
	}

	return (
		<DragDropProvider onDragEnd={onDragEnd}>
			{tiers.map((tier) => (
				<TierContainerDroppable
					key={tier.id}
					id={tier.id}
					label={tier.name}
					color={tier.color}
					disabled={!modificationEnabled}
				>
					{entriesByTierId.get(tier.id)!.map((entry) => (
						<TierlistEntryDraggable key={entry.id} entry={entry} disabled={!modificationEnabled} />
					))}
				</TierContainerDroppable>
			))}
			<DragOverlay>
				{(source) => (
					// @ts-expect-error - Type mismatch in drag overlay source data
					<TierlistEntryCard key={source.id} entry={source.data} />
				)}
			</DragOverlay>
		</DragDropProvider>
	);
}
