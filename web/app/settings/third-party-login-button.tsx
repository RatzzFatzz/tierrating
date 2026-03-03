import Link from "next/link";
import {Button} from "@/components/ui/button";
import Image from "next/image";
import React from "react";

export default function ThirdPartyLoginButton({index, title, path, color, service}: {
    index: number,
    title: string,
    path: string,
    color: string,
    service: string
}) {
    return (
        <Link key={index} href={path} className="block w-full">
            <Button
                variant="outline"
                className={`cursor-pointer w-full rounded-full`}
            >
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
            </Button>
        </Link>
    )
}