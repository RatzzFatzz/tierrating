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
	connectedServices: string[];
}

export interface ThirdPartyInfoResponse {
	clientId: string;
}
