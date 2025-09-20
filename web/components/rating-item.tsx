"use client"
import type { DragItem, TierlistEntry } from "@/model/types"
import Image from "next/image"
import { memo, useRef } from "react"
import { useDrag, useDrop } from "react-dnd"
import { cn } from "@/lib/utils"

interface RatingItemProps {
    item: TierlistEntry
    index: number
    moveItem: (dragIndex: number, hoverIndex: number, sourceTier: string, targetTier: string) => void
}

export const RatingItemComponent = memo(function RatingItemComponent({ item, index, moveItem }: RatingItemProps) {
    const ref = useRef<HTMLDivElement>(null)
    const [{ isDragging }, drag] = useDrag({
        type: "RATING_ITEM",
        item: { id: item.id, index, tier: item.tier, type: "RATING_ITEM" } as DragItem,
        collect: (monitor) => ({
            isDragging: monitor.isDragging(),
        }),
    })
    // @ts-expect-error TODO
    const [{ isOver }, drop] = useDrop({
        accept: "RATING_ITEM",
        collect: (monitor) => ({
            isOver: monitor.isOver(),
        }),
        // @ts-expect-error TODO
        hover(dragItem: DragItem, monitor) {
            if (!ref.current) {
                return
            }
            const dragIndex = dragItem.index
            const hoverIndex = index
            const sourceTier = dragItem.tier
            const targetTier = item.tier
            // Don't replace items with themselves
            if (dragIndex === hoverIndex && sourceTier === targetTier) {
                return
            }
            // Determine rectangle on screen
            const hoverBoundingRect = ref.current.getBoundingClientRect()
            // Get horizontal middle
            const hoverMiddleX = (hoverBoundingRect.right - hoverBoundingRect.left) / 2
            // Determine mouse position
            const clientOffset = monitor.getClientOffset()
            if (!clientOffset) return
            // Get pixels to the left
            const hoverClientX = clientOffset.x - hoverBoundingRect.left
            // Only perform the move when the mouse has crossed half of the items width
            // When dragging to the right, only move when the cursor is after 50%
            // When dragging to the left, only move when the cursor is before 50%
            // Dragging right
            if (dragIndex < hoverIndex && hoverClientX < hoverMiddleX) {
                return
            }
            // Dragging left
            if (dragIndex > hoverIndex && hoverClientX > hoverMiddleX) {
                return
            }
            // Time to actually perform the action
            moveItem(dragIndex, hoverIndex, sourceTier, targetTier)
            // Note: we're mutating the monitor item here!
            // Generally it's better to avoid mutations,
            // but it's good here for the sake of performance
            // to avoid expensive index searches.
            dragItem.index = hoverIndex
            dragItem.tier = targetTier
        },
    })
    drag(drop(ref))
    return (
        <div
            ref={ref}
            className={cn(
                "w-20 h-36 bg-card border border-border/40 rounded-md shadow-sm cursor-move flex flex-col items-center justify-center transition-opacity",
                isDragging ? "opacity-30" : isOver ? "opacity-70" : "opacity-100",
            )}
        >
            <div className="relative w-full h-full">
                <Image
                    src={item.cover || "/placeholder.svg"}
                    alt={item.title}
                    fill
                    className="object-cover rounded-t-md"
                    sizes="80px"
                />
            </div>
            <div className="text-xs line-clamp-2 h-11 w-full text-center p-1 text-foreground bg-card">{item.title}</div>
        </div>
    )
})