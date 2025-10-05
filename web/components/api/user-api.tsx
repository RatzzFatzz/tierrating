"use server"
import {API_URL} from "@/components/global-config";

export async function fetchUser(token: string | null, username: string, logout: () => void): Promise<any> {
    if (!token) {
        throw new Error("No authentication token")
    }
    const headers = {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
    }
    return fetch(`${API_URL}/user/${username}`, {headers})
        .then(response => {
            if (response.status === 401 || response.status === 403) {
                logout();
                throw new Error("Session expired");
            }

            if (!response.ok) {
                throw new Error(`API error: ${response.status}`);
            }

            return response.json();
        })
}

export async function requestLogin (username: string, password: string): Promise<any> {
    return fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
    }).then(response => {
        if (!response.ok) {
            throw new Error('Invalid credentials')
        }
        return response.json();
    });
}

export async function submitSignup (username: string, email: string, password: string): Promise<any> {
    return fetch(`${API_URL}/auth/signup`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, email, password }),
    }).then(response => {
        if (!response.ok) {
            throw new Error('Internal Server Error or API rejected request')
        }

        return response.json()
    })
}