import {AbstractDataProvider} from "@/components/data-providers/abstract-data-provider";

export abstract class AnilistDataProvider extends AbstractDataProvider {

    getServiceName(): string {
        return "anilist";
    }

    abstract getTypeName(): string;

}