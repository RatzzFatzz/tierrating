import Link from "next/link"
import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card"
import {AnonymousAllowedRoute} from "@/contexts/route-accessibility";
import {InputForm} from "@/app/login/login-form";
import {SuccessMessage} from "@/app/login/login-success-message";
import {cn} from "@/lib/utils";

export default function LoginPage() {

    return (
        <AnonymousAllowedRoute>
            <div className="flex min-h-screen -mt-24 items-center justify-center px-4">
                <Card className={cn(
                    "w-full max-w-md z-50",
                    "bg-card/60 backdrop-blur-sm border border-border/100 shadow-lg",
                )}>
                    <CardHeader className="space-y-1">
                        <CardTitle className="text-2xl font-bold">Login</CardTitle>
                        <CardDescription>
                            Enter your credentials to access your account
                        </CardDescription>
                    </CardHeader>
                    <CardContent>
                        <SuccessMessage/>
                        <InputForm />
                    </CardContent>
                    <CardFooter className="flex flex-col space-y-4 pt-4">
                        <div className="text-center text-sm">
                            <Link
                                href="/signup"
                                className="text-primary hover:underline"
                            >
                                Don&apos;t have an account? Sign up
                            </Link>
                        </div>
                    </CardFooter>
                </Card>
            </div>
        </AnonymousAllowedRoute>
    )
}