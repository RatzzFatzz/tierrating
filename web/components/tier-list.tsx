"use client"

import React, {useEffect, useState} from "react"

import { DndProvider } from "react-dnd"
import { HTML5Backend } from "react-dnd-html5-backend"
import { TierContainer } from "./tier-container"
import {RatingItem, Tier} from "@/model/types"
import { memo } from "react"
import {useAuth} from "@/contexts/AuthContext";
import {fetchTiers} from "@/components/api/tier-api";
import TierContainerSkeleton from "@/components/loading-skeletons/tier-container-skeleton";

interface TierListProps {
    items: RatingItem[]
    setItems: React.Dispatch<React.SetStateAction<RatingItem[]>>
}

const TierMapper = (tiers: Tier[], items: RatingItem[]) => {
    // TODO validate that this sorting is really necessary
    items.sort((a, b) => b.score - a.score);
    tiers.sort((a, b) => b.score - a.score);

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

export const TierList = memo(function TierList({ items, setItems }: TierListProps) {
    const [tiers, setTiers] = useState<Tier[]>([]);
    const [queryRunning, setQueryRunning] = useState(true);
    const { user, token, isLoading, isAuthenticated, logout } = useAuth();
    const username = "RatzzFatzz";

    useEffect(() => {
        if (!isLoading && isAuthenticated) {
            fetchTiers(token, username, "anilist", "anime", logout)
                .then((data: Tier[]) => data.sort((a, b) => b.score - a.score))
                .then(data => {
                    setTiers(data)
                    TierMapper(data, items)
                })
                .catch((error) => console.error(error))
                .finally(() => setQueryRunning(false));
        }
    }, [username, items, isLoading, isAuthenticated, token, logout]);

    const moveItem = (dragIndex: number, hoverIndex: number, sourceTier: string, targetTier: string) => {
        console.debug(dragIndex, hoverIndex, sourceTier, targetTier);
        setItems((prevItems) => {
            // Create a new array to avoid mutating the state directly
            const newItems = [...prevItems]

            // Find the item being dragged by its tier and index
            const draggedItemIndex = newItems.findIndex(
                (item) =>
                    item.tier === sourceTier && newItems.filter((i) => i.tier === sourceTier).indexOf(item) === dragIndex,
            )

            if (draggedItemIndex === -1) return prevItems

            // Get the dragged item
            const draggedItem = { ...newItems[draggedItemIndex] }

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
                {queryRunning ? (
                    <TierContainerSkeleton/>
                ) : (
                tiers.map((tier) => (
                    <TierContainer
                        key={tier.name}
                        tier={tier.name}
                        label={tier.name}
                        color={tier.color}
                        items={items.filter((item) => item.tier === tier.name)}
                        moveItem={moveItem}
                    />
                )))}
            </div>
        </DndProvider>
    )
})
