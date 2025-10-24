export interface ServerResponse<T> {
    status: number;
    error?: string;
    data?: T;
}

export interface LoginResponse {
    token: string;
}

export interface SignupResponse {
    usernameTaken: boolean;
    emailTaken: boolean;
    signupSuccess: boolean;
}

export interface UserResponse {
    id: number;
    username: string;
    bio: string;

    anilistConnected: boolean;
    traktConnected: boolean;
}

export interface UpdateScoreResponse {
    error: boolean;
    message: string;
}

export interface ThirdPartyAuthResponse {
    message: string
}