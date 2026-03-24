export interface ServerResponse<T> {
	status: number;
	error?: string;
	data?: T;
}

export interface ErrorResponseDTO {
	error: string;
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
	connectedServices: string[];
}

export interface ThirdPartyAuthResponse {
	message: string;
}

export interface ThirdPartyInfoResponse {
	clientId: string;
}
