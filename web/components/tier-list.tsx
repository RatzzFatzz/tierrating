"use client"

import type React from "react"

import { DndProvider } from "react-dnd"
import { HTML5Backend } from "react-dnd-html5-backend"
import { TierContainer } from "./tier-container"
import type { RatingItem } from "@/model/types"
import { memo } from "react"

interface TierListProps {
    items: RatingItem[]
    setItems: React.Dispatch<React.SetStateAction<RatingItem[]>>
}

const tiers = [
    { id: "s", label: "S", color: "bg-red-500" },
    { id: "a", label: "A", color: "bg-orange-500" },
    { id: "b", label: "B", color: "bg-yellow-500" },
    { id: "c", label: "C", color: "bg-green-500" },
    { id: "d", label: "D", color: "bg-blue-500" },
    { id: "f", label: "F", color: "bg-purple-500" },
    { id: "unassigned", label: "", color: "bg-gray-300" },
]

export const TierList = memo(function TierList({ items, setItems }: TierListProps) {
    const moveItem = (dragIndex: number, hoverIndex: number, sourceTier: string, targetTier: string) => {
        setItems((prevItems) => {
            // Create a new array to avoid mutating the state directly
            const newItems = [...prevItems]

            // Find the item being dragged by its tier and index
            const draggedItemIndex = newItems.findIndex(
                (item, idx) =>
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
            <div>
                {tiers.map((tier) => (
                    <TierContainer
                        key={tier.id}
                        tier={tier.id}
                        label={tier.label}
                        color={tier.color}
                        items={items.filter((item) => item.tier === tier.id)}
                        moveItem={moveItem}
                    />
                ))}
            </div>
        </DndProvider>
    )
})
