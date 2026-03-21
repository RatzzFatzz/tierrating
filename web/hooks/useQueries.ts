import { useState, useEffect, useCallback, useRef } from "react";
import { ServerResponse } from "@/types/api-response";

type QueryFn<T> = () => Promise<ServerResponse<T>>;

type QueriesConfig = Record<string, QueryFn<any>>;

type QueriesData<T extends QueriesConfig> = {
	[K in keyof T]: T[K] extends QueryFn<infer D> ? D | null : never;
};

type QueriesErrors<T extends QueriesConfig> = {
	[K in keyof T]: { status: number; error: string } | null;
};

interface UseQueriesResult<T extends QueriesConfig> {
	data: QueriesData<T>;
	errors: QueriesErrors<T>;
	isRunning: boolean;
	isSuccess: boolean;
	isError: boolean;
	refetch: () => void;
}

export function useQueries<T extends QueriesConfig>(queries: T, deps: unknown[] = []): UseQueriesResult<T> {
	type Keys = keyof T;
	const keys = Object.keys(queries) as Keys[];

	const [data, setData] = useState<QueriesData<T>>(() => {
		const init = {} as any;
		keys.forEach((k) => (init[k] = null));
		return init;
	});

	const [errors, setErrors] = useState<QueriesErrors<T>>(() => {
		const init = {} as any;
		keys.forEach((k) => (init[k] = null));
		return init;
	});

	const [isLoading, setIsLoading] = useState(true);

	const queriesRef = useRef(queries);
	queriesRef.current = queries;

	const execute = useCallback(async () => {
		setIsLoading(true);
		const currentKeys = Object.keys(queriesRef.current) as Keys[];

		const results = await Promise.allSettled(currentKeys.map((key) => queriesRef.current[key]()));

		const newData = {} as any;
		const newErrors = {} as any;

		currentKeys.forEach((key, i) => {
			const result = results[i];

			if (result.status === "rejected") {
				newData[key] = null;
				newErrors[key] = { status: 0, error: "Unexpected error" };
				return;
			}

			const response = result.value;
			if (response.ok) {
				newData[key] = response.data;
				newErrors[key] = null;
			} else {
				newData[key] = null;
				newErrors[key] = { status: response.status, error: response.error };
			}
		});

		setData(newData);
		setErrors(newErrors);
		setIsLoading(false);
	}, deps); // eslint-disable-line react-hooks/exhaustive-deps

	useEffect(() => {
		execute();
	}, [execute]);

	const isError = !isLoading && keys.some((k) => errors[k] !== null);
	const isSuccess = !isLoading && keys.every((k) => data[k] !== null);

	return { data, errors, isRunning: isLoading, isSuccess, isError, refetch: execute };
}
