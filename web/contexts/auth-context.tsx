"use client";

import { createContext, ReactNode, useCallback, useContext, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { extractJwtData } from "@/lib/auth/jwt-decoder";
import { useRefreshToken } from "@/lib/services/auth-service";
import { toast } from "sonner";

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

	const { trigger: refreshToken, error, isMutating: isRefreshingToken } = useRefreshToken();

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
				refreshToken({ token: storedToken })
					.then((response) => {
						login(response.token);
					})
					.catch((error) => {
						toast.error("Error refreshing token");
					});
			}

			setLoading(false);
		};
		checkAuth();
	}, [login, logout, refreshToken]);

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
