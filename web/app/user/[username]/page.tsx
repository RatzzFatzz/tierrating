"use client"
import Link from "next/link"
import {Button} from "@/components/ui/button"
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import {Separator} from "@/components/ui/separator"
import React, {useEffect, useState} from "react";
import ProtectedRoute from "@/components/ProtectedRoute";
import {useAuth} from "@/contexts/AuthContext";
import {fetchUser} from "@/components/api/user-api";
import {useParams} from "next/navigation";
import LoadingPage from "@/components/loading-page";
import {cn} from "@/lib/utils"
import {Card, CardContent, CardHeader} from "@/components/ui/card";
import AniListTierListConfigModal from "@/components/tiers/anilist-tier-list-config-modal";
import {updateTiers} from "@/components/api/tier-api";
import {Tier} from "@/model/types";

function ProviderLoginButton({index, title, path, color}: {
    index: number,
    title: string,
    path: string,
    color: string
}) {
    return (
        <Link key={index} href={path} className="block w-full">
            <Button
                variant="default"
                className={`w-full justify-center rounded-full transition-all duration-200 text-white font-medium ${color}`}
            >
                {title}
            </Button>
        </Link>
    )
}

function ProviderGroupButton({index, configAllowed, groupTitle, groupEntries, token, logout}: {
    index: number,
    configAllowed: boolean,
    groupTitle: string,
    groupEntries: { title: string, path: string, color: string }[],
    token: string | null,
    logout: () => void;
}) {
    return (
        <div key={index} className={cn(
            "w-full max-w-md rounded-2xl p-5",
            "bg-inherit backdrop-blur-md border border-border/100 shadow-lg",
        )}>
            <div className="mb-3">
                <h2 className="text-lg font-semibold">{groupTitle}</h2>
                <Separator className="mt-1"/>
            </div>
            <div className="space-y-3 mt-4">
                {groupEntries.map((entry, index) => (
                    <div key={index} className="flex gap-0.75">
                        <Link href={entry.path} className="block w-full">
                            <Button
                                variant="default"
                                className={cn(
                                    `w-full justify-center rounded-l-full transition-all duration-200 ${entry.color} text-white font-medium`,
                                    configAllowed ? "" : "rounded-r-full"
                                )}
                            >
                                {entry.title}
                            </Button>
                        </Link>
                        {configAllowed && <div className={`rounded-r-full transition-all duration-200 ${entry.color} text-white font-medium`}>
                            <AniListTierListConfigModal onSave={(tiers: Tier[]) => updateTiers(token, 'anilist', 'anime', tiers, () => logout())}/>
                        </div>}
                    </div>
                ))}
            </div>
        </div>
    )
}

export default function Profile() {
    const params = useParams<{ username: string }>();
    const username: string = params.username;
    const [userConnections, setUserConnections] = useState(null);
    const {user, token, isLoading, isAuthenticated, logout} = useAuth();
    const isConfigAllowed: boolean = username === user;

    useEffect(() => {
        if (!isLoading && isAuthenticated) {
            fetchUser(token, username, logout)
                .then(data => setUserConnections(data))
                .catch((error) => console.error(error))
        }
    }, [username, isLoading, isAuthenticated, token, logout]);

    // Render nothing until connections are loaded
    if (!userConnections || isLoading) return <LoadingPage/>

    return (
        <ProtectedRoute>
            <div className="flex min-h-screen -mt-24 items-center justify-center">
                <Card className={cn(
                    "w-full max-w-md rounded-2xl p-4 pt-6 pb-6 z-50",
                    "bg-background/80 backdrop-blur-md border border-border/100 shadow-lg",
                    "transition-all duration-200 ease-in-out"
                )}>
                    <CardHeader className="flex flex-col items-center text-center space-y-4 pt-4 pb-6">
                        <Avatar className="h-24 w-24 border-2 border-border/50 shadow-md">
                            <AvatarImage src={"/avatar.svg"} alt={username}/>
                            <AvatarFallback>{username.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <div>
                            <h1 className="text-2xl font-bold">{username}</h1>
                            <p className="text-muted-foreground mt-1">{userConnections["bio"]}</p>
                        </div>
                    </CardHeader>

                    <CardContent className="space-y-6 pb-4">
                        {/* Provider Connection Section */}
                        {isConfigAllowed && !userConnections['aniListConnected']
                            && <ProviderLoginButton index={0} title={"Connect AniList"} path={"/auth/anilist"}
                                                    color={"bg-blue-600 hover:bg-blue-700"}/>}
                        {isConfigAllowed && !userConnections['traktConnected']
                            && <ProviderLoginButton index={1} title={"Connect Trakt"} path={"/auth/trakt"}
                                                    color={"bg-red-600 hover:bg-red-700"}/>}

                        {/* Group Section */}
                        {userConnections['aniListConnected']
                            && <ProviderGroupButton index={0} configAllowed={isConfigAllowed} groupTitle={"AniList"}
                                                    groupEntries={[
                                                        {
                                                            title: "Anime",
                                                            path: `/user/${username}/anilist/anime`,
                                                            color: "bg-blue-600 hover:bg-blue-700"
                                                        },
                                                        {
                                                            title: "Manga",
                                                            path: `/user/${username}/anilist/manga`,
                                                            color: "bg-blue-500 hover:bg-blue-600"
                                                        },
                                                    ]} logout={logout} token={token}/>}
                        {userConnections['traktConnected']
                            && <ProviderGroupButton index={1} configAllowed={isConfigAllowed} groupTitle={"Trakt"}
                                                    groupEntries={[
                                                        {
                                                            title: "Series",
                                                            path: `/user/${username}/trakt/series`,
                                                            color: "bg-red-600 hover:bg-red-700"
                                                        },
                                                        {
                                                            title: "Movies",
                                                            path: `/user/${username}/trakt/movies`,
                                                            color: "bg-red-500 hover:bg-red-600"
                                                        },
                                                    ]} logout={logout} token={token}/>}
                    </CardContent>
                </Card>
            </div>
        </ProtectedRoute>
    )
}