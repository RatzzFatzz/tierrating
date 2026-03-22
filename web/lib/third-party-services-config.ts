function defineService(id: string, name: string, connectColor: string, types: { id: string; name: string }[]) {
	return {
		connectTitle: `Connect ${name}`,
		authPath: `/auth/${id}`,
		connectColor,
		service: { id, name },
		types,
	};
}

export const THIRD_PARTY_SERVICE_CONFIG = {
	ANILIST: defineService("anilist", "AniList", "bg-blue-600 hover:bg-blue-700", [
		{ id: "anime", name: "Anime" },
		{ id: "manga", name: "Manga" },
	]),
	TRAKT: defineService("trakt", "Trakt", "bg-red-600 hover:bg-red-700", [
		{ id: "movies", name: "Movies" },
		{ id: "tvshows", name: "TV Shows" },
		{ id: "tvshows-seasons", name: "TV Shows - Seasons" },
	]),
	STEAM: defineService("steam", "Steam", "bg-red-600 hover:bg-red-700", [
		{ id: "games", name: "Games" }
	]),
} as const;

export function getServiceConfig(key: string) {
	if (key in THIRD_PARTY_SERVICE_CONFIG) {
		return THIRD_PARTY_SERVICE_CONFIG[key as keyof typeof THIRD_PARTY_SERVICE_CONFIG];
	}
	return undefined;
}