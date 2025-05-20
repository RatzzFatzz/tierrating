"use client";

import { useState, useEffect } from "react";
import Image from "next/image";
import { AspectRatio } from "@/components/ui/aspect-ratio";
import {
    DndContext,
    DragOverlay,
    closestCenter,
    KeyboardSensor,
    PointerSensor,
    useSensor,
    useSensors,
    DragEndEvent,
    DragOverEvent,
    DragStartEvent,
    useDroppable,
} from "@dnd-kit/core";
import {
    arrayMove,
    SortableContext,
    sortableKeyboardCoordinates,
    useSortable,
    horizontalListSortingStrategy,
} from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";

interface RatingItem {
    score: number;
    media: {
        id: number
        title: {
            romaji: string;
            english: string;
        };
        coverImage: {
            large: string;
            extraLarge: string;
        };
    };
    tier?: string; // Added tier property
}

interface TierConfig {
    id: string;
    name: string;
    color: string;
}

// Define the tiers
const tiers: TierConfig[] = [
    { id: "s", name: "S", color: "bg-red-500" },
    { id: "a", name: "A", color: "bg-orange-500" },
    { id: "b", name: "B", color: "bg-yellow-500" },
    { id: "c", name: "C", color: "bg-green-500" },
    { id: "d", name: "D", color: "bg-blue-500" },
    { id: "f", name: "F", color: "bg-purple-500" },
];

// Draggable Item Component
function DraggableItem({ item }: { item: RatingItem }) {
    const { attributes, listeners, setNodeRef, transform, transition } = useSortable({
        id: item.media.id.toString(),
    });

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
    };

    return (
        <div
            ref={setNodeRef}
            style={style}
            {...attributes}
            {...listeners}
            className="cursor-grab active:cursor-grabbing"
        >
            <div className="w-30">
                <AspectRatio ratio={460 / 650}>
                    <Image
                        fill={true}
                        src={item.media.coverImage.large}
                        alt={item.media.title.english || item.media.title.romaji}
                        className="rounded-md object-cover"
                    />
                </AspectRatio>
                <p className="truncate text-sm mt-1">{item.media.title.english || item.media.title.romaji}</p>
            </div>
        </div>
    );
}

// Droppable Tier Container
function DroppableTier({ tier, items }: { tier: TierConfig; items: RatingItem[] }) {
    const { setNodeRef } = useDroppable({
        id: `tier-${tier.id}`,
    });

    const tierItems = items.filter((item) => item.tier === tier.id);

    return (
        <div className="flex mb-4">
            <div className={`${tier.color} w-24 flex items-center justify-center text-white font-bold text-2xl rounded-l-md`}>
                {tier.name}
            </div>
            <div
                ref={setNodeRef}
                className="flex-1 bg-gray-100 min-h-[160px] p-4 rounded-r-md"
            >
                <SortableContext items={tierItems.map(item => item.media.id.toString())} strategy={horizontalListSortingStrategy}>
                    <div className="grid grid-cols-6 gap-4">
                        {tierItems.map((item) => (
                            <DraggableItem key={item.media.id} item={item} />
                        ))}
                    </div>
                </SortableContext>
            </div>
        </div>
    );
}

// Droppable Unassigned Container
function DroppableUnassigned({ items }: { items: RatingItem[] }) {
    const { setNodeRef } = useDroppable({
        id: 'unassigned',
    });

    return (
        <div ref={setNodeRef} className="bg-gray-100 p-4 rounded-md min-h-[160px]">
            <SortableContext items={items.map(item => item.media.id.toString())} strategy={horizontalListSortingStrategy}>
                <div className="grid grid-cols-6 gap-4">
                    {items.map((item) => (
                        <DraggableItem key={item.media.id} item={item} />
                    ))}
                </div>
            </SortableContext>
        </div>
    );
}

function transformScoreToTier(score?: number): string {
    if (score === undefined) return "unassigned";

    // Divide score by 2
    const normalizedScore = score / 2;

    // Map the normalized score to a tier
    if (normalizedScore >= 5) return "s";
    if (normalizedScore >= 4) return "a";
    if (normalizedScore >= 3) return "b";
    if (normalizedScore >= 2) return "c";
    if (normalizedScore >= 1) return "d";
    if (normalizedScore >= 0) return "f";

    return "unassigned"; // Default for negative scores or other edge cases
}

