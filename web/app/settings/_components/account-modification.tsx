"use client";

import { Button } from "@/components/ui/button";
import { useAuth } from "@/contexts/auth-context";
import { useAccountDeletion } from "@/lib/services/user-service";
import { toast } from "sonner";
import {
	Dialog,
	DialogClose,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
	DialogTrigger,
} from "@/components/ui/dialog";

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
			<Dialog>
				<DialogTrigger asChild>
					<Button className={"w-full"} variant={"destructive"}>
						Delete Account
					</Button>
				</DialogTrigger>
				<DialogContent>
					<DialogHeader>
						<DialogTitle>Are you absolutely sure?</DialogTitle>
						<DialogDescription>
							This action cannot be undone. This will permanently delete your account and remove all your data.
						</DialogDescription>
					</DialogHeader>
					<DialogFooter>
						<DialogClose asChild>
							<Button variant="outline">Cancel</Button>
						</DialogClose>
						<Button type="submit" variant={"destructive"} disabled={isMutating} onClick={submitDeletion}>
							Delete permanently
						</Button>
					</DialogFooter>
				</DialogContent>
			</Dialog>
		</div>
	);
}
