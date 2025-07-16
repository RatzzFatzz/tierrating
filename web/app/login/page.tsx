"use client"

import Link from "next/link"
import {Suspense, useEffect, useState} from "react"
import {useRouter, useSearchParams} from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { EyeIcon, EyeOffIcon } from "lucide-react"
import {useAuth} from "@/contexts/AuthContext";
import {requestLogin} from "@/components/api/user-api";

function SuccessMessage() {
    const searchParams = useSearchParams()
    const signupSuccess = searchParams.get('signup') === 'success'

    return (
        <div>
            {signupSuccess && (
                <div className="bg-green-500/15 text-green-500 text-sm p-3 rounded-md">
                    Account created successfully! You can now log in.
                </div>
            )}
        </div>
    )
}

export default function LoginPage() {
    const [showPassword, setShowPassword] = useState(false)
    const [isLoading, setIsLoading] = useState(false)
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const router = useRouter()
    const { login, isAuthenticated, user} = useAuth();

    useEffect(() => {
        console.debug(`${user} - ${isAuthenticated}`)
        if (isAuthenticated && !isLoading) {
            router.push(`/user/${user}`)
        }
    }, [isAuthenticated, isLoading, user, router])

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsLoading(true)
        setErrorMessage("")

        requestLogin(username, password)
            .then(data => login(data.token))
            .catch(error => {
                console.error('Login error:', error)
                setErrorMessage(error instanceof Error ? error.message : 'Login failed. Please try again.')
            })
            .finally(() => setIsLoading(false))
    }

    if (isLoading || isAuthenticated) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="animate-pulse text-muted-foreground">Loading...</div>
            </div>
        )
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-background px-4">
            <Card className="w-full max-w-md border-border/40 bg-card/60 backdrop-blur-sm">
                <CardHeader className="space-y-1">
                    <CardTitle className="text-2xl font-bold">Login</CardTitle>
                    <CardDescription>
                        Enter your credentials to access your account
                    </CardDescription>
                </CardHeader>
                <form onSubmit={handleSubmit}>
                    <CardContent className="space-y-4">
                        <Suspense>
                            <SuccessMessage />
                        </Suspense>
                        {errorMessage && (
                            <div className="bg-destructive/15 text-destructive text-sm p-3 rounded-md">
                                {errorMessage}
                            </div>
                        )}
                        <div className="space-y-2">
                            <Label htmlFor="username">Username</Label>
                            <Input
                                id="username"
                                type="username"
                                placeholder="Username"
                                required
                                className="bg-background/50"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                            />
                        </div>
                        <div className="space-y-2">
                            <div className="flex items-center justify-between">
                                <Label htmlFor="password">Password</Label>
                            </div>
                            <div className="relative">
                                <Input
                                    id="password"
                                    type={showPassword ? "text" : "password"}
                                    placeholder="••••••••"
                                    required
                                    className="bg-background/50 pr-10"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                />
                                <Button
                                    type="button"
                                    variant="ghost"
                                    size="icon"
                                    className="absolute right-0 top-0 h-full px-3 py-2 text-muted-foreground hover:text-foreground"
                                    onClick={() => setShowPassword(!showPassword)}
                                >
                                    {showPassword ? (<EyeOffIcon className="h-4 w-4" />) : (<EyeIcon className="h-4 w-4" />)}
                                    <span className="sr-only">{showPassword ? "Hide password" : "Show password"}</span>
                                </Button>
                            </div>
                            <div className="flex items-center justify-end">
                                <Link
                                    href="/forgot-password"
                                    className="text-xs text-primary hover:underline"
                                >
                                    Forgot password?
                                </Link>
                            </div>
                        </div>
                    </CardContent>
                    <CardFooter className="flex flex-col space-y-4 pt-4">
                        <Button
                            type="submit"
                            className="w-full"
                            disabled={isLoading}
                        >
                            {isLoading ? "Signing in..." : "Sign in"}
                        </Button>
                        <div className="text-center text-sm">
                            <Link
                                href="/signup"
                                className="text-primary hover:underline"
                            >
                                Don&apos;t have an account? Sign up
                            </Link>
                        </div>
                    </CardFooter>
                </form>
            </Card>
        </div>
    )
}