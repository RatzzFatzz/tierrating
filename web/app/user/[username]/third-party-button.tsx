import {Button} from "@/components/ui/button";
import AniListTierListConfigModal from "@/components/tiers/anilist-tier-list-config-modal";
import {Tier} from "@/model/types";
import React from "react";
import Image from "next/image";
import {updateTiers} from "@/components/api/tier-api";
import Link from "next/link";

export default function ThirdPartyButton({service, type, title, username, configAllowed, token, logout}: {service: string, type: string, title: string, username: string, configAllowed: boolean, token: string | null, logout: () => void}) {
    const saveTiers = (type: string, tiers: Tier[]) => {
        updateTiers(token, username, service, type, tiers).then(response => {
            if (response.status === 401 || response.status === 403) {
                logout();
                throw new Error("Session expired");
            }

            if (response.status === 404) throw new Error("User not found or user doesn't have requested service connected");
            if (response.error) throw new Error(`API error: ${response.status}`);
        });
    }

    return (
        <div>
            <Link href={`/user/${username}/${service}/${type}`}>
                <Button variant="outline" className="w-full flex items-center justify-between gap-2">
                    <div className="relative w-5 h-5 flex-shrink-0">
                        <Image
                            src={`/icons/${service}.svg`}
                            alt={`${service} icon`}
                            fill={true}
                        />
                    </div>
                    <div className="flex-grow text-center">
                        {title}
                    </div>
                    {configAllowed &&
                        <div className="relative w-5 h-5 flex-shrink-0" onClick={(e) => e.stopPropagation()}>
                            <AniListTierListConfigModal
                                type={type}
                                onSave={(tiers: Tier[]) => saveTiers(type, tiers)}
                                providerName={`${service}-${type}`}
                                username={username}
                            />
                        </div>
                    }
                </Button>
            </Link>
        </div>
    )
}