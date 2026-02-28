import {Button} from "@/components/ui/button";
import {Search} from "lucide-react";

export default function SearchBar() {
    return (
        <div className="relative max-w-[240px] w-full max-lg:hidden">
            <Button
                variant="outline"
                onClick={() => window.location.href = "/search"}
                className="flex w-full items-center justify-between rounded-full border bg-background/50 px-3 py-1.5 text-sm text-muted-foreground shadow-none hover:bg-accent"
            >
                <div className="flex items-center gap-2">
                    <Search className="h-4 w-4" />
                    <span>Search</span>
                </div>
                <div className="flex items-center gap-1">
                    <kbd className="rounded border bg-muted px-1.5 text-[10px] font-medium">⌘</kbd>
                    <kbd className="rounded border bg-muted px-1.5 text-[10px] font-medium">K</kbd>
                </div>
            </Button>
        </div>
    );
}