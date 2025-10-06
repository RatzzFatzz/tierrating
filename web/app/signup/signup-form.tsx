"use client"

import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"

import { Button } from "@/components/ui/button"
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import {submitSignup} from "@/components/api/user-api";
import {redirect} from "next/navigation";
import {useState} from "react";
import {EyeIcon, EyeOffIcon} from "lucide-react";

const FormSchema = z.object({
    username: z.string().min(5).max(30),
    email: z.string().email(),
    password: z.string().min(8).max(30),
    confirmPassword: z.string().min(8).max(30),
}).refine((data) => data.password === data.confirmPassword, {
    message: "Passwords don't match",
    path: ['confirmPassword'],
})

export function SignUpForm() {
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [isSubmitLoading, setSubmitLoading] = useState(false)
    const [errorMessage, setErrorMessage] = useState("");

    const form = useForm<z.infer<typeof FormSchema>>({
        resolver: zodResolver(FormSchema),
        defaultValues: {
            username: "",
            email: "",
            password: "",
            confirmPassword: "",
        },
    })

    function onSubmit(data: z.infer<typeof FormSchema>) {
        setSubmitLoading(true);
        submitSignup(data.username, data.email, data.password)
            .then(r => {
                if (r.usernameTaken) throw new Error("Username already taken");
                if (r.emailTaken) throw new Error("Email already associated with a different account")
                if (!r.signupSuccess) throw new Error("Signup failed. Please try again");
                setErrorMessage("");
                redirect("/login?signup=success");
            })
            .catch(error => {
                setErrorMessage(error.message);
            })
            .finally(() => {
                setSubmitLoading(false);
            })
    }

    return (
        <div>
            {errorMessage && (
                <div className="bg-destructive/15 text-destructive text-sm p-3 rounded-md mb-4">
                    {errorMessage}
                </div>
            )}
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                    <FormField
                        control={form.control}
                        name="username"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Username</FormLabel>
                                <FormControl>
                                    <Input {...field} />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="email"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>E-Mail</FormLabel>
                                <FormControl>
                                    <Input {...field} />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="password"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Password</FormLabel>
                                <FormControl>
                                    <div className="relative">
                                        <Input type={showPassword ? "text" : "password"} {...field} />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="icon"
                                            className="absolute right-0 top-0 h-full px-3 py-2 text-muted-foreground hover:text-foreground"
                                            onClick={() => setShowPassword(!showPassword)}
                                        >
                                            {showPassword ? (
                                                <EyeOffIcon className="h-4 w-4"/>
                                            ) : (
                                                <EyeIcon className="h-4 w-4"/>
                                            )}
                                            <span className="sr-only">
                                                {showPassword ? "Hide password" : "Show password"}
                                            </span>
                                        </Button>
                                    </div>
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="confirmPassword"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Confirm Password</FormLabel>
                                <FormControl>
                                    <div className="relative">
                                        <Input type={showConfirmPassword ? "text" : "password"} {...field} />
                                        <Button
                                            type="button"
                                            variant="ghost"
                                            size="icon"
                                            className="absolute right-0 top-0 h-full px-3 py-2 text-muted-foreground hover:text-foreground"
                                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                        >
                                            {showConfirmPassword ? (
                                                <EyeOffIcon className="h-4 w-4"/>
                                            ) : (
                                                <EyeIcon className="h-4 w-4"/>
                                            )}
                                            <span className="sr-only">
                                                {showConfirmPassword ? "Hide password" : "Show password"}
                                            </span>
                                        </Button>
                                    </div>
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <Button
                        type="submit"
                        className="w-full"
                        disabled={isSubmitLoading}
                    >
                        {isSubmitLoading ? "Creating account..." : "Create account"}
                    </Button>
                </form>
            </Form>
        </div>
    )
}
