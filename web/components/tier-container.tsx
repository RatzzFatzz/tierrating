"use client"

import { useDroppable } from "@dnd-kit/core"
import {rectSortingStrategy, SortableContext, verticalListSortingStrategy} from "@dnd-kit/sortable"
import SortableItem from "./sortable-item"
import { cn } from "@/lib/utils"
import {RatingItem} from "@/components/tier-list";

interface TierContainerProps {
    id: string
    label: string
    items: RatingItem[]
    activeId: string | null
}

const tierColors: Record<string, string> = {
    s: "bg-red-100 border-red-500",
    a: "bg-orange-100 border-orange-500",
    b: "bg-yellow-100 border-yellow-500",
    c: "bg-green-100 border-green-500",
    d: "bg-blue-100 border-blue-500",
    f: "bg-purple-100 border-purple-500",
    unassigned: "bg-gray-100 border-gray-500",
}

export default function TierContainer({ id, label, items, activeId }: TierContainerProps) {
    const { setNodeRef, isOver } = useDroppable({
        id,
    })

    return (
        <div
            ref={setNodeRef}
            className={cn("flex border-2 rounded-md overflow-hidden", tierColors[id], isOver && "ring-2 ring-primary")}
        >
            <div className="flex items-center justify-center w-16 font-bold text-xl p-2">{label}</div>
            <div className="flex-1 min-h-[120px] p-2 flex flex-wrap gap-2 bg-white/50">
                <SortableContext items={items.map((item) => item.id)} strategy={rectSortingStrategy}>
                    {items.map((item) => (
                        <SortableItem
                            key={item.id}
                            id={item.id}
                            title={item.title}
                            cover={item.cover}
                            activeId={item.id === activeId} />
                    ))}
                </SortableContext>
                {items.length === 0 && (
                    <div className="w-full h-full min-h-[80px] flex items-center justify-center text-gray-400 text-sm">
                        Drop items here
                    </div>
                )}
            </div>
        </div>
    )
}
