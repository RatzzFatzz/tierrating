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

    while (itemsIndex < items.length && tiersIndex < tiers.length) {
        if (items[itemsIndex].score >= tiers[tiersIndex].score) {
            items[itemsIndex].tier = tiers[tiersIndex].name;
            itemsIndex++;
        } else {
            tiersIndex++;
        }
    }
}

export default function TierList({providerName}: {providerName: string}) {
    const [tiers, setTiers] = useState<Tier[]>([]);
    const [data, setData] = useState<TierlistEntry[]>([]);

    const {token, isLoading, isAuthenticated, logout} = useAuth();
    const params = useParams<{username: string}>();
    const username: string = params.username;

    const [tierlistQueryRunning, setTierlistQueryRunning] = useState(true);
    const [dataQueryRunning, setDataQueryRunning] = useState(true);
    const [mappingCompleted, setMappingCompleted] = useState(false);

    const [target, setTarget] = useState("arst");

    const provider = getProviderByName(providerName);
    if (!provider) {
        notFound();
    }

    useEffect(() => {
        if (!isLoading && isAuthenticated && provider) {
            provider.fetchTierlist(token, username, logout)
                .then((data: Tier[]) => setTiers(data && data.length > 0 ? data : getDefaultTiers()))
                .catch((error) => console.error(error))
                .finally(() => setTierlistQueryRunning(false));
        }
    }, [isLoading, isAuthenticated, provider, token, username, logout]);

    useEffect(() => {
        if (!isLoading && isAuthenticated && provider) {
            provider.fetchData(token, username, logout)
                .then((data: TierlistEntry[]) => setData(data))
                .catch((error) => console.error(error))
                .finally(() => setDataQueryRunning(false));
        }
    }, [isLoading, isAuthenticated, provider, token, username, logout]);

    useEffect(() => {
        if (!tierlistQueryRunning && !dataQueryRunning && !mappingCompleted) {
            TierMapper(tiers, data)
            setMappingCompleted(true)
        }
    }, [tiers, data, tierlistQueryRunning, dataQueryRunning, mappingCompleted])

    if (tierlistQueryRunning) {
        return <TierContainerSkeleton/>
    }

    if (dataQueryRunning) {
        return tiers.map((tier) => (<TierlistEntrySkeleton key={tier.id} color={tier.color} label={tier.name}/>));
    }

    const onDragEnd = async (event: { canceled: any; operation: { source: any; target: any; }; }) => {
        if (event.canceled) return;

        const {source, target} = event.operation;
        // replace with maps for O(1) access tiers[id: string, tier: Tier], data[data.id: string, data: TierlistEntry], dataByTier[data.tier: string, data: TierlistEntry]
        const matchedEntry = data.find(entry => entry.id === source.id);
        const matchedTier = tiers.find(tier => tier.id === target.id);

        if (!(matchedEntry?.tier && matchedTier?.name)) return;
        if (matchedEntry.tier === matchedTier.name) return;

        const prevEntryTier = matchedEntry.tier;
        const prevTier = tiers.find(tier => tier.name === prevEntryTier)

        console.debug(`Changing ${matchedEntry.tier} -> ${matchedTier.name} for ${matchedEntry.title}`)
        matchedEntry.tier = matchedTier?.name
        setTarget(event.operation.source?.id + event.operation.target?.id);

        setTimeout(() => {
            provider.updateData(matchedEntry.id, matchedTier.adjustedScore, token, username)
                .then(updateResponse => {
                    if (updateResponse.error) throw new Error(updateResponse.message);
                    console.debug("Committed changes to third-party service")
                })
                .catch(error => {
                    console.error(error);
                    matchedEntry.tier = prevEntryTier;
                    setTarget(matchedEntry.id + prevTier?.id)
                })
        }, 25);
    }

    return (
        <DragDropProvider onDragEnd={onDragEnd}>
            {tiers.map(tier => (
                <TierContainer key={tier.id} id={tier.id} label={tier.name} color={tier.color}>
                    {data
                        .filter(entry => entry.tier === tier.name)
                        .map(entry => (
                            target ? <TierlistEntryDraggable key={entry.id} entry={entry}/> : `Droppable ${tier.id}`
                        ))}
                </TierContainer>
            ))}
        </DragDropProvider>
    );
}