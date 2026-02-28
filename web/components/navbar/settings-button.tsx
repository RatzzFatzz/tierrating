import {Button} from "@/components/ui/button";
import Link from "next/link";
import {Settings} from "lucide-react";

export default function SettingsButton() {
    return (
        <Button
            variant="ghost"
            size="icon"
            className="text-muted-foreground hover:text-foreground"
            aria-label="Settings"
        >
            <Link href={"/settings"}>
                <Settings className="h-5 w-5"/>
            </Link>
        </Button>
    );
}