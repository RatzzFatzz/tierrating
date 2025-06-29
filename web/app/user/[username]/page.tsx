"use client"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Separator } from "@/components/ui/separator"
import React, { useState, useEffect } from "react";
import ProtectedRoute from "@/components/ProtectedRoute";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import {useAuth} from "@/contexts/AuthContext";
import {API_URL} from "@/components/global-config";

export default function Profile({params}) {
    const username: string = React.use(params).username.toString();
    const [userConnections, setUserConnections] = useState(null);
    const {token, isLoading, isAuthenticated, user} = useAuth();

    // Predefined profile structure
    const profile = {
        name: username,
        avatar: "/placeholder.svg?height=100&width=100",
        bio: "Customizable placeholder text",
        links: [
            {
                title: "Connect AniList",
                path: "/auth/anilist",
                color: "bg-blue-600 hover:bg-blue-700",
                connectedKey: 'aniListConnected'
            },
            {
                title: "Connect Trakt",
                path: "/auth/trakt",
                color: "bg-red-600 hover:bg-red-700",
                connectedKey: 'traktConnected'
            },
        ],
        groups: [
            {
                title: "AniList",
                connectedKey: 'aniListConnected',
                links: [
                    {
                        title: "Anime",
                        path: `/user/${username}/anilist/anime`,
                        color: "bg-blue-600 hover:bg-blue-700",
                    },
                    {
                        title: "Manga",
                        path: `/user/${username}/anilist/manga`,
                        color: "bg-blue-500 hover:bg-blue-600",
                    },
                ],
            },
            {
                title: "Trakt",
                connectedKey: 'traktConnected',
                links: [
                    {
                        title: "Series",
                        path: `/user/${username}/trakt/series`,
                        color: "bg-red-600 hover:bg-red-700",
                    },
                    {
                        title: "Movies",
                        path: `/user/${username}/trakt/movies`,
                        color: "bg-red-500 hover:bg-red-600",
                    },
                ],
            },
        ],
    }

    useEffect(() => {
        if (!isLoading && isAuthenticated) {
            fetch(`${API_URL}/user/${username}`, {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            }).then(res => res.json())
            .then(data => setUserConnections(data))
            .catch((error) => console.error(error))
        }

    }, [username, isLoading, isAuthenticated, token]);

    // Helper function to determine if a section should be rendered
    const shouldRenderSection = (section, connections) => {
        if (!connections || user != username) return false;
        return connections[section.connectedKey] === false;
    }

    const shouldRenderGroup = (group, connections) => {
        if (!connections) return false;
        return connections[group.connectedKey] === true;
    }

    // Render nothing until connections are loaded
    if (!userConnections || isLoading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="animate-pulse text-muted-foreground">Loading...</div>
            </div>
        )
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
                        {profile.links
                            .filter(link => shouldRenderSection(link, userConnections))
                            .map((link, index) => (
                                <Link key={index} href={link.path} className="block w-full">
                                    <Button
                                        variant="default"
                                        className={`w-full justify-center ${link.color} text-white font-bold`}
                                    >
                                        {link.title}
                                    </Button>
                                </Link>
                            ))
                        }

                        {/* Grouped Links Section */}
                        {profile.groups
                            .filter(group => shouldRenderGroup(group, userConnections))
                            .map((group, groupIndex) => (
                                <div key={groupIndex}>
                                    <div className="mb-3">
                                        <h2 className="text-lg font-semibold">{group.title}</h2>
                                        <Separator className="mt-1" />
                                    </div>
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
                            ))
                        }
                    </CardContent>
                </Card>
            </div>
        </ProtectedRoute>
    )
}