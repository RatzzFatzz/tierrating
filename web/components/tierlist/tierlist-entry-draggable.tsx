import {TierlistEntry} from "@/model/types";
import {cn} from "@/lib/utils";
import Image from "next/image";
import {useDraggable} from "@dnd-kit/react";
import {HoverCard, HoverCardContent, HoverCardTrigger} from "@/components/ui/hover-card";

export function TierlistEntryDraggable({entry}: {entry: TierlistEntry}) {
    const {ref} = useDraggable({
        id: entry.id,
        data: entry,
    });

    return (
        <div ref={ref}>
            <TierlistEntryCard entry={entry}/>
        </div>
    )
}

export function TierlistEntryCard({entry}: {entry: TierlistEntry}) {
    return (
        <div
            className={cn(
                "w-20 h-36 flex flex-col items-center justify-center",
                "bg-card border border-border/40 rounded-md shadow-sm cursor-move",
            )}
        >
            <div className="relative w-full h-full">
                <Image
                    src={entry.cover}
                    alt={entry.title}
                    fill
                    unoptimized={true}
                    className="object-cover rounded-t-md"
                    sizes="80px"
                />
            </div>
            <HoverCard>
                <HoverCardTrigger asChild>
                    <div className="text-xs line-clamp-2 h-11 w-full text-center p-1 text-foreground bg-card">
                        {entry.title}
                    </div>
                </HoverCardTrigger>
                <HoverCardContent className="w-64 p-2">
                    {entry.title}
                </HoverCardContent>
            </HoverCard>
        </div>
    )
}