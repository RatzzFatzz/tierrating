"use client"

import { createContext, useContext, useEffect, useState, ReactNode } from "react"
import { useRouter } from "next/navigation"
import {extractJwtData} from "@/components/jwt-decoder";

interface AuthContextType {
    token: string | null
    user: string | null
    isLoading: boolean
    isAuthenticated: boolean
    isExpired: boolean
    login: (token: string) => void
    logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
    const [token, setToken] = useState<string | null>(null)
    const [user, setUser] = useState<string | null>(null)
    const [isAuthenticated, setIsAuthenticated] = useState(false)
    const [isLoading, setIsLoading] = useState(true)
    const [isExpired, setIsExpired] = useState(false)
    const router = useRouter()

    // Load token from localStorage on initial render
    useEffect(() => {
        const checkAuth= () => {
            const storedToken = localStorage.getItem("authToken")
            if (!storedToken) {
                logout()
                return;
            }

            const decodedJwt = extractJwtData(storedToken);
            if (!decodedJwt || decodedJwt.isExpired) {
                logout()
                return;
            }

            setToken(storedToken)
            setUser(decodedJwt.username)
            setIsExpired(decodedJwt.isExpired)
            setIsAuthenticated(true)
            setIsLoading(false);
        }
        checkAuth()
    }, [])

    const login = (newToken: string) => {
        localStorage.setItem("authToken", newToken)
        setToken(newToken)
        const extracted = extractJwtData(newToken);
        if (extracted) setUser(extracted.username)
        setIsAuthenticated(true)
    }

    const logout = () => {
        localStorage.removeItem("authToken")
        setToken(null)
        setUser(null)
        setIsAuthenticated(false)
        router.push("/login")
    }

    return (
        <AuthContext.Provider value={{ token, user, isAuthenticated, isLoading, isExpired, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    const context = useContext(AuthContext)
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider")
    }
    return context
}