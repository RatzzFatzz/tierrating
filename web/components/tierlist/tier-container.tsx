import {useDroppable} from "@dnd-kit/react";
import React from "react";
import {cn} from "@/lib/utils";

export default function TierContainerDroppable({children, id, label, color}: {children: React.ReactNode, id: string, label: string, color: string}) {
    const {ref, isDropTarget} = useDroppable({
        id
    });

    return (
        <div
            ref={ref}
            // class={isDropTarget ? `ring[${color}` : ""}
            className={cn("grid grid-cols-[auto_1fr] rounded-md overflow-hidden border border-border/60 mb-1",
                            isDropTarget ? `ring-2 ring-accent` : "")}
            style={{ minHeight: "80px" }}
        >
            <div
                className={"flex items-center justify-center w-16 self-stretch text-white font-bold text-xl"}
                style={{ backgroundColor: `${color}`}}
            >
                {label}
            </div>
            <div className={"flex-1 min-h-16 bg-background/50 p-2 flex flex-wrap gap-2 transition-colors"}>
                {children}
            </div>
        </div>
    )
}