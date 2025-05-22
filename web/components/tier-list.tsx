"use client"

import { useState } from "react"
import {
    DndContext,
    DragOverlay,
    closestCenter,
    KeyboardSensor,
    PointerSensor,
    useSensor,
    useSensors,
    type DragStartEvent,
    type DragEndEvent,
} from "@dnd-kit/core"
import { sortableKeyboardCoordinates } from "@dnd-kit/sortable"

import TierContainer from "@/components/tier-container"
import Item from "@/components/item"

export interface RatingItem {
    id: string;
    score: number;

    title: string;
    cover: string;

    tier: string;
}

export default function TierList({itemList}: {itemList: RatingItem[]}) {
    // Initial items in the unassigned container
    const [items, setItems] = useState<RatingItem[]>(itemList);

    const [activeId, setActiveId] = useState<string | null>(null)

    const tiers = ["s", "a", "b", "c", "d", "f", "unassigned"]

    // Configure the sensors
    const sensors = useSensors(
        useSensor(PointerSensor, {
            activationConstraint: {
                distance: 8, // Increase activation distance for better drag detection
            },
        }),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        }),
    )

    // Get items for a specific tier
    const getItemsByTier = (tier: string) => {
        return items.filter((item) => item.tier === tier)
    }

    // Handle drag start
    const handleDragStart = (event: DragStartEvent) => {
        const { active } = event
        setActiveId(active.id as string)
    }

    // Handle drag end
    const handleDragEnd = (event: DragEndEvent) => {
        const { active, over } = event

        if (over && active.id !== over.id) {
            setItems((items) => {
                // Find the item being dragged
                const activeItem = items.find((item) => item.id === active.id)

                if (!activeItem) return items

                const overId = over.id as string

                // Check if dropping directly onto a container
                if (tiers.includes(overId)) {
                    // Moving to a different container - place at the end
                    return items.map((item) => {
                        if (item.id === active.id) {
                            return { ...item, tier: overId }
                        }
                        return item
                    })
                } else {
                    // Dropping onto or near another item
                    const overItem = items.find((item) => item.id === overId)

                    if (!overItem) return items

                    // If moving within the same container, reorder
                    if (activeItem.tier === overItem.tier) {
                        const itemsInTier = items.filter((item) => item.tier === activeItem.tier)
                        const otherItems = items.filter((item) => item.tier !== activeItem.tier)

                        const activeIndex = itemsInTier.findIndex((item) => item.id === active.id)
                        const overIndex = itemsInTier.findIndex((item) => item.id === over.id)

                        // Reorder the items in the tier
                        const newItemsInTier = [...itemsInTier]
                        const [movedItem] = newItemsInTier.splice(activeIndex, 1)
                        newItemsInTier.splice(overIndex, 0, movedItem)

                        return [...otherItems, ...newItemsInTier]
                    } else {
                        // Moving to a different container - insert at the position of the target item
                        const targetTier = overItem.tier
                        const itemsInTargetTier = items.filter((item) => item.tier === targetTier)
                        const otherItems = items.filter((item) => item.tier !== targetTier && item.id !== active.id)

                        // Find the position to insert at
                        const overIndex = itemsInTargetTier.findIndex((item) => item.id === over.id)

                        // Create the moved item with new tier
                        const movedItem = { ...activeItem, tier: targetTier }

                        // Insert the item at the correct position
                        const newItemsInTargetTier = [...itemsInTargetTier]
                        newItemsInTargetTier.splice(overIndex, 0, movedItem)

                        return [...otherItems, ...newItemsInTargetTier]
                    }
                }
            })
        }
        setActiveId(null)
    }

    // Find the active item
    const activeItem = activeId ? items.find((item) => item.id === activeId) : null

    return (
        <div className="container mx-auto p-4 max-w-5xl">
            <h1 className="text-3xl font-bold mb-6 text-center">Tier Maker</h1>

            <DndContext
                sensors={sensors}
                collisionDetection={closestCenter}
                onDragStart={handleDragStart}
                onDragEnd={handleDragEnd}
            >
                <div className="space-y-4">
                    {tiers.map((tier) => (
                        <TierContainer
                            key={tier}
                            id={tier}
                            label={tier === "unassigned" ? "Unassigned" : tier.toUpperCase()}
                            items={getItemsByTier(tier)}
                            activeId={activeId}
                        />
                    ))}
                </div>

                <DragOverlay>
                    {activeId && activeItem ? <Item id={activeItem.id} name={activeItem.title} image={activeItem.cover} /> : null}
                </DragOverlay>
            </DndContext>
        </div>
    )
}