export default function TierList({type}) {
    const [items, setItems] = useState<RatingItem[]>([]);
    const [activeId, setActiveId] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    const sensors = useSensors(
        useSensor(PointerSensor),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        })
    );

    useEffect(() => {
        async function fetchData() {
            try {
                const response = await fetch("http://localhost:8080/data/anilist/ratzzfatzz/" + type);
                const data = await response.json();
                // Initialize all items as unassigned
                setItems(data.map((item: RatingItem) => ({ ...item, tier: transformScoreToTier(item.score) })));
            } catch (error) {
                console.error("Error fetching data:", error);
            } finally {
                setLoading(false);
            }
        }

        fetchData();
    }, []);

    const handleDragStart = (event: DragStartEvent) => {
        setActiveId(event.active.id.toString());
    };

    const handleDragOver = (event: DragOverEvent) => {
        const { active, over } = event;

        if (!over) return;

        // Check if we're dragging over a tier container
        if (over.id.toString().startsWith('tier-')) {
            const tierId = over.id.toString().replace('tier-', '');

            setItems((items) =>
                items.map((item) =>
                    item.media.id.toString() === active.id.toString()
                        ? { ...item, tier: tierId }
                        : item
                )
            );
        }

        // Check if we're dragging over the unassigned container
        if (over.id === 'unassigned') {
            setItems((items) =>
                items.map((item) =>
                    item.media.id.toString() === active.id.toString()
                        ? { ...item, tier: "unassigned" }
                        : item
                )
            );
        }
    };

    const handleDragEnd = (event: DragEndEvent) => {
        const { active, over } = event;

        if (!over) return;

        // Handle reordering within the same tier
        if (active.id !== over.id && !over.id.toString().startsWith('tier-') && over.id !== 'unassigned') {
            setItems((items) => {
                const activeItem = items.find(item => item.media.id.toString() === active.id.toString());
                const overItem = items.find(item => item.media.id.toString() === over.id.toString());

                // Only reorder if they're in the same tier
                if (activeItem && overItem && activeItem.tier === overItem.tier) {
                    const oldIndex = items.findIndex(item => item.media.id.toString() === active.id.toString());
                    const newIndex = items.findIndex(item => item.media.id.toString() === over.id.toString());
                    return arrayMove(items, oldIndex, newIndex);
                }

                return items;
            });
        }

        setActiveId(null);
    };

    if (loading) {
        return <div className="flex justify-center items-center h-screen">Loading...</div>;
    }

    const activeItem = activeId ? items.find((item) => item.media.id.toString() === activeId) : null;
    const unassignedItems = items.filter((item) => item.tier === "unassigned");

    return (
        <main className="container mx-auto py-8 px-4">
            <h1 className="text-3xl font-bold mb-6">AniList - Manga Tier List</h1>

            <DndContext
                sensors={sensors}
                collisionDetection={closestCenter}
                onDragStart={handleDragStart}
                onDragOver={handleDragOver}
                onDragEnd={handleDragEnd}
            >
                {/* Tier Containers */}
                {tiers.map((tier) => (
                    <DroppableTier key={tier.id} tier={tier} items={items} />
                ))}

                {/* Unassigned Items Container */}
                <div className="mt-8">
                    <h2 className="text-xl font-bold mb-4">Unassigned Items</h2>
                    <DroppableUnassigned items={unassignedItems} />
                </div>

                {/* Drag Overlay */}
                <DragOverlay>
                    {activeId && activeItem ? (
                        <div className="opacity-80">
                            <AspectRatio ratio={460 / 650}>
                                <Image
                                    fill={true}
                                    src={activeItem.media.coverImage.large}
                                    alt={activeItem.media.title.english || activeItem.media.title.romaji}
                                    className="rounded-md object-cover"
                                />
                            </AspectRatio>
                            <p className="truncate text-sm mt-1">{activeItem.media.title.english || activeItem.media.title.romaji}</p>
                        </div>
                    ) : null}
                </DragOverlay>
            </DndContext>
        </main>
    );
}