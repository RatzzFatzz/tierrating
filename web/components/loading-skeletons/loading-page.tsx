import React from "react";
import { cn } from "@/lib/utils";

export function LoadingPage() {
	return (
		<div className="flex items-center justify-center min-h-[90vh]">
			<div className="animate-pulse text-muted-foreground">Loading...</div>
		</div>
	);
}

export function LoadingDiv({ className }: { className?: string }) {
	return (
		<div className={cn("flex items-center justify-center", className)}>
			<div className="animate-pulse text-muted-foreground">Loading...</div>
		</div>
	);
}
