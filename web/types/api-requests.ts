export interface LoginRequest {
	username: string;
	password: string;
}

export interface SignupRequest {
	username: string;
	email: string;
	password: string;
}

export interface ChangePasswordReqest {
	username: string;
	newPassword: string;
	oldPassword: string;
}
