import Link from "next/link"
import {cn} from "@/lib/utils"
import ThemeToggle from "@/components/themes/theme-toggle";
import LogoutButton from "@/components/auth/logout-button";
import NavbarLinks from "@/components/navbar/navbar-links";
import {RestrictedRenderingRoute} from "@/contexts/route-accessibility";
import SettingsButton from "@/components/navbar/settings-button";

export default function NavBar() {

    return (
        <RestrictedRenderingRoute>
            <div className="fixed top-0 left-0 right-0 z-50 flex justify-center px-4 py-4 pointer-events-none">
                <nav className={cn(
                    "flex h-14 w-full max-w-[1600px] flex-row items-center px-4 lg:px-6 rounded-2xl",
                    "bg-card/60 backdrop-blur-sm border border-border/100 shadow-lg",
                    "transition-all duration-200 ease-in-out pointer-events-auto"
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
                                <path d="M2 20h20"/>
                                <path d="M5 14h14"/>
                                <path d="M8 8h8"/>
                                <path d="M12 4h1"/>
                            </svg>
                            <span className="text-xl font-medium">TierRating</span>
                        </div>
                    </Link>

                    <NavbarLinks/>

                    <div className="flex flex-1 items-center justify-end gap-2">
                        {/*<SearchBar/>*/}

                        <ThemeToggle/>
                        <SettingsButton/>
                        <LogoutButton/>
                    </div>
                </nav>
            </div>
        </RestrictedRenderingRoute>
    )
}