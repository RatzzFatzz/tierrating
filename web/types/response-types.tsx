export interface ServerResponse<T> {
	status: number;
	error?: string;
	data?: T;
}

export interface ErrorResponseDTO {
	error: string;
}

export interface ThirdPartyAuthResponse {
	message: string;
}


