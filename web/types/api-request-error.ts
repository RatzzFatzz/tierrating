export class ApiRequestError extends Error {
	status: number;
	backendError?: string;

	constructor(status: number, message: string, backendError?: string) {
		super(message);
		this.name = "ApiRequestError";
		this.status = status;
		this.backendError = backendError;
	}

	isUnauthorized(): boolean {
		return this.status == 401;
	}

	isForbidden(): boolean {
		return this.status == 403;
	}

	isNotFound(): boolean {
		return this.status == 404;
	}

	isInternalServerError(): boolean {
		return this.status >= 500;
	}
}
