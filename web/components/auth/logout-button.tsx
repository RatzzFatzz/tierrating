"use client"

import {Button} from "@/components/ui/button";
import {LogOut} from "lucide-react";
import {useAuth} from "@/components/contexts/auth-context";

export default function LogoutButton() {
    const {logout} = useAuth()

    return (
        <Button
            variant="ghost"
            size="icon"
            onClick={logout}
            className="text-muted-foreground hover:text-foreground"
            aria-label="Logout"
        >
            <LogOut className="h-5 w-5"/>
        </Button>
    );
}