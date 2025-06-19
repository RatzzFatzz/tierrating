"use client"

import Link from "next/link"
import React, { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { EyeIcon, EyeOffIcon } from "lucide-react"
import { useAuth } from "@/contexts/AuthContext";

export default function SignupPage() {
    const [showPassword, setShowPassword] = useState(false)
    const [isLoading, setIsLoading] = useState(false)
    const [username, setUsername] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [confirmPassword, setConfirmPassword] = useState("")
    const [errors, setErrors] = useState<{
        username?: string;
        email?: string;
        password?: string;
        general?: string;
    }>({})

    const router = useRouter()
    const { isAuthenticated, user, isLoading: authLoading } = useAuth()

    // Redirect to dashboard if already authenticated
    useEffect(() => {
        if (isAuthenticated && !authLoading) {
            router.push(`/user/${user}`)
        }
    }, [isAuthenticated, user, authLoading, router])

    const validateForm = () => {
        const newErrors: {
            username?: string;
            email?: string;
            password?: string;
            general?: string;
        } = {}

        // Basic validation
        if (!username.trim()) {
            newErrors.username = "Username is required"
        } else if (username.length < 3) {
            newErrors.username = "Username must be at least 3 characters"
        }

        if (!email.trim()) {
            newErrors.email = "Email is required"
        } else if (!/\S+@\S+\.\S+/.test(email)) {
            newErrors.email = "Email is invalid"
        }

        if (!password) {
            newErrors.password = "Password is required"
        } else if (password.length < 8) {
            newErrors.password = "Password must be at least 8 characters"
        }

        if (password !== confirmPassword) {
            newErrors.password = "Passwords do not match"
        }

        setErrors(newErrors)
        return Object.keys(newErrors).length === 0
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()

        if (!validateForm()) {
            return
        }

        setIsLoading(true)
        setErrors({})

        try {
            const response = await fetch('http://localhost:8080/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, email, password }),
            })

            const data = await response.json()

            if (!response.ok || !data.signupSuccess) {
                // Handle specific error cases
                const newErrors: {
                    username?: string;
                    email?: string;
                    general?: string;
                } = {}

                if (data.usernameTaken) {
                    newErrors.username = "Username is already taken"
                }

                if (data.emailTaken) {
                    newErrors.email = "Email is already registered"
                }

                if (!data.usernameTaken && !data.emailTaken && !data.signupSuccess) {
                    newErrors.general = "Signup failed. Please try again."
                }

                setErrors(newErrors)
                return
            }

            // Signup successful
            router.push('/login?signup=success')
        } catch (error) {
            console.error('Signup error:', error)
            setErrors({
                general: "An unexpected error occurred. Please try again."
            })
        } finally {
            setIsLoading(false)
        }
    }

    // Don't show signup form if checking authentication or already authenticated
    if (authLoading || isAuthenticated) {
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
                    <CardTitle className="text-2xl font-bold">Create an account</CardTitle>
                    <CardDescription>
                        Enter your information to create an account
                    </CardDescription>
                </CardHeader>
                <form onSubmit={handleSubmit}>
                    <CardContent className="space-y-4">
                        {errors.general && (
                            <div className="bg-destructive/15 text-destructive text-sm p-3 rounded-md">
                                {errors.general}
                            </div>
                        )}

                        <div className="space-y-2">
                            <Label htmlFor="username">Username</Label>
                            <Input
                                id="username"
                                placeholder="johndoe"
                                className={`bg-background/50 ${errors.username ? "border-destructive" : ""}`}
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                            />
                            {errors.username && (
                                <p className="text-destructive text-xs mt-1">{errors.username}</p>
                            )}
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                type="email"
                                placeholder="name@example.com"
                                className={`bg-background/50 ${errors.email ? "border-destructive" : ""}`}
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                            />
                            {errors.email && (
                                <p className="text-destructive text-xs mt-1">{errors.email}</p>
                            )}
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="password">Password</Label>
                            <div className="relative">
                                <Input
                                    id="password"
                                    type={showPassword ? "text" : "password"}
                                    placeholder="••••••••"
                                    className={`bg-background/50 pr-10 ${errors.password ? "border-destructive" : ""}`}
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
                                    {showPassword ? (
                                        <EyeOffIcon className="h-4 w-4" />
                                    ) : (
                                        <EyeIcon className="h-4 w-4" />
                                    )}
                                    <span className="sr-only">
                    {showPassword ? "Hide password" : "Show password"}
                  </span>
                                </Button>
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="confirmPassword">Confirm Password</Label>
                            <Input
                                id="confirmPassword"
                                type={showPassword ? "text" : "password"}
                                placeholder="••••••••"
                                className={`bg-background/50 ${errors.password ? "border-destructive" : ""}`}
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                            />
                            {errors.password && (
                                <p className="text-destructive text-xs mt-1">{errors.password}</p>
                            )}
                        </div>
                    </CardContent>

                    <CardFooter className="flex flex-col space-y-4">
                        <Button
                            type="submit"
                            className="w-full"
                            disabled={isLoading}
                        >
                            {isLoading ? "Creating account..." : "Create account"}
                        </Button>
                        <div className="text-center text-sm">
                            Already have an account?{" "}
                            <Link
                                href="/login"
                                className="text-primary hover:underline"
                            >
                                Sign in
                            </Link>
                        </div>
                    </CardFooter>
                </form>
            </Card>
        </div>
    )
}