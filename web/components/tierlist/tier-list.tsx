"use client";

import React, { useEffect, useMemo, useState } from "react";
import { Tier, TierlistEntry } from "@/types/types";
import { TierlistEntrySkeleton } from "@/components/loading-skeletons/tier-container-skeleton";
import { TierlistEntryCard, TierlistEntryDraggable } from "@/components/tierlist/tierlist-entry-draggable";
import { DragDropProvider, DragOverlay } from "@dnd-kit/react";
import { assignTiersAndGroupEntriesByTier, groupBySingle, sortByName } from "@/components/tierlist/tier-mapper";
import TierContainerDroppable from "@/components/tierlist/tier-container-droppable";
import { toast } from "sonner";
import { ServerResponse } from "@/types/api-response";

export default function TierList({
	tiers,
	entries,
	modificationAllowed,
	pushEntryUpdateAction,
}: {
	tiers: Tier[];
	entries: TierlistEntry[];
	modificationAllowed: boolean;
	pushEntryUpdateAction: (id: string, score: number) => Promise<ServerResponse<unknown>>;
}) {
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
		const targetTier = tiersById.get(target.id);
		const entryToChange = entriesById.get(source.id);
		const currentTier = entryToChange?.tier;

		if (!(entryToChange?.tier && targetTier?.name)) return;
		if (entryToChange.tier === targetTier) return; // entry already in desired tier

		updateEntry(entryToChange, targetTier);

		// This kinda works for processing changes in the background, but this can only be spawned once.
		// As soon as a second action is triggered, it is blocked again, until the first one has completed
		setTimeout(() => {
			pushEntryUpdateAction(entryToChange.id, targetTier.adjustedScore)
				.then(response => {
					if (!response.ok) throw new Error(response.error);
					console.debug("Committed changes to third-party service");
				})
				.catch((error) => {
					console.error(error);
					updateEntry(entryToChange, currentTier!);
					toast.error(`Couldn't update ${entryToChange.title}. Reverted change.`)
				});
		}, 200);
	};

	const updateEntry = (entryToChange: TierlistEntry, targetTier: Tier) => {
		console.debug(`${entryToChange.title}: ${entryToChange.tier.name} -> ${targetTier.name}`);

		const prevTier = entryToChange.tier;

		entryToChange.tier = targetTier;
		entryToChange.score = targetTier?.adjustedScore;

		// add entryToChange to new tier
		entriesByTierId.set(targetTier.id, [...entriesByTierId.get(targetTier.id)!, entryToChange].sort(sortByName));
		// remove entryToChange from its current tier
		setEntriesByTierId((prevMap) => {
			const currentEntries = prevMap.get(prevTier.id)!;
			const newMap = new Map(prevMap);
			const updatedEntries = currentEntries.filter((entry) => entry.id !== entryToChange.id);
			newMap.set(prevTier.id, updatedEntries);
			return newMap;
		});
	};

	if (!mappingCompleted) {
		return tiers.map((tier) => <TierlistEntrySkeleton key={tier.id} color={tier.color} label={tier.name} />);
	}

	return (
		<DragDropProvider onDragEnd={onDragEnd}>
			{Array.from(entriesByTierId.keys())
				.map((tierId) => tiersById.get(tierId))
				.map(
					(tier) =>
						tier && (
							<TierContainerDroppable
								key={tier.id}
								id={tier.id}
								label={tier.name}
								color={tier.color}
								disabled={!modificationAllowed}
							>
								{Array.from(entriesByTierId.get(tier.id)!).map((entry) => (
									<TierlistEntryDraggable key={entry.id} entry={entry} disabled={!modificationAllowed} />
								))}
							</TierContainerDroppable>
						)
				)}
			<DragOverlay>
				{(source) => (
					// @ts-expect-error - Type mismatch in drag overlay source data
					<TierlistEntryCard key={source.id} entry={source.data} />
				)}
			</DragOverlay>
		</DragDropProvider>
	);
}
