"use client"

import React, {useEffect, useState} from "react"

import {DndProvider} from "react-dnd"
import {HTML5Backend} from "react-dnd-html5-backend"
import {TierContainer} from "./tier-container"
import {TierlistEntry, Tier} from "@/model/types"
import {memo} from "react"
import {useAuth} from "@/contexts/AuthContext";
import {TierContainerSkeleton, TierlistEntrySkeleton} from "@/components/loading-skeletons/tier-container-skeleton";
import {getDefaultTiers} from "@/model/defaults";
import {getProviderByName} from "@/components/data-providers/data-provider";
import {notFound, useParams} from "next/navigation";

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

export const TierList = memo(function TierList({providerName}: {providerName: string},) {
    const [tiers, setTiers] = useState<Tier[]>([]);
    const [data, setData] = useState<TierlistEntry[]>([]);

    const {token, isLoading, isAuthenticated, logout} = useAuth();
    const params = useParams<{username: string}>();
    const username: string = params.username;

    const [tierlistQueryRunning, setTierlistQueryRunning] = useState(true);
    const [dataQueryRunning, setDataQueryRunning] = useState(true);
    const [mappingCompleted, setMappingCompleted] = useState(false);

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

    const moveItem = (dragIndex: number, hoverIndex: number, sourceTier: string, targetTier: string) => {
        console.debug(dragIndex, hoverIndex, sourceTier, targetTier);
        setData((prevItems) => {
            // Create a new array to avoid mutating the state directly
            const newItems = [...prevItems]

            // Find the item being dragged by its tier and index
            const draggedItemIndex = newItems.findIndex(
                (item) =>
                    item.tier === sourceTier && newItems.filter((i) => i.tier === sourceTier).indexOf(item) === dragIndex,
            )

            if (draggedItemIndex === -1) return prevItems

            // Get the dragged item
            const draggedItem = {...newItems[draggedItemIndex]}

            // Remove the dragged item from the array
            newItems.splice(draggedItemIndex, 1)

            // Update the tier of the dragged item
            draggedItem.tier = targetTier

            // Calculate the insertion index in the target tier
            const itemsInTargetTier = newItems.filter((item) => item.tier === targetTier)

            // If hoverIndex is greater than the length, place at the end
            const insertAtIndex = Math.min(hoverIndex, itemsInTargetTier.length)

            // Find where to insert in the overall array
            let insertionIndex = 0
            let tierItemsCount = 0

            for (let i = 0; i < newItems.length; i++) {
                if (newItems[i].tier === targetTier) {
                    if (tierItemsCount === insertAtIndex) {
                        insertionIndex = i
                        break
                    }
                    tierItemsCount++
                    insertionIndex = i + 1
                } else if (
                    tierItemsCount === insertAtIndex &&
                    (i === 0 || newItems[i - 1].tier === targetTier) &&
                    newItems[i].tier !== targetTier
                ) {
                    insertionIndex = i
                    break
                }
            }

            // Insert the dragged item at the calculated position
            newItems.splice(insertionIndex, 0, draggedItem)

            return newItems
        })
    }

    return (
        <DndProvider backend={HTML5Backend}>
            <div className="grid grid-cols-1 gap-2">
                {tierlistQueryRunning ? (
                    <TierContainerSkeleton/>
                ) : (
                    tiers.map((tier) => (
                        dataQueryRunning ? (
                            <TierlistEntrySkeleton key={tier.id} color={tier.color} label={tier.name}/>
                        ) : (
                            <TierContainer
                                key={tier.id}
                                tier={tier.name}
                                label={tier.name}
                                color={tier.color}
                                items={data.filter((item) => item.tier === tier.name)}
                                moveItem={moveItem}
                            />
                    ))))}
            </div>
        </DndProvider>
    )
})
