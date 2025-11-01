"use server"
import {API_URL} from "@/components/global-config";
import {LoginResponse, ServerResponse, SignupResponse, UserResponse} from "@/model/response-types";

export async function requestLogin(username: string, password: string): Promise<ServerResponse<LoginResponse>> {
    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({username, password}),
        });

        const data = await response.json().catch(() => null)
        return {data, status: response.status};
    } catch (error) {
        console.error('API proxy error: ', error);
        return {error: 'Server unavailable', status: 500}
    }
}

export async function submitSignup(username: string, email: string, password: string): Promise<ServerResponse<SignupResponse>> {
    try {
        const response = await fetch(`${API_URL}/auth/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({username, email, password}),
        });

        const data = await response.json().catch(() => null)
        return {data, status: response.status};
    } catch (error) {
        console.error('API proxy error: ', error);
        return {error: 'Server unavailable', status: 500}
    }
}

export async function refreshToken(token: string | null): Promise<ServerResponse<LoginResponse>> {
    if (!token) throw new Error("No authentication token");

    try {
        const response = await fetch(`${API_URL}/auth/refresh`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`,
            },
            body: JSON.stringify({token}),
        })

        const data = await response.json().catch(() => null);
        return {data, status: response.status};
    } catch (error) {
        console.error('API proxy error: ', error);
        return {error: 'Server unavailable', status: 500}
    }
}


export async function fetchUser(token: string | null, username: string): Promise<ServerResponse<UserResponse>> {
    if (!token) throw new Error("No authentication token")

    try {
        const response = await fetch(`${API_URL}/user/${username}`, {
            method: 'GET',
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`,
            }
        });

        const data = await response.json().catch(() => null)
        return {data, status: response.status};
    } catch (error) {
        console.error('API proxy error: ', error);
        return {error: 'Server unavailable', status: 500}
    }
}