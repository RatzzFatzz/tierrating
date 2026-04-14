export interface ThirdPartyServiceConfig {
	id: string;
	name: string;
	scoreDecimal: string;
	types: ThirdPartyServiceTypeConfig[];
}

interface ThirdPartyServiceTypeConfig {
	id: string;
	name: string;
}

export type ThirdPartyService = "ANILIST" | "TRAKT" | "STEAM";

export const THIRD_PARTY_SERVICE_CONFIG: Record<ThirdPartyService, ThirdPartyServiceConfig> = {
	ANILIST: {
		id: "anilist",
		name: "AniList",
		scoreDecimal: "0.01",
		types: [
			{ id: "anime", name: "Anime" },
			{ id: "manga", name: "Manga" },
		],
	},
	TRAKT: {
		id: "trakt",
		name: "Trakt",
		scoreDecimal: "1.00",
		types: [
			{ id: "movies", name: "Movies" },
			{ id: "tvshows", name: "TV Shows" },
			{ id: "tvshows-seasons", name: "TV Shows - Seasons" },
		],
	},
	STEAM: {
		id: "steam",
		name: "Steam",
		scoreDecimal: "0.01",
		types: [
			{ id: "games", name: "Games" },
		],
	},
} as const;

export function getServiceConfig(key: string): ThirdPartyServiceConfig | undefined {
	const upperKey = key.toUpperCase() as ThirdPartyService;
	return THIRD_PARTY_SERVICE_CONFIG[upperKey] ?? undefined;
}