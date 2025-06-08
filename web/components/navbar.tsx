"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import { useAuth } from "@/contexts/AuthContext"
import {
    NavigationMenu,
    NavigationMenuItem,
    NavigationMenuLink,
    NavigationMenuList,
    navigationMenuTriggerStyle,
} from "@/components/ui/navigation-menu"
import { Button } from "@/components/ui/button"
import { LogOut } from "lucide-react"

export default function NavBar() {
    const { logout, isAuthenticated, user } = useAuth()
    const pathname = usePathname()

    if (pathname === "/login" || pathname === "/signup" || !isAuthenticated) {
        return null
    }

    return (
        <div className="border-b border-border/40 bg-background/95 backdrop-blur supports-backdrop-blur:bg-background/60">
            <div className="grid grid-cols-3 h-16 items-center px-4 container mx-auto">
                <div className="justify-self-start">
                    <Link href="/dashboard" className="text-xl font-bold text-primary">
                        MyApp
                    </Link>
                </div>

                <div className="justify-self-center">
                    <NavigationMenu>
                        <NavigationMenuList>
                            <NavigationMenuItem>
                                <NavigationMenuLink asChild className={navigationMenuTriggerStyle()}>
                                    <Link href="/dashboard">Home</Link>
                                </NavigationMenuLink>
                            </NavigationMenuItem>

                            <NavigationMenuItem>
                                <NavigationMenuLink asChild className={navigationMenuTriggerStyle()}>
                                    <Link href="/search" >Search</Link>
                                </NavigationMenuLink>
                            </NavigationMenuItem>

                            <NavigationMenuItem>
                                <NavigationMenuLink asChild className={navigationMenuTriggerStyle()}>
                                    <Link href={`/user/${user}`} >Profile</Link>
                                </NavigationMenuLink>
                            </NavigationMenuItem>
                        </NavigationMenuList>
                    </NavigationMenu>
                </div>

                <div className="justify-self-end">
                    <Button
                        variant="ghost"
                        size="sm"
                        onClick={logout}
                        className="text-muted-foreground hover:text-foreground"
                    >
                        <LogOut className="h-4 w-4 mr-2" />
                        Logout
                    </Button>
                </div>
            </div>
        </div>
    )
}