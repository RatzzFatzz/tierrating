import {TraktDataProvider} from "@/components/data-providers/trakt/trakt-data-provider";

export class TraktMoviesDataProvider extends TraktDataProvider {
    getTypeName(): string {
        return "movies";
    }
}