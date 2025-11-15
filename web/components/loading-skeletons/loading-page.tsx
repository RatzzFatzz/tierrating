import React from "react";

export default function LoadingPage() {
    return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="animate-pulse text-muted-foreground">Loading...</div>
        </div>
    )
}