import {TraktDataProvider} from "@/components/data-providers/trakt-data-provider";

export class TraktMovieDataProvider extends TraktDataProvider {
    getTypeName(): string {
        return "movies";
    }
}