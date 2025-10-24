"use client"
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar"
import React, {useEffect, useState} from "react";
import {ProtectedRoute} from "@/contexts/route-accessibility";
import {useAuth} from "@/contexts/auth-context";
import {fetchUser} from "@/components/api/user-api";
import {useParams} from "next/navigation";
import LoadingPage from "@/components/loading-page";
import {cn} from "@/lib/utils"
import {Card, CardContent, CardHeader} from "@/components/ui/card";
import {UserResponse} from "@/model/response-types";
import ThirdPartyLoginButton from "@/app/user/[username]/ThirdPartyLoginButton";
import ThirdPartyGroupBox from "@/app/user/[username]/ThirdPartyGroupBox";


export default function Profile() {
    const params = useParams<{ username: string }>();
    const username: string = params.username;
    const [userResponse, setUserResponse] = useState<UserResponse>();
    const {user, token, isLoading, isAuthenticated, logout} = useAuth();
    const isConfigAllowed: boolean = username === user;

    useEffect(() => {
        if (!isLoading && isAuthenticated) {
            fetchUser(token, username)
                .then(response => {
                    if (response.status === 401 || response.status === 403) {
                        logout();
                        throw new Error("Session expired");
                    }

                    if (response.error) throw new Error(`API error: ${response.status}`);
                    if (!response.data) throw new Error("Faulty response");
                    console.log(JSON.stringify(response.data));
                    setUserResponse(response.data);
                })
                .catch((error) => console.error(error))
        }
    }, [username, isLoading, isAuthenticated, token, logout]);

    // Render nothing until connections are loaded
    if (!userResponse) return <LoadingPage/>

    return (
        <ProtectedRoute>
            <div className="flex min-h-screen -mt-24 items-center justify-center">
                <Card className={cn(
                    "w-full max-w-md",
                    "bg-card/60 backdrop-blur-sm border border-border/100 shadow-lg",
                )}>
                    <CardHeader className="flex flex-col items-center text-center space-y-4 pt-4 pb-6">
                        <Avatar className="h-24 w-24 border-2 border-border/50 shadow-md">
                            <AvatarImage src={"/avatar.svg"} alt={username}/>
                            <AvatarFallback>{username.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <div>
                            <h1 className="text-2xl font-bold">{username}</h1>
                            <p className="text-muted-foreground mt-1">{userResponse["bio"]}</p>
                        </div>
                    </CardHeader>

                    <CardContent className="space-y-6 pb-4">
                        {/* Provider Connection Section */}
                        {isConfigAllowed && !userResponse.anilistConnected
                            && <ThirdPartyLoginButton index={0} title={"Connect AniList"} path={"/auth/anilist"}
                                                      color={"bg-blue-600 hover:bg-blue-700"}/>}
                        {isConfigAllowed && !userResponse.traktConnected
                            && <ThirdPartyLoginButton index={1} title={"Connect Trakt"} path={"/auth/trakt"}
                                                      color={"bg-red-600 hover:bg-red-700"}/>}

                        {/* Group Section */}
                        {userResponse.anilistConnected
                            && <ThirdPartyGroupBox index={0} configAllowed={isConfigAllowed} groupTitle={"AniList"}
                                                   groupEntries={[
                                                        {
                                                            title: "Anime",
                                                            path: `/user/${userResponse.username}/anilist/anime`,
                                                            color: "bg-blue-600 hover:bg-blue-700"
                                                        },
                                                        {
                                                            title: "Manga",
                                                            path: `/user/${userResponse.username}/anilist/manga`,
                                                            color: "bg-blue-500 hover:bg-blue-600"
                                                        },
                                                    ]} logout={logout} token={token} username={userResponse.username}/>}
                        {userResponse.traktConnected
                            && <ThirdPartyGroupBox index={1} configAllowed={isConfigAllowed} groupTitle={"Trakt"}
                                                   groupEntries={[
                                                        {
                                                            title: "Series",
                                                            path: `/user/${userResponse.username}/trakt/series`,
                                                            color: "bg-red-600 hover:bg-red-700"
                                                        },
                                                        {
                                                            title: "Movies",
                                                            path: `/user/${userResponse.username}/trakt/movies`,
                                                            color: "bg-red-500 hover:bg-red-600"
                                                        },
                                                    ]} logout={logout} token={token} username={userResponse.username}/>}
                    </CardContent>
                </Card>
            </div>
        </ProtectedRoute>
    )
}