import { jwtDecode } from 'jwt-decode';

interface JwtPayload {
    sub?: string;
    exp?: number;
}

/**
 * Extracts username and expiration from a JWT token
 * @param token JWT token string
 * @returns Object containing username and expiration date, or null if invalid
 */
export function extractJwtData(token: string): {
    username: string;
    expiration: Date | null;
    isExpired: boolean;
} | null {

    if (!token) {
        return null;
    }

    try {
        const decoded = jwtDecode<JwtPayload>(token);
        const username = decoded.sub || '';
        const expiration = decoded.exp ? new Date(decoded.exp * 1000) : null;
        const isExpired = expiration ? new Date() > expiration : false;

        return {
            username,
            expiration,
            isExpired
        };
    } catch (error) {
        console.error('Failed to decode JWT:', error);
        return null;
    }
}