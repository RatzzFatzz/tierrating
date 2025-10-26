import Link from "next/link";
import {Button} from "@/components/ui/button";
import React from "react";

export default function ThirdPartyLoginButton({index, title, path, color}: {
    index: number,
    title: string,
    path: string,
    color: string
}) {
    return (
        <Link key={index} href={path} className="block w-full">
            <Button
                variant="default"
                className={`cursor-pointer w-full justify-center rounded-full transition-all duration-200 text-white font-medium ${color}`}
            >
                {title}
            </Button>
        </Link>
    )
}