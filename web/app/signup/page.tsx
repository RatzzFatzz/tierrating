import Link from "next/link"

import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from "@/components/ui/card"
import {SignUpForm} from "@/app/signup/signup-form";
import {AnonymousAllowedRoute} from "@/components/contexts/route-accessibility";

export default function SignupPage() {

    return (
        <AnonymousAllowedRoute>
            <div className="flex min-h-screen -mt-24 items-center justify-center px-4">
                <Card className="w-full max-w-md border-border/40 bg-card/60 backdrop-blur-sm z-50">
                    <CardHeader className="space-y-1">
                        <CardTitle className="text-2xl font-bold">Create an account</CardTitle>
                        <CardDescription>
                            Enter your information to create an account
                        </CardDescription>
                    </CardHeader>
                        <CardContent className="space-y-4">
                            <SignUpForm />
                        </CardContent>
                        <CardFooter className="flex flex-col space-y-4 pt-4">
                            <div className="text-center text-sm">
                                <Link
                                    href="/login"
                                    className="text-primary hover:underline"
                                >
                                    Already have an account? Sign in
                                </Link>
                            </div>
                        </CardFooter>
                </Card>
            </div>
        </AnonymousAllowedRoute>
    )
}