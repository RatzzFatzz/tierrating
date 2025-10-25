import {TierlistEntry} from "@/model/types";
import {cn} from "@/lib/utils";
import Image from "next/image";
import {useSortable} from "@dnd-kit/react/sortable";

export default function TierlistEntryDraggable({entry, column}: {entry: TierlistEntry, column: string}) {
    const {ref, isDragging} = useSortable({
        id: entry.id,
        index: entry.index,
        group: column
    });

    return (
        <li ref={ref} data-dragging={isDragging}>
            <TierlistEntryCard entry={entry}/>
        </li>
    )
}

function TierlistEntryCard({entry}: {entry: TierlistEntry}) {
    return (
        <div
            className={cn(
                "w-20 h-36 bg-card border border-border/40 rounded-md shadow-sm cursor-move flex flex-col items-center justify-center transition-opacity",
                // isDragging ? "opacity-30" : isOver ? "opacity-70" : "opacity-100",
            )}
        >
            <div className="relative w-full h-full">
                <Image
                    src={entry.cover || "/placeholder.svg"}
                    alt={entry.title}
                    fill
                    className="object-cover rounded-t-md"
                    sizes="80px"
                />
            </div>
            <div className="text-xs line-clamp-2 h-11 w-full text-center p-1 text-foreground bg-card">{entry.title}</div>
        </div>
    )
}