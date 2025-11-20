import {Button} from "@/components/ui/button";
import TierConfigModal from "@/components/tiers/tier-config-modal";
import {Tier} from "@/components/model/types";
import React, {useState} from "react";
import Image from "next/image";
import {updateTiers} from "@/components/api/tier-api";
import Link from "next/link";
import {X} from "lucide-react";
import {removeConnection} from "@/components/api/user-api";
import {useRouter} from "next/navigation";
import {ButtonGroup} from "@/components/ui/button-group";

function getDecimals(service: string): string {
    if (service === "anilist") return "0.01";
    if (service === "trakt") return "1.00";
    return "1.00";
}

export default function ThirdPartyButton({service, type, title, username, configAllowed, token, logout}: {service: string, type: string, title: string, username: string, configAllowed: boolean, token: string | null, logout: () => void}) {
    const [isRemovingService, setIsRemovingService] = useState(false);
    const router = useRouter();

    const saveTiers = (type: string, tiers: Tier[]) => {
        updateTiers(token, username, service, type, tiers)
            .then(response => {
                if (response.status === 401 || response.status === 403) {
                    logout();
                    throw new Error("Session expired");
                }

                if (response.status === 404) throw new Error("User not found or user doesn't have requested service connected");
                if (response.error) throw new Error(`API error: ${response.status}`);
        });
    }

    const removeService = () => {
        setIsRemovingService(true);
        removeConnection(service, username, token)
            .then(response => {
                if (response.error) throw new Error(response.error);
                if (!response.data) throw new Error("Faulty response");
                if (!response.data.success) throw new Error(response.data.message);
                router.refresh();
                console.debug(`${service} connection removed`);
            })
            .catch(error => {
                console.error(error.message);
            })
            .finally(() => {
                setIsRemovingService(false);
            })
    }

    return (
        <ButtonGroup className={"w-full flex"}>
            <Button variant="outline" className={"grow"}>
                <Link href={`/user/${username}/${service}/${type}`} className="w-full flex justify-center">
                    <div className="relative w-5 h-5 mr-auto">
                        <Image
                            src={`/icons/${service}.svg`}
                            alt={`${service} icon`}
                            fill={true}
                        />
                    </div>
                    <div className="text-center absolute">
                        {title}
                    </div>
                </Link>
            </Button>

            {configAllowed &&
                <TierConfigModal
                    service={service}
                    type={type}
                    onSave={(tiers: Tier[]) => saveTiers(type, tiers)}
                    username={username}
                    decimals={getDecimals(service)}
                />
            }

            {configAllowed &&
                <Button
                    type={"submit"}
                    variant={"outline"}
                    // className={"w-full h-full"}
                    onClick={removeService}
                    disabled={isRemovingService}
                >
                    <X></X>
                </Button>
            }
        </ButtonGroup>
    )
}