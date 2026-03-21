import { useState, useEffect, useCallback, useRef } from "react";
import { ServerResponse } from "@/types/api-response";

type QueryFn<T> = () => Promise<ServerResponse<T>>;

export function useQuery<T>(queryFn: QueryFn<T>, deps: unknown[] = []) {
	const [data, setData] = useState<T | null>(null);
	const [error, setError] = useState<{ status: number; error: string } | null>(null);
	const [isLoading, setIsLoading] = useState(true);
	const queryFnRef = useRef(queryFn);
	queryFnRef.current = queryFn;

	const execute = useCallback(async () => {
		setIsLoading(true);
		setError(null);

		const result = await queryFnRef.current();

		if (result.ok) {
			setData(result.data);
		} else {
			setData(null);
			setError({ status: result.status, error: result.error });
		}

		setIsLoading(false);
	}, deps); // eslint-disable-line react-hooks/exhaustive-deps

	useEffect(() => {
		execute();
	}, [execute]);

	return {
		data,
		error,
		isRunning: isLoading,
		isSuccess: !isLoading && data !== null,
		isError: !isLoading && error !== null,
		refetch: execute,
	};
}
