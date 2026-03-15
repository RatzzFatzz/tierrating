import { SteamDataProvider } from "@/components/data-providers/steam/steam-data-provider";

export class SteamGamesDataProvider extends SteamDataProvider {
	getTypeName(): string {
		return "games";
	}
}
