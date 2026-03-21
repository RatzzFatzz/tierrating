"use client";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import React, { useEffect } from "react";
import { useAuth } from "@/components/contexts/auth-context";
import { useParams } from "next/navigation";
import LoadingPage from "@/components/loading-skeletons/loading-page";
import { cn } from "@/lib/utils";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { UserResponse } from "@/components/model/response-types";
import TierlistLink from "@/app/user/[username]/tierlist-link";
import { apiClient } from "@/lib/api-client";
import { useQuery } from "@/hooks/useQuery";

export default function Profile() {
	const params = useParams<{ username: string }>();
	const username: string = params.username;

	const { token, logout } = useAuth();
	const { data, error, isRunning, isSuccess, isError } = useQuery(
		() => apiClient.get<UserResponse>(token, `/user/${username}`),
		[token, username]
	);

	useEffect(() => {
		if (error?.status === 401 || error?.status === 403) {
			logout();
		}
	}, [error, logout]);

	if (isRunning) return <LoadingPage />;

	if (!isSuccess) return null;
	const userData = data!;

	return (
		<div className="flex min-h-screen -mt-24 items-center justify-center px-4">
			<Card
				className={cn(
					"w-full max-w-md",
					"flex flex-col rounded-2xl",
					"bg-card/60 backdrop-blur-sm border border-border/100 shadow-lg"
				)}
			>
				<CardHeader className="flex flex-row items-center text-center pb-6">
					<Avatar className="h-24 w-24 border-2 border-border/50 shadow-md">
						{/*<AvatarImage src={"/avatar.svg"} alt={username}/>*/}
						<AvatarFallback>{username.charAt(0)}</AvatarFallback>
					</Avatar>
					<div className="p-3">
						<h1 className="text-2xl font-bold">{userData["username"]}</h1>
						{/*<p className="text-muted-foreground mt-1">{userResponse["bio"]}</p>*/}
					</div>
				</CardHeader>

				<CardContent className="space-y-4">
					<div className="grid columns-1 gap-4">
						{userData.connectedServices.includes("ANILIST") && (
							<TierlistLink service={"anilist"} type={"anime"} title={"Anime"} username={userData.username} />
						)}
						{userData.connectedServices.includes("ANILIST") && (
							<TierlistLink service={"anilist"} type={"manga"} title={"Manga"} username={userData.username} />
						)}
						{userData.connectedServices.includes("TRAKT") && (
							<TierlistLink service={"trakt"} type={"movies"} title={"Movies"} username={userData.username} />
						)}
						{userData.connectedServices.includes("TRAKT") && (
							<TierlistLink service={"trakt"} type={"tvshows"} title={"TV Shows"} username={userData.username} />
						)}
						{userData.connectedServices.includes("TRAKT") && (
							<TierlistLink
								service={"trakt"}
								type={"tvshows-seasons"}
								title={"TV Shows - Seasons"}
								username={userData.username}
							/>
						)}
						{userData.connectedServices.includes("STEAM") && (
							<TierlistLink service={"steam"} type={"games"} title={"Games"} username={userData.username} />
						)}
					</div>
				</CardContent>
			</Card>
		</div>
	);
}
