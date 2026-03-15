import { AbstractDataProvider } from "@/components/data-providers/abstract-data-provider";

export abstract class SteamDataProvider extends AbstractDataProvider {
	getServiceName(): string {
		return "steam";
	}
}
