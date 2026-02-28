import {Skeleton} from "@/components/ui/skeleton";

export function TierConfigTableSkeleton() {
    return Array(5).fill(0).map((_, index) => (
        <div
            key={`skeleton-${index}`}
            className="grid grid-cols-[60px_1fr_100px_120px_40px] gap-4 items-center"
        >
            {Array(4).fill(0).map((_, index2) => (
                // eslint-disable-next-line react/jsx-key
                <div key={`skeleton-${index}-${index2}`}>
                    <Skeleton className="h-9 w-full"/>
                </div>
            ))}
            <div>
                <Skeleton className="h-9 w-9"/>
            </div>
        </div>
    ));
}