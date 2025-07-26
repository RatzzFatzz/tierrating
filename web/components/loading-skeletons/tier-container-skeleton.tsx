import {Skeleton} from "@/components/ui/skeleton";

export default function TierContainerSkeleton() {
    return Array(5).fill(0).map((_, index) => (
        <div key={`skeleton-${index}`} className="grid grid-cols-[auto_1fr] rounded-md overflow-hidden border border-border/60 h-[160px]" >
            <Skeleton className={"w-16 rounded-l-md rounded-r-none"} />
            <Skeleton className={"w-full rounded-r-md rounded-l-none"} />
        </div>
    ))
}