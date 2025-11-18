"use client"

import {Button} from "@/components/ui/button";
import {cn} from "@/lib/utils";
import Link from "next/link";
import {useAuth} from "@/components/contexts/auth-context";
import {usePathname} from "next/navigation";

export default function NavbarLinks() {
    const {user} = useAuth()
    const pathname = usePathname()

    return (
        <div className="flex items-center space-x-1 ml-6">
            {/*<Button*/}
            {/*    variant="ghost"*/}
            {/*    asChild*/}
            {/*    className={cn(*/}
            {/*        "px-3 text-sm font-medium",*/}
            {/*        pathname === "/dashboard" ? "text-foreground" : "text-muted-foreground"*/}
            {/*    )}*/}
            {/*>*/}
            {/*    <Link href="/dashboard">Home</Link>*/}
            {/*</Button>*/}

            <Button
                variant="ghost"
                asChild
                className={cn(
                    "px-3 text-sm font-medium",
                    pathname.startsWith("/user/") ? "text-foreground" : "text-muted-foreground"
                )}
            >
                <Link href={`/user/${user}`}>Profile</Link>
            </Button>
        </div>
    );
}