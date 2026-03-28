"use client";

import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/auth-context";
import { useAccountDeletion } from "@/lib/services/user-service";
import { toast } from "sonner";

export default function AccountModification() {
	const { user, token, logout } = useAuth();
	const { trigger: deleteAccount, error, isMutating } = useAccountDeletion(token!);

	const submitDeletion = () => {
		deleteAccount({ username: user! })
			.then(() => {
				toast.success("Account successfully deleted.");
				logout();
			})
			.catch((error) => {
				toast.error(`Error occurred deleting account: ${error.message}`);
			});
	};

	return (
		<div className={"w-full"}>
			<Popover>
				<PopoverTrigger asChild>
					<Button className={"w-full"} variant={"destructive"}>
						Delete Account
					</Button>
				</PopoverTrigger>
				<PopoverContent>
					<div className={"gap-4 w-full"}>
						<Button className={"w-full"} variant={"destructive"} type={"submit"} disabled={isMutating} onClick={submitDeletion}>
							Are you sure?
						</Button>
					</div>
				</PopoverContent>
			</Popover>
		</div>
	);
}
