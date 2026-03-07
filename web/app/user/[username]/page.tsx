"use client"
import {Avatar, AvatarFallback} from "@/components/ui/avatar"
import React, {useEffect, useState} from "react";
import {ProtectedRoute} from "@/components/contexts/route-accessibility";
import {useAuth} from "@/components/contexts/auth-context";
import {fetchUser} from "@/components/api/user-api";
import {useParams} from "next/navigation";
import LoadingPage from "@/components/loading-skeletons/loading-page";
import {cn} from "@/lib/utils"
import {Card, CardContent, CardHeader} from "@/components/ui/card";
import {UserResponse} from "@/components/model/response-types";
import {fetchConfiguredServices} from "@/components/api/config-api";
import TierlistLink from "@/app/user/[username]/tierlist-link";


export default function Profile() {
	const params = useParams<{ username: string }>();
	const username: string = params.username;
	const [userResponse, setUserResponse] = useState<UserResponse>();
	const {token, isLoading, isAuthenticated, logout} = useAuth();

	const [configuredServices, setConfiguredServices] = useState<string[]>();

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
					setUserResponse(response.data);
				})
				.catch((error) => console.error(error))
		}
	}, [username, isLoading, isAuthenticated, token, logout]);

	useEffect(() => {
		if (!isLoading && isAuthenticated) {
			fetchConfiguredServices(token)
				.then(response => {
					if (response.status === 401 || response.status === 403) {
						logout();
						throw new Error("Session expired");
					}

					if (response.error) throw new Error(`API error: ${response.status}`);
					if (!response.data) throw new Error("Faulty response");
					setConfiguredServices(response.data);
				})
				.catch((error) => console.error(error))
		}
	}, [isAuthenticated, isLoading, logout, token])

	if (!userResponse || !configuredServices) return <LoadingPage/>

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
						<div className="grid columns-1 gap-4">
							{userResponse.connectedServices.includes('ANILIST') &&
                                <TierlistLink service={"anilist"} type={"anime"} title={"Anime"}
                                              username={userResponse.username}/>}
							{userResponse.connectedServices.includes('ANILIST') &&
                                <TierlistLink service={"anilist"} type={"manga"} title={"Manga"}
                                              username={userResponse.username}/>}
							{userResponse.connectedServices.includes('TRAKT') &&
                                <TierlistLink service={"trakt"} type={"movies"} title={"Movies"}
                                              username={userResponse.username}/>}
							{userResponse.connectedServices.includes('TRAKT') &&
                                <TierlistLink service={"trakt"} type={"tvshows"} title={"TV Shows"}
                                              username={userResponse.username}/>}
							{userResponse.connectedServices.includes('TRAKT') &&
                                <TierlistLink service={"trakt"} type={"tvshows-seasons"} title={"TV Shows - Seasons"}
                                              username={userResponse.username}/>}
						</div>
					</CardContent>
				</Card>
			</div>
		</ProtectedRoute>
	)
}