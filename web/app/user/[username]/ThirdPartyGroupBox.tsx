import {Tier} from "@/model/types";
import {updateTiers} from "@/components/api/tier-api";
import {cn} from "@/lib/utils";
import {Separator} from "@/components/ui/separator";
import Link from "next/link";
import {Button} from "@/components/ui/button";
import AniListTierListConfigModal from "@/components/tiers/anilist-tier-list-config-modal";
import React from "react";

export default function ThirdPartyGroupBox({index, configAllowed, groupTitle, groupEntries, token, username, logout}: {
    index: number,
    configAllowed: boolean,
    groupTitle: string,
    groupEntries: { title: string, path: string, color: string }[],
    token: string | null,
    username: string,
    logout: () => void;
}) {
    const saveTiers = (type: string, tiers: Tier[]) => {
        updateTiers(token, username, groupTitle, type, tiers).then(response => {
            if (response.status === 401 || response.status === 403) {
                logout();
                throw new Error("Session expired");
            }

            if (response.status === 404) throw new Error("User not found or user doesn't have requested service connected");
            if (response.error) throw new Error(`API error: ${response.status}`);


            // return response;
        });
    }

    return (
        <div key={index} className={cn(
            "w-full max-w-md rounded-2xl p-5",
            "bg-inherit backdrop-blur-inherit border border-border/100 shadow-lg",
        )}>
            <div className="mb-3">
                <h2 className="text-lg font-semibold">{groupTitle}</h2>
                <Separator className="mt-1"/>
            </div>
            <div className="space-y-3 mt-4">
                {groupEntries.map((entry, index) => (
                    <div key={index} className="flex gap-0.75">
                        <Link href={entry.path} className="block w-full">
                            <Button
                                variant="default"
                                className={cn(
                                    `cursor-pointer w-full justify-center rounded-l-full transition-all duration-200 ${entry.color} text-white font-medium`,
                                    configAllowed ? "" : "rounded-r-full"
                                )}
                            >
                                {entry.title}
                            </Button>
                        </Link>
                        {configAllowed && <div
                            className={`rounded-r-full transition-all duration-200 ${entry.color} text-white font-medium`}>
                            <AniListTierListConfigModal type={entry.title}
                                                        onSave={(tiers: Tier[]) => saveTiers(entry.title, tiers)}
                                                        providerName={`${groupTitle}-${entry.title}`}
                                                        username={username}/>
                        </div>}
                    </div>
                ))}
            </div>
        </div>
    )
}