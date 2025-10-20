import {useDroppable} from "@dnd-kit/react";
import React from "react";

export default function TierContainerDndKit({children, id, label, color}: {children: React.ReactNode, id: string, label: string, color: string}) {
    return (
        <div className="grid grid-cols-[auto_1fr] rounded-md overflow-hidden border border-border/60">
            <div style={{ backgroundColor: `${color}`}} className={"flex items-center justify-center w-16 self-stretch text-white font-bold text-xl"}>
                {label}
            </div>
            <TierContainerDroppable id={id}>
                {children}
            </TierContainerDroppable>
        </div>
    )
}

function TierContainerDroppable({id, children}: {id: string, children: React.ReactNode}) {
    const {ref} = useDroppable({
        id
    });

    return (
        <div
            ref={ref}
            className={`flex-1 min-h-16 bg-background/50 p-2 flex flex-wrap gap-2 transition-colors`}
            style={{ minHeight: "80px" }}
        >
            {children}
        </div>
    )
}