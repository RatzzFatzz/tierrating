import {Skeleton} from "@/components/ui/skeleton";
import {cn} from "@/lib/utils";

export function TierContainerSkeleton() {
    return Array(6).fill(0).map((_, index) => (
        <div key={`skeleton-${index}`} className="grid grid-cols-[auto_1fr] rounded-md overflow-hidden border border-border/60 h-[160px] mb-1" >
            <Skeleton className={"w-16 rounded-l-md rounded-r-none"} />
            <Skeleton className={"w-full rounded-r-md rounded-l-none"} />
        </div>
    ));
}

export function TierlistEntrySkeleton({color, label}: {color: string, label: string}) {
    return (
        <div className={cn("grid grid-cols-[auto_1fr] mb-1",
            "overflow-hidden border border-border/60 rounded-2xl bg-card/60 backdrop-blur-sm",)}
        >
            <div style={{ backgroundColor: `${color}`}} className={"flex items-center justify-center w-16 self-stretch text-white font-bold text-xl"}>
                {label}
            </div>
            <div className="flex-1 min-h-16 bg-background/50 p-2 flex flex-wrap gap-2 transition-colors">
                {Array(Math.floor(Math.random() * (7) + 2)).fill(0).map((_, index) => (
                    <div key={`skeleton-tierlist-entry-${index}`}>
                        <Skeleton className="w-20 h-36 bg-card border border-border/40 rounded-md shadow-sm cursor-move flex flex-col items-center justify-center transition-opacity"></Skeleton>
                    </div>
                ))}
            </div>
        </div>
    );
}