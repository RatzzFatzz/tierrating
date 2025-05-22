"use client"

import { useSortable } from "@dnd-kit/sortable"
import { CSS } from "@dnd-kit/utilities"
import Item from "./item"

interface SortableItemProps {
    id: string
    title: string
    cover: string
    isActive: boolean
}

export default function SortableItem({ id, title, cover, isActive }: SortableItemProps) {
    const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id, disabled: false})

    const style = {
        transform: CSS.Transform.toString(transform),
        transition,
        opacity: isDragging ? 0.4: 1,
        position: "relative" as const,
        zIndex: isDragging ? 999 : 1,
    }

    return (
        <div ref={setNodeRef} style={style} {...attributes} {...listeners} className="transform-none">
            <Item id={id} title={title} cover={cover} />
            {isActive && (
                <div className="absolute inset-0 pointer-events-none opacity-30">
                    <Item id={id} title={title} cover={cover} />
                </div>
            )}
        </div>
    )
}
