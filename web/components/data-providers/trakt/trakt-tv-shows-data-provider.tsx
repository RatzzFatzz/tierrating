import {TraktDataProvider} from "@/components/data-providers/trakt/trakt-data-provider";

export class TraktTvShowsDataProvider extends TraktDataProvider {
    getTypeName(): string {
        return "tvshows";
    }
}