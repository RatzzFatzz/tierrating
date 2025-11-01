import {AbstractDataProvider} from "@/components/data-providers/abstract-data-provider";

export abstract class TraktDataProvider extends AbstractDataProvider {
    getServiceName(): string {
        return "trakt";
    }
}