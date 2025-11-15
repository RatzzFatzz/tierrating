"use client"

import {z} from "zod"
import {useState} from "react";
import {useRouter} from "next/navigation";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {changePassword} from "@/components/api/user-api";
import {useAuth} from "@/components/contexts/auth-context";
import {Form, FormControl, FormField, FormItem, FormLabel, FormMessage} from "@/components/ui/form";
import {Input} from "@/components/ui/input";
import {Button} from "@/components/ui/button";
import {EyeIcon, EyeOffIcon} from "lucide-react";

const FormSchema = z.object({
    old: z.string(),
    new: z.string().min(8).max(30),
    confirmNew: z.string().min(8).max(30)
}).refine((data) => data.new === data.confirmNew, {
    message: "Passwords don't match",
    path: ['confirmPassword']
});

export default function ChangePassword() {
    const [showOld, setShowOld] = useState(false);
    const [showNew, setShowNew] = useState(false);
    const [showConfirmNew, setShowConfirmNew] = useState(false);
    const [isSubmitLoading, setSubmitLoading] = useState(false)
    const [errorMessage, setErrorMessage] = useState("");
    const {user, token, logout} = useAuth();
    const router = useRouter();

    const form = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
        defaultValues: {
            old: "",
            new: "",
            confirmNew: "",
        },
    })

    function onSubmit(data: z.infer<typeof FormSchema>) {
        setSubmitLoading(true);
        changePassword(data.old, data.new, user, token)
            .then(response => {
                if (response.error) throw new Error(response.error);
                if (!response.data) throw new Error("Faulty response");
                if (!response.data.success) throw new Error(response.data.message);
                setTimeout(() => logout(), 1000);
                setErrorMessage("");
            })
            .catch(error => {
                setErrorMessage(error.message);
            })
            .finally(() => {
                setSubmitLoading(false);
            })
    }

    return (
        <div className={"w-full"}>
            {errorMessage && (
                <div className="bg-destructive/15 text-destructive text-sm p-3 rounded-md mb-4">
                    {errorMessage}
                </div>
            )}
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                    <FormField
                        control={form.control}
                        name="old"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>Old password</FormLabel>
                                <FormControl>
                                    <div className="relative">
                                        <Input type={showOld ? "text" : "password"} {...field} />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="icon"
                                            className="absolute right-0 top-0 h-full px-3 py-2 text-muted-foreground hover:text-foreground"
                                            onClick={() => setShowOld(!showOld)}
                                        >
                                            {showOld ? (
                                                <EyeOffIcon className="h-4 w-4"/>
                                            ) : (
                                                <EyeIcon className="h-4 w-4"/>
                                            )}
                                            <span className="sr-only">
                                                {showOld ? "Hide password" : "Show password"}
                                            </span>
                                        </Button>
                                    </div>
                                </FormControl>
                                <FormMessage/>
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="new"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>New password</FormLabel>
                                <FormControl>
                                    <div className="relative">
                                        <Input type={showNew ? "text" : "password"} {...field} />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="icon"
                                            className="absolute right-0 top-0 h-full px-3 py-2 text-muted-foreground hover:text-foreground"
                                            onClick={() => setShowNew(!showNew)}
                                        >
                                            {showNew ? (
                                                <EyeOffIcon className="h-4 w-4"/>
                                            ) : (
                                                <EyeIcon className="h-4 w-4"/>
                                            )}
                                            <span className="sr-only">
                                                {showNew ? "Hide password" : "Show password"}
                                            </span>
                                        </Button>
                                    </div>
                                </FormControl>
                                <FormMessage/>
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="confirmNew"
                        render={({field}) => (
                            <FormItem>
                                <FormLabel>Confirm new password</FormLabel>
                                <FormControl>
                                    <div className="relative">
                                        <Input type={showConfirmNew ? "text" : "password"} {...field} />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="icon"
                                            className="absolute right-0 top-0 h-full px-3 py-2 text-muted-foreground hover:text-foreground"
                                            onClick={() => setShowConfirmNew(!showConfirmNew)}
                                        >
                                            {showConfirmNew ? (
                                                <EyeOffIcon className="h-4 w-4"/>
                                            ) : (
                                                <EyeIcon className="h-4 w-4"/>
                                            )}
                                            <span className="sr-only">
                                                {showConfirmNew ? "Hide password" : "Show password"}
                                            </span>
                                        </Button>
                                    </div>
                                </FormControl>
                                <FormMessage/>
                            </FormItem>
                        )}
                    />
                    <Button
                        type="submit"
                        className="w-full"
                        disabled={isSubmitLoading}
                    >
                        {isSubmitLoading ? "Changing password..." : "Change password"}
                    </Button>
                </form>
            </Form>
        </div>
    )
}