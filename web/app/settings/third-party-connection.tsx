"use client"

import {Card, CardContent, CardHeader} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
import {X} from "lucide-react";

import Image from "next/image";
import ThirdPartyConnectedButton from "@/app/settings/third-party-connected-button";

export default function ThirdPartyConnection({service, types, removeConnection, isRemovingService, username, token, logout}: {
    service: { id: string, title: string },
    types: { id: string, title: string }[],
    removeConnection: (service: string) => void,
    isRemovingService: boolean,
    username: string,
    token: string | null,
    logout: () => void
}) {

    return (
        <Card className={"gap-1"}>
            <CardHeader className={"h-9"}>
                <div className={"w-full flex gap-2 items-center"}>
                    <div className="relative size-7">
                        <Image
                            src={`/icons/${service.id}.svg`}
                            alt={`${service.id} icon`}
                            fill={true}
                        />
                    </div>
                    <div className={"w-full font-bold content-center"}>{service.title}</div>
                    <Button
                        type={"submit"}
                        variant={"ghost"}
                        onClick={() => removeConnection(service.id)}
                        disabled={isRemovingService}
                    >
                        <X></X>
                    </Button>
                </div>
            </CardHeader>
            <CardContent className={"grid gap-1"}>
                {
                    types.map(entry => (
                        <ThirdPartyConnectedButton service={service.id} type={entry.id} title={entry.title} username={username} token={token} logout={logout}/>
                    ))
                }
            </CardContent>
        </Card>
    )
}