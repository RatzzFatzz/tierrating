import {API_URL} from "@/components/global-config";

export const fetchUser = async (token: string | null, username: string, logout: () => void) => {
    if (!token) {
        throw new Error("No authentication token")
    }
    const headers = {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
    }
    try {
        const response = await fetch(`${API_URL}/user/${username}`, {headers});

        if (response.status === 401 || response.status === 403) {
            logout();
            throw new Error("Session expired");
        }

        if (!response.ok) {
            throw new Error(`API error: ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error("API request failed: ", error)
        throw error
    }
}

export const requestLogin = async (username: string, password: string) => {
    const response = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
    })

    if (!response.ok) {
        throw new Error('Invalid credentials')
    }

    return await response.json()
}

export const submitSignup = async (username: string, email: string, password: string) => {
    const response = await fetch(`${API_URL}/auth/signup`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, email, password }),
    })

    if (!response.ok) {
        throw new Error('Internal Server Error or API rejected request')
    }

    return await response.json()
}