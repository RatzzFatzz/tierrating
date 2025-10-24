"use client"

import Link from "next/link"
import {useState} from "react"
import {Button} from "@/components/ui/button"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {EyeIcon, EyeOffIcon} from "lucide-react"
import {useAuth} from "@/contexts/auth-context";
import {requestLogin} from "@/components/api/user-api";

export function InputForm() {
    const [showPassword, setShowPassword] = useState(false)
    const [isLoading, setIsLoading] = useState(false)
    const [username, setUsername] = useState("")
    const [password, setPassword] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const {login} = useAuth();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setIsLoading(true)
        setErrorMessage("")

        requestLogin(username, password)
            .then(response => {
                if (response.status === 401) throw new Error("Invalid credentials");
                if (response.error) throw new Error(response.error);
                if (!response.data) throw new Error("Faulty response")
                login(response.data.token);
            })
            .catch(error => {
                setErrorMessage(error instanceof Error ? error.message : 'Login failed. Please try again.')
            })
            .finally(() => setIsLoading(false));
    }

    return (
        <div>
            {errorMessage && (
                <div className="bg-destructive/15 text-destructive text-sm p-3 rounded-md mb-4">
                    {errorMessage}
                </div>
            )}
            <form onSubmit={handleSubmit} className="space-y-4">
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
                            {showPassword ? (<EyeOffIcon className="h-4 w-4"/>) : (
                                <EyeIcon className="h-4 w-4"/>)}
                            <span
                                className="sr-only">{showPassword ? "Hide password" : "Show password"}</span>
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
                <Button
                    type="submit"
                    className="w-full"
                    disabled={isLoading}
                >
                    {isLoading ? "Signing in..." : "Sign in"}
                </Button>
            </form>
        </div>
)
}
