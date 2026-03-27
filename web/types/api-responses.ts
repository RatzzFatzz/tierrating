export interface LoginResponse {
	token: string;
}

export interface SignupResponse {
	usernameTaken: boolean;
	emailTaken: boolean;
	signupSuccess: boolean;
}

export interface ThirdPartyInfoResponse {
	clientId: string;
}

