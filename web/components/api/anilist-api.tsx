import {API_URL} from "@/components/global-config";

export const authorize = async(username: string | null, token: string | null, code: string | null)=> {
    if (!username && !token && !code) {
        return new Error("Invalid username, token or code")
    }

    const response = await fetch(`${API_URL}/anilist/auth/${username}`, {
        method: 'POST',
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({code})
    })

    if (!response.ok) {
        return new Error('Could not authorize with AniList');
    }

    return await response.json();
}

