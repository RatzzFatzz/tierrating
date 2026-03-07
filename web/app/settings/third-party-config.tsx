"use client"

import {useAuth} from "@/components/contexts/auth-context";
import {useEffect, useState} from "react";

import {fetchUser, removeConnection} from "@/components/api/user-api";
import {fetchConfiguredServices} from "@/components/api/config-api";
import LoadingPage from "@/components/loading-skeletons/loading-page";
import ThirdPartyLoginButton from "@/app/settings/third-party-login-button";
import {UserResponse} from "@/components/model/response-types";
import ThirdPartyConnection from "@/app/settings/third-party-connection";
import {router} from "next/client";

export default function ThirdPartyConfig() {
	const {user, token, logout, isLoading, isAuthenticated} = useAuth();

	const [isRemovingService, setIsRemovingService] = useState(false);
	const [userResponse, setUserResponse] = useState<UserResponse>();
	const [configuredServices, setConfiguredServices] = useState<string[]>();

	const removeService = (service: string) => {
		setIsRemovingService(true);
		removeConnection(service, user, token)
			.then(response => {
				if (response.status === 401 || response.status === 403) {
					logout()
					throw new Error("Session expired or unauthorized");
				}
				if (response.status != 200) throw new Error(response.data ? response.data.message : `API error: ${response.status}`)
				console.debug(`${service} connection removed`);
				router.reload();
			})
			.catch(error => {
				console.error(error.message);
			})
			.finally(() => {
				setIsRemovingService(false);
			})
	}

	useEffect(() => {
		if (!isLoading && isAuthenticated && user) {
			fetchUser(token, user)
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
	}, [isLoading, isAuthenticated, user, token, logout]);

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

	if (!userResponse || !configuredServices || !user) return <LoadingPage/>

	return (
		<div className={"w-full grid gap-4"}>
			{
				userResponse.connectedServices.length != configuredServices.length
				&& <div className="grid columns-1 gap-2">
					{
						configuredServices.includes('anilist')
						&& !userResponse.connectedServices.includes('ANILIST')
						&& <ThirdPartyLoginButton title={"Connect AniList"} path={"/auth/anilist"} service="anilist"/>
					}
					{
						configuredServices.includes('trakt')
						&& !userResponse.connectedServices.includes('TRAKT')
						&& <ThirdPartyLoginButton title={"Connect Trakt"} path={"/auth/trakt"} service="trakt"/>
					}
                </div>
			}
			{
				userResponse.connectedServices.length > 0
				&& <div className="grid columns-1 gap-2">
					{
						userResponse.connectedServices.includes('ANILIST')
						&& <ThirdPartyConnection service={{id: "anilist", title: "Anilist"}}
                                                 types={[{id: "anime", title: "Anime"}, {id: "manga", title: "Manga"}]}
                                                 removeConnection={removeService} isRemovingService={isRemovingService}
                                                 username={user} token={token} logout={logout}/>
					}
					{
						userResponse.connectedServices.includes('TRAKT')
						&& <ThirdPartyConnection service={{id: "trakt", title: "Trakt"}}
                                                 types={[{id: "movies", title: "Movies"}, {
													 id: "tvshows",
													 title: "TV Shows"
												 }, {id: "tvshows-seasons", title: "TV Shows - Seasons"}]}
                                                 removeConnection={removeService} isRemovingService={isRemovingService}
                                                 username={user} token={token} logout={logout}/>
					}
                </div>
			}
		</div>
	)
}