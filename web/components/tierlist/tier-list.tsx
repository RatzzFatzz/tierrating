"use client"

import React, {useEffect, useState} from "react";
import {Tier, TierlistEntry} from "@/model/types";
import {getDefaultTiers} from "@/model/defaults";
import {useAuth} from "@/contexts/auth-context";
import {notFound, useParams} from "next/navigation";
import {getProviderByName} from "@/components/data-providers/data-provider";
import {TierContainerSkeleton, TierlistEntrySkeleton} from "@/components/loading-skeletons/tier-container-skeleton";
import TierContainer from "@/components/tierlist/tier-container";
import TierlistEntryDraggable from "@/components/tierlist/tierlist-entry-draggable";
import {DragDropProvider} from "@dnd-kit/react";

const groupBy = <T, K extends keyof any>(arr: T[], key: (i: T) => K) =>
    arr.reduce((groups, item) => {
        (groups[key(item)] ||= []).push(item);
        return groups;
    }, {} as Record<K, T[]>);

const TierMapper = (tiers: Tier[], items: TierlistEntry[]) => {
    // Proper order (sorted descending by score) is assured by the server
    let itemsIndex = 0;
    let tiersIndex = 0;
    let positionIndex = 0;

    while (itemsIndex < items.length && tiersIndex < tiers.length) {
        if (items[itemsIndex].score >= tiers[tiersIndex].score) {
            items[itemsIndex].tier = tiers[tiersIndex].name;
            items[itemsIndex].index = positionIndex;
            positionIndex++;
            itemsIndex++;
        } else {
            tiersIndex++;
            positionIndex = 0;
        }
    }
}

export default function TierList({providerName}: {providerName: string}) {
    const [tiers, setTiers] = useState<Tier[]>([]);
    const [entries, setEntries] = useState<TierlistEntry[]>([]);
    const [tiersById, setTiersById] = useState<Record<string, Tier[]>>();
    const [tiersByTier, setTiersByTier] = useState<Record<string, Tier[]>>();
    const [entriesById, setEntriesById] = useState<Record<string, TierlistEntry[]>>();

    const {token, isLoading, isAuthenticated, logout} = useAuth();
    const username: string = useParams<{username: string}>().username;

    const [tiersQueryRunning, setTiersQueryRunning] = useState(true);
    const [entriesQueryRunning, setEntriesQueryRunning] = useState(true);
    const [mappingCompleted, setMappingCompleted] = useState(false);

    const [target, setTarget] = useState("arst");
    const updateEntry = (entry: TierlistEntry, tier: Tier) => {
        console.debug(`Changing ${entry.tier} -> ${tier.name} for ${entry.title}`)
        entry.tier = tier?.name
        entry.score = tier?.adjustedScore
        setTarget(entry.id + tier.id);
    }

    const provider = getProviderByName(providerName);
    if (!provider) {
        notFound();
    }

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
            TierMapper(tiers, entries)
            setEntriesById(groupBy(entries, entry => entry.id))
            setTiersById(groupBy(tiers, tier => tier.id))
            setTiersByTier(groupBy(tiers, tier => tier.name));
            setMappingCompleted(true)
        }
    }, [tiers, entries, tiersQueryRunning, entriesQueryRunning, mappingCompleted])

    if (tiersQueryRunning) {
        return <TierContainerSkeleton/>
    }

    if (entriesQueryRunning) {
        return tiers.map((tier) => (<TierlistEntrySkeleton key={tier.id} color={tier.color} label={tier.name}/>));
    }

    const onDragEnd = async (event: { canceled: any; operation: { source: any; target: any; }; }) => {
        if (event.canceled) return;

        if (!tiersById || !tiersByTier || !entriesById) {
            console.error("Something went wrong while mapping. Refresh page")
            return;
        }

        const {source, target} = event.operation;
        const matchedTier = tiersById[target.id][0];
        const matchedEntry = entriesById[source.id][0];
        const currentTier = tiersByTier[matchedEntry.tier][0];

        if (!(matchedEntry?.tier && matchedTier?.name)) return;
        if (matchedEntry.tier === matchedTier.name) return; // entry already in desired tier

        updateEntry(matchedEntry, matchedTier);

        // This kinda works for processing changes in the background, but this can only be spawned once.
        // As soon as a second action is triggered, it is blocked again, until the first one has completed
        setTimeout(() => {
            console.log(`timeout begin ${matchedEntry.title}`)
            provider.updateData(matchedEntry.id, matchedTier.adjustedScore, token, username, logout)
                .then(updateResponse => {
                    if (updateResponse.error) throw new Error(updateResponse.message);
                    console.debug("Committed changes to third-party service")
                })
                .catch(error => {
                    console.error(error);
                    updateEntry(matchedEntry, currentTier);
                })
            console.log(`timeout end ${matchedEntry.title}`)
        }, 200);
    }

    return (
        <DragDropProvider onDragEnd={onDragEnd}>
            {tiers.map(tier => (
                <TierContainer key={tier.id} id={tier.id} label={tier.name} color={tier.color}>
                    {entries
                        .filter(entry => entry.tier === tier.name)
                        .map(entry => (
                            target ? <TierlistEntryDraggable key={entry.id} entry={entry} column={tier.id}/> : `Droppable ${tier.id}`
                        ))}
                </TierContainer>
            ))}
        </DragDropProvider>
    );
}