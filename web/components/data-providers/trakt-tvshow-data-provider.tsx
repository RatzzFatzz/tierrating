import {TraktDataProvider} from "@/components/data-providers/trakt-data-provider";

export class TraktTvshowDataProvider extends TraktDataProvider {
    getTypeName(): string {
        return "tvshows";
    }

}