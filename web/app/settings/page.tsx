import ChangePassword from "@/app/settings/change-password";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import AccountModification from "@/app/settings/account-modification";
import ThirdPartyConfig from "@/app/settings/third-party-config";

export default function Settings() {
	return (
		<div className={"flex flex-wrap items-start justify-center gap-4 w-full min-w-4/5 pr-4 pl-4"}>
			<Card className={"max-w-120 w-full"}>
				<CardHeader>Connect Third-Party Service</CardHeader>
				<CardContent>
					<ThirdPartyConfig />
				</CardContent>
			</Card>
			<Card className={"max-w-120 w-full"}>
				<CardHeader>Change Password</CardHeader>
				<CardContent>
					<ChangePassword />
				</CardContent>
			</Card>
			<Card className={"max-w-120 w-full"}>
				<CardHeader>Account Deletion</CardHeader>
				<CardHeader>
					<AccountModification />
				</CardHeader>
			</Card>
		</div>
	);
}
