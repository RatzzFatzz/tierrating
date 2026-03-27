import TierListPage from "@/app/user/[username]/[service]/[type]/_components/tier-list-page";
import { getServiceConfig } from "@/lib/config/third-party-services-config";
import { notFound } from "next/navigation";

export default async function ServiceTypeTierlist({ params }: { params: Promise<{ username: string; service: string; type: string }> }) {
	const { username, service, type } = await params;

	const thirdPartyService = getServiceConfig(service);
	const thirdPartyContentType = thirdPartyService?.types.find((t) => t.id === type);
	if (!thirdPartyService || !thirdPartyContentType) notFound();
	const title = `${thirdPartyService.service.name} ${thirdPartyContentType.name} Tierlist`;

	return <TierListPage title={title} username={username} service={service} type={type} />;
}
