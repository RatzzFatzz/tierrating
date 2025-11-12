"use client"

import {Popover, PopoverContent, PopoverTrigger} from "@/components/ui/popover";
import {Button} from "@/components/ui/button";
import {useAuth} from "@/contexts/auth-context";
import {deleteAccount} from "@/components/api/user-api";
import {useState} from "react";

export default function AccountModification() {
    const {user, token, logout} = useAuth();
    const [errorMessage, setErrorMessage] = useState("");
    const [isDeleting, setIsDeleting] = useState(false);

    const submitDeletion = () => {
        setIsDeleting(true);
        deleteAccount(user, token)
            .then(response => {
                if (response.error) throw new Error(response.error);
                if (!response.data) throw new Error("Faulty response");
                if (!response.data.success) throw new Error(response.data.message);
                setTimeout(() => logout(), 1000);
                setErrorMessage("");
                console.info("account deleted")
            })
            .catch(error => {
                setErrorMessage(error.message);
            })
            .finally(() => {
                setIsDeleting(false);
            })
    }

    return (
        <div className={"w-full"}>
            <Popover>
                <PopoverTrigger asChild>
                    <Button
                        className={"w-full"}
                        variant={"destructive"}
                    >
                        Delete Account
                    </Button>
                </PopoverTrigger>
                <PopoverContent>
                    <div className={"gap-4 w-full"}>
                        <Button
                            className={"w-full"}
                            variant={"destructive"}
                            type={"submit"}
                            disabled={isDeleting}
                            onClick={submitDeletion}
                        >
                            Are you  sure?
                        </Button>
                        {errorMessage && (
                            <div className="bg-destructive/15 text-destructive text-sm p-2 rounded-md mt-4">
                                {errorMessage}
                            </div>
                        )}
                    </div>
                </PopoverContent>
            </Popover>
        </div>
    )
}