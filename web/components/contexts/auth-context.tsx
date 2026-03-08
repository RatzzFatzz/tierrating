"use client";

import { createContext, ReactNode, useCallback, useContext, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { extractJwtData } from "@/components/auth/jwt-decoder";
import { refreshToken } from "@/components/api/user-api";

interface AuthContextType {
	token: string | null;
	user: string | null;
	isLoading: boolean;
	isAuthenticated: boolean;
	isExpired: boolean;
	expiration: Date | null;
	login: (token: string) => void;
	logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
	const [token, setToken] = useState<string | null>(null);

	const [user, setUser] = useState<string | null>(null);
	const [expiration, setExpiration] = useState<Date | null>(null);

	const [isAuthenticated, setIsAuthenticated] = useState(false);
	const [isExpired, setIsExpired] = useState(false);

	const [isLoading, setLoading] = useState(true);
	const router = useRouter();

	const login = useCallback((newToken: string) => {
		localStorage.setItem("authToken", newToken);
		setToken(newToken);
		const extracted = extractJwtData(newToken);
		if (extracted) setUser(extracted.username);
		setIsAuthenticated(true);
	}, []);

	const logout = useCallback(() => {
		localStorage.removeItem("authToken");
		setToken(null);
		setUser(null);
		setIsAuthenticated(false);
		router.push("/login");
	}, [router]);

	// Load token from localStorage on initial render
	useEffect(() => {
		const checkAuth = () => {
			const storedToken = localStorage.getItem("authToken");
			if (!storedToken) {
				logout();
				setLoading(false);
				return;
			}

			const decodedJwt = extractJwtData(storedToken);
			if (!decodedJwt || decodedJwt.isExpired) {
				logout();
				setLoading(false);
				return;
			}

			setToken(storedToken);
			setUser(decodedJwt.username);
			setIsExpired(decodedJwt.isExpired);
			setExpiration(decodedJwt.expiration);
			setIsAuthenticated(true);

			if (
				!decodedJwt.isExpired &&
				decodedJwt.expiration &&
				decodedJwt.expiration.getTime() - new Date().getTime() - 15 * 60 * 1000 <= 0
			) {
				refreshToken(storedToken)
					.then((response) => {
						if (response.status === 401) throw new Error("Invalid credentials");
						if (response.error) throw new Error(response.error);
						if (!response.data) throw new Error("Faulty response");
						login(response.data.token);
						console.debug("Token refreshed");
					})
					.catch((error) => {
						console.debug(error);
					});
			}

			setLoading(false);
		};
		checkAuth();
	}, [login, logout]);

	return (
		<AuthContext.Provider value={{ token, user, isAuthenticated, isLoading, isExpired, expiration, login, logout }}>
			{children}
		</AuthContext.Provider>
	);
}

export function useAuth() {
	const context = useContext(AuthContext);
	if (context === undefined) {
		throw new Error("useAuth must be used within an AuthProvider");
	}
	return context;
}
