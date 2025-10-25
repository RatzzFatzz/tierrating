"use client"

import React, {useEffect, useState} from "react";
import {Tier, TierlistEntry} from "@/model/types";
import {getDefaultTiers} from "@/model/defaults";
import {useAuth} from "@/contexts/auth-context";
import {useParams} from "next/navigation";
import {getProviderByName} from "@/components/data-providers/data-provider";
import {TierContainerSkeleton, TierlistEntrySkeleton} from "@/components/loading-skeletons/tier-container-skeleton";
import TierlistEntryDraggable from "@/components/tierlist/tierlist-entry-draggable";
import {DragDropProvider} from "@dnd-kit/react";
import {assignTiersAndGroupEntriesByTier, groupBySingle, sortByName} from "@/components/tierlist/tier-mapper";
import TierContainerDroppable from "@/components/tierlist/tier-container";


export default function TierList({providerName}: {providerName: string}) {
    const [tiers, setTiers] = useState<Tier[]>([]);
    const [entries, setEntries] = useState<TierlistEntry[]>([]);

    const [entriesByTierId, setEntriesByTierId] = useState<Map<string, TierlistEntry[]>>(new Map());
    const [entriesById, setEntriesById] = useState<Map<string, TierlistEntry>>(new Map());
    const [tiersById, setTiersById] = useState<Map<string, Tier>>(new Map());
    const [tiersByName, setTiersByName] = useState<Map<string, Tier>>(new Map());

    const {token, isLoading, isAuthenticated, logout} = useAuth();
    const username: string = useParams<{username: string}>().username;

    const [tiersQueryRunning, setTiersQueryRunning] = useState(true);
    const [entriesQueryRunning, setEntriesQueryRunning] = useState(true);
    const [mappingCompleted, setMappingCompleted] = useState(false);

    const provider = getProviderByName(providerName);

    // fetch tiers
    useEffect(() => {
        if (!isLoading && isAuthenticated && provider) {
            provider.fetchTierlist(token, username, logout)
                .then((data: Tier[]) => setTiers(data && data.length > 0 ? data : getDefaultTiers()))
                .catch((error) => console.error(error))
                .finally(() => setTiersQueryRunning(false));
        }
    }, [isLoading, isAuthenticated, provider, token, username, logout]);

    // fetch entries
    useEffect(() => {
        if (!isLoading && isAuthenticated && provider) {
            provider.fetchData(token, username, logout)
                .then((data: TierlistEntry[]) => setEntries(data))
                .catch((error) => console.error(error))
                .finally(() => setEntriesQueryRunning(false));
        }
    }, [isLoading, isAuthenticated, provider, token, username, logout]);

    // perform mapping
    useEffect(() => {
        if (!tiersQueryRunning && !entriesQueryRunning && !mappingCompleted) {
            const entriesByTier = assignTiersAndGroupEntriesByTier(tiers, entries)
            setEntriesByTierId(entriesByTier);
            setEntriesById(groupBySingle(entries, entry => entry.id));
            setTiersById(groupBySingle(tiers, tier => tier.id));
            setTiersByName(groupBySingle(tiers, tier => tier.name));
            setMappingCompleted(true)
        }
    }, [tiers, entries, tiersQueryRunning, entriesQueryRunning, mappingCompleted])

    const onDragEnd = async (event: { canceled: any; operation: { source: any; target: any; }; }) => {
        if (event.canceled) return;

        if (!tiersById || !tiersByName || !entriesById) {
            console.error("Something went wrong while mapping. Refresh page")
            return;
        }

        const {source, target} = event.operation;
        const targetTier = tiersById.get(target.id);
        const entryToChange = entriesById.get(source.id);
        const currentTier = entryToChange?.tier;

        if (!(entryToChange?.tier && targetTier?.name)) return;
        if (entryToChange.tier === targetTier) return; // entry already in desired tier

        updateEntry(entryToChange, targetTier);

        // This kinda works for processing changes in the background, but this can only be spawned once.
        // As soon as a second action is triggered, it is blocked again, until the first one has completed
        setTimeout(() => {
            console.log(`timeout begin ${entryToChange.title}`)
            provider.updateData(entryToChange.id, targetTier.adjustedScore, token, username, logout)
                .then(updateResponse => {
                    if (updateResponse.error) throw new Error(updateResponse.message);
                    console.debug("Committed changes to third-party service")
                })
                .catch(error => {
                    console.error(error);
                    updateEntry(entryToChange, currentTier!);
                })
            console.log(`timeout end ${entryToChange.title}`)
        }, 200);
    }

    const updateEntry = (entryToChange: TierlistEntry, targetTier: Tier) => {
        console.debug(`${entryToChange.title}: ${entryToChange.tier.name} -> ${targetTier.name}`)

        const prevTier = entryToChange.tier;

        entryToChange.tier = targetTier
        entryToChange.score = targetTier?.adjustedScore

        // add entryToChange to new tier
        entriesByTierId.set(targetTier.id, [...entriesByTierId.get(targetTier.id)!, entryToChange].sort(sortByName))
        // remove entryToChange from its current tier
        setEntriesByTierId(prevMap => {
            const currentEntries = prevMap.get(prevTier.id)!;
            const newMap = new Map(prevMap);
            const updatedEntries = currentEntries.filter(entry => entry.id !== entryToChange.id);
            newMap.set(prevTier.id, updatedEntries);
            return newMap;
        })
    }

    if (tiersQueryRunning) {
        return <TierContainerSkeleton/>
    }

    if (entriesQueryRunning || !mappingCompleted) {
        return tiers.map((tier) => (<TierlistEntrySkeleton key={tier.id} color={tier.color} label={tier.name}/>));
    }

    entriesByTierId.keys().forEach(id => {
        console.debug(`Tier ${tiersById.get(id)?.name}`)
        entriesByTierId.get(id)?.forEach(entry => {
            console.debug(`- ${entry.title} in ${entry.tier.name}`)
        })
    })

    return (
        <DragDropProvider onDragEnd={onDragEnd}>
            {Array.from(entriesByTierId.keys())
                .map(tierId => tiersById.get(tierId))
                .map(tier => (
                    tier &&
                    <TierContainerDroppable key={tier.id} id={tier.id} label={tier.name} color={tier.color}>
                        {Array.from(entriesByTierId.get(tier.id)!).map(entry => (
                            <TierlistEntryDraggable key={entry.id} entry={entry}/>
                        ))}
                    </TierContainerDroppable>
                ))}
        </DragDropProvider>
    );
}