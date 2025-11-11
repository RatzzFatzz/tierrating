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
import ThirdPartyLoginButton from "@/app/user/[username]/third-party-login-button";
import ThirdPartyButton from "@/app/user/[username]/third-party-button";


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
            <div className="flex min-h-screen -mt-24 items-center justify-center px-4">
                <Card className={cn(
                    "w-full max-w-md",
                    "flex flex-col rounded-2xl",
                    "bg-card/60 backdrop-blur-sm border border-border/100 shadow-lg",
                )}>
                    <CardHeader className="flex flex-row items-center text-center pb-6">
                        <Avatar className="h-24 w-24 border-2 border-border/50 shadow-md">
                            {/*<AvatarImage src={"/avatar.svg"} alt={username}/>*/}
                            <AvatarFallback>{username.charAt(0)}</AvatarFallback>
                        </Avatar>
                        <div className="p-3">
                            <h1 className="text-2xl font-bold">{userResponse["username"]}</h1>
                            {/*<p className="text-muted-foreground mt-1">{userResponse["bio"]}</p>*/}
                        </div>
                    </CardHeader>

                    <CardContent className="space-y-4">
                        {/* Provider Connection Section */}
                        {isConfigAllowed && !userResponse.anilistConnected
                            && <ThirdPartyLoginButton index={0} title={"Connect AniList"} path={"/auth/anilist"}
                                                      color={"bg-blue-600 hover:bg-blue-700"} service="anilist"/>}
                        {isConfigAllowed && !userResponse.traktConnected
                            && <ThirdPartyLoginButton index={1} title={"Connect Trakt"} path={"/auth/trakt"}
                                                      color={"bg-red-600 hover:bg-red-700"} service="trakt"/>}

                        <div className="grid columns-1 gap-4">
                            {userResponse.anilistConnected && <ThirdPartyButton service={"anilist"} type={"anime"} title={"Anime"} username={userResponse.username} configAllowed={isConfigAllowed} token={token} logout={logout}/>}
                            {userResponse.anilistConnected && <ThirdPartyButton service={"anilist"} type={"manga"} title={"Manga"} username={userResponse.username} configAllowed={isConfigAllowed} token={token} logout={logout}/>}
                            {userResponse.traktConnected && <ThirdPartyButton service={"trakt"} type={"movies"} title={"Movies"} username={userResponse.username} configAllowed={isConfigAllowed} token={token} logout={logout}/>}
                            {userResponse.traktConnected && <ThirdPartyButton service={"trakt"} type={"tvshows"} title={"TV Shows"} username={userResponse.username} configAllowed={isConfigAllowed} token={token} logout={logout}/>}
                            {userResponse.traktConnected && <ThirdPartyButton service={"trakt"} type={"tvshows-seasons"} title={"TV Shows - Seasons"} username={userResponse.username} configAllowed={isConfigAllowed} token={token} logout={logout}/>}
                        </div>
                    </CardContent>
                </Card>
            </div>
        </ProtectedRoute>
    )
}