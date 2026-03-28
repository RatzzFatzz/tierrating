export interface ServerResponse<T> {
	status: number;
	error?: string;
	data?: T;
}

export interface ErrorResponseDTO {
	error: string;
}

