import {TraktDataProvider} from "@/components/data-providers/trakt/trakt-data-provider";

export class TraktTvShowsSeasonsDataProvider extends TraktDataProvider {
    getTypeName(): string {
        return "tvshows-seasons";
    }
}