"use client"
import Link from "next/link"
import { usePathname } from "next/navigation"
import { useAuth } from "@/contexts/AuthContext"
import { Button } from "@/components/ui/button"
import { LogOut, Search, Sun, Moon } from "lucide-react"
import { useTheme } from "next-themes"
import { cn } from "@/lib/utils"
import { useEffect, useState } from "react"

export default function NavBar() {
    const { logout, isAuthenticated, user } = useAuth()
    const pathname = usePathname()
    const { theme, setTheme } = useTheme()
    const [scrolled, setScrolled] = useState(false)

    // Add scroll event listener to detect when page is scrolled
    useEffect(() => {
        const handleScroll = () => {
            setScrolled(window.scrollY > 10)
        }

        window.addEventListener("scroll", handleScroll)
        return () => window.removeEventListener("scroll", handleScroll)
    }, [])

    if (pathname === "/login" || pathname === "/signup" || !isAuthenticated) {
        return null
    }

    return (
        <div className="fixed top-0 left-0 right-0 z-50 flex justify-center px-4 py-4 pointer-events-none">
            <nav className={cn(
                "flex h-14 w-full max-w-[90%] lg:max-w-[1600px] flex-row items-center px-4 lg:px-6 rounded-2xl",
                "bg-background/80 backdrop-blur-md border border-border/30 shadow-lg",
                "transition-all duration-200 ease-in-out pointer-events-auto",
                scrolled ? "shadow-md" : ""
            )}>
                <Link href="/dashboard" className="inline-flex items-center gap-2 font-semibold">
                    <div className="flex items-center gap-1.5">
                        <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="24"
                            height="24"
                            viewBox="0 0 24 24"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            className="text-primary"
                        >
                            <path d="M2 20h20" />
                            <path d="M5 14h14" />
                            <path d="M8 8h8" />
                            <path d="M12 4h1" />
                        </svg>
                        <span className="text-xl font-medium">TierRating</span>
                    </div>
                </Link>

                <div className="flex items-center space-x-1 ml-6 max-sm:hidden">
                    <Button
                        variant="ghost"
                        asChild
                        className={cn(
                            "px-3 text-sm font-medium",
                            pathname === "/dashboard" ? "text-foreground" : "text-muted-foreground"
                        )}
                    >
                        <Link href="/dashboard">Home</Link>
                    </Button>

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

                <div className="flex flex-1 items-center justify-end gap-2">
                    <Button
                        variant="ghost"
                        size="icon"
                        className="lg:hidden"
                        aria-label="Search"
                    >
                        <Search className="h-5 w-5" />
                    </Button>

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

                    <Button
                        variant="outline"
                        size="icon"
                        className="rounded-full border h-9 w-9 p-0 max-lg:hidden"
                        onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
                    >
                        <Sun className="h-[1.2rem] w-[1.2rem] rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
                        <Moon className="absolute h-[1.2rem] w-[1.2rem] rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
                        <span className="sr-only">Toggle theme</span>
                    </Button>

                    <Button
                        variant="ghost"
                        size="icon"
                        onClick={logout}
                        className="text-muted-foreground hover:text-foreground"
                        aria-label="Logout"
                    >
                        <LogOut className="h-5 w-5" />
                    </Button>
                </div>
            </nav>
        </div>
    )
}