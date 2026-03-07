"use client";

import TierList from "@/components/tierlist/tier-list";
import TmdbDisclaimer from "@/components/disclaimer/tmdb-disclaimer";
import { useState } from "react";
import { Toggle } from "@/components/ui/toggle";
import { ArrowLeftFromLine, ArrowRightFromLine } from "lucide-react";
import { cn } from "@/lib/utils";
import { ButtonGroup } from "@/components/ui/button-group";
import { Button } from "@/components/ui/button";
import { getProviderByName } from "@/components/data-providers/data-provider";
import { useAuth } from "@/components/contexts/auth-context";
import { useParams } from "next/navigation";

export default function TierListPage({ title, provider }: { title: string; provider: string }) {
	const [isFullWidth, setIsFullWidth] = useState<boolean>(false);
	const { token, user, logout } = useAuth();
	const username: string = useParams<{ username: string }>().username;
	const modificationAllowed: boolean = user == username;

	return (
		<div
			className={cn(
				"max-w-full mx-auto content-center px-4",
				"transition-all duration-400 ease-in-out",
				isFullWidth ? "w-full" : "w-[1514px] "
			)}
		>
			<div className={"grid grid-cols-2"}>
				<h1 className="text-3xl font-bold mb-6">{title}</h1>
				<div className={"w-full flex justify-end items-end pb-2"}>
					<ButtonGroup>
						{modificationAllowed && (
							<Button variant={"ghost"} onClick={() => getProviderByName(provider).pullData(token, username, logout)}>
								Pull
							</Button>
						)}
						<Toggle aria-label={"Toggle full width"} onPressedChange={() => setIsFullWidth(!isFullWidth)}>
							<ArrowLeftFromLine />
							Full Width
							<ArrowRightFromLine />
						</Toggle>
					</ButtonGroup>
				</div>
			</div>
			<TierList providerName={provider} />
			{provider.startsWith("trakt") && <TmdbDisclaimer />}
		</div>
	);
}
