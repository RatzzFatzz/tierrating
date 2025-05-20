import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Separator } from "@/components/ui/separator"

export default function LinkTree() {
    // This would typically come from a database or CMS
    const profile = {
        name: "RatzzFatzz",
        avatar: "/placeholder.svg?height=100&width=100",
        bio: "Customizable placeholder text",
        // Ungrouped links (displayed at the top)
        links: [
            // {
            //     title: "Dashboard",
            //     path: "/dashboard",
            //     color: "bg-pink-500 hover:bg-pink-600",
            // },
        ],
        // Grouped links
        groups: [
            {
                title: "AniList",
                links: [
                    {
                        title: "Anime",
                        path: "/profile/anilist/anime",
                        color: "bg-blue-600 hover:bg-blue-700",
                    },
                    {
                        title: "Manga",
                        path: "/profile/anilist/manga",
                        color: "bg-blue-500 hover:bg-blue-600",
                    },
                ],
            },

        ],
    }

    return (
        <main className="flex min-h-screen flex-col items-center justify-center bg-gradient-to-b from-slate-50 to-slate-100 p-4">
            <div className="w-full max-w-md space-y-8 py-10">
                {/* Profile Section */}
                <div className="flex flex-col items-center justify-center space-y-4 text-center">
                    <Avatar className="h-24 w-24 border-2 border-white shadow-md">
                        <AvatarImage src={profile.avatar || "/placeholder.svg"} alt={profile.name} />
                        <AvatarFallback>{profile.name.charAt(0)}</AvatarFallback>
                    </Avatar>
                    <div>
                        <h1 className="text-2xl font-bold">{profile.name}</h1>
                        <p className="text-muted-foreground">{profile.bio}</p>
                    </div>
                </div>

                {/* Ungrouped Links Section */}
                {profile.links.length > 0 && (
                    <div className="space-y-4 pt-4">
                        {profile.links.map((link, index) => (
                            <Link key={index} href={link.path} className="block w-full">
                                <Button
                                    variant="default"
                                    className={`w-full justify-center ${link.color} text-white transition-all duration-300`}
                                >
                                    {link.title}
                                </Button>
                            </Link>
                        ))}
                    </div>
                )}

                {/* Grouped Links Section */}
                {profile.groups.map((group, groupIndex) => (
                    <div key={groupIndex} className="pt-6">
                        {/* Group Title */}
                        <div className="mb-3">
                            <h2 className="text-lg font-semibold">{group.title}</h2>
                            <Separator className="mt-1" />
                        </div>

                        {/* Group Links */}
                        <div className="space-y-3">
                            {group.links.map((link, linkIndex) => (
                                <Link key={linkIndex} href={link.path} className="block w-full">
                                    <Button
                                        variant="default"
                                        className={`w-full justify-center ${link.color} text-white transition-all duration-300`}
                                    >
                                        {link.title}
                                    </Button>
                                </Link>
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        </main>
    )
}
