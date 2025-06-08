"use client"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Separator } from "@/components/ui/separator"
import React from "react";
import ProtectedRoute from "@/components/ProtectedRoute";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

export default function Profile({params}) {
    // This would typically come from a database or CMS
    const username = React.use(params).username.toString();
    const profile = {
        name: username,
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
                        path: "/user/"+ username + "/anilist/anime",
                        color: "bg-blue-600 hover:bg-blue-700",
                    },
                    {
                        title: "Manga",
                        path: "/user/"+ username + "/anilist/manga",
                        color: "bg-blue-500 hover:bg-blue-600",
                    },
                ],
            },
            {
                title: "Trakt",
                links: [
                    {
                        title: "Series",
                        path: "/user/"+ username + "/trakt/series",
                        color: "bg-red-600 hover:bg-red-700",
                    },
                    {
                        title: "Movies",
                        path: "/user/"+ username + "/trakt/movies",
                        color: "bg-red-500 hover:bg-red-600",
                    },
                ],
            },
        ],
    }
    return (
        <ProtectedRoute>
            <div className="flex min-h-screen items-center justify-center bg-background px-4">
                <Card className="w-full max-w-md border-border/40 bg-card/60 backdrop-blur-sm">
                    <CardHeader className="flex flex-col items-center text-center space-y-4">
                        <Avatar className="h-24 w-24 border-2 border-border shadow-md">
                            <AvatarImage src={profile.avatar || "/placeholder.svg"} alt={profile.name} />
                            <AvatarFallback>{profile.name.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <div>
                            <CardTitle className="text-2xl font-bold">{profile.name}</CardTitle>
                            <p className="text-muted-foreground mt-1 ">{profile.bio}</p>
                        </div>
                    </CardHeader>
                    <CardContent className="space-y-6">
                        {/* Ungrouped Links Section */}
                        {profile.links.length > 0 && (
                            <div className="space-y-3">
                                {profile.links.map((link, index) => (
                                    <Link key={index} href={link.path} className="block w-full">
                                        <Button
                                            variant="default"
                                            className={`w-full justify-center ${link.color} text-white font-bold`}
                                        >
                                            {link.title}
                                        </Button>
                                    </Link>
                                ))}
                            </div>
                        )}
                        {/* Grouped Links Section */}
                        {profile.groups.map((group, groupIndex) => (
                            <div key={groupIndex}>
                                {/* Group Title */}
                                <div className="mb-3">
                                    <h2 className="text-lg font-semibold">{group.title}</h2>
                                    <Separator className="mt-1" />
                                </div>
                                {/* Group Links */}
                                <div className="space-y-3 mt-4">
                                    {group.links.map((link, linkIndex) => (
                                        <Link key={linkIndex} href={link.path} className="block w-full">
                                            <Button
                                                variant="default"
                                                className={`w-full justify-center ${link.color} text-white font-bold`}
                                            >
                                                {link.title}
                                            </Button>
                                        </Link>
                                    ))}
                                </div>
                            </div>
                        ))}
                    </CardContent>
                </Card>
            </div>
        </ProtectedRoute>
    )
}