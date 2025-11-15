import {Card, CardHeader} from "@/components/ui/card";
import {cn} from "@/lib/utils";
import {ProtectedRoute} from "@/components/contexts/route-accessibility";

export default function Dashboard() {
    return (
        <ProtectedRoute>
            <div className={"flex justify-center items-center"}>
                <Card className={cn(
                    "w-full max-w-md rounded-2xl p-4 pt-6 pb-6 z-50",
                    "bg-background/80 backdrop-blur-md border border-border/100 shadow-lg",
                    "transition-all duration-200 ease-in-out"
                )}>
                    <CardHeader>
                        <h2>This is a dashboard. Believe it or not</h2>
                    </CardHeader>
                </Card>
            </div>
        </ProtectedRoute>
    )
}