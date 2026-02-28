import {useDroppable} from "@dnd-kit/react";
import React from "react";
import {cn} from "@/lib/utils";

export default function TierContainerDroppable({children, id, label, color, disabled}: {children: React.ReactNode, id: string, label: string, color: string, disabled: boolean}) {
    const {ref, isDropTarget} = useDroppable({
        id: id,
        disabled: disabled
    });

    return (
        <div
            ref={ref}
            className={cn("grid grid-cols-[auto_1fr] mb-1",
                            "overflow-hidden border border-border/60 rounded-2xl bg-card/60 backdrop-blur-sm",
                            isDropTarget ? `ring-2 ring-accent` : "")}
            style={{ minHeight: "80px" }}
        >
            <div
                className={"flex items-center justify-center w-16 text-shadow-white font-bold text-xl line-clamp-1 truncate"}
                style={{ backgroundColor: `${color}`}}
            >
                {label}
            </div>
            <div className={"min-h-16 bg-background/50 p-2 flex flex-wrap gap-2"}>
                {children}
            </div>
        </div>
    )
}