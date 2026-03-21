export type ServerResponse<T> = { ok: true; status: number; data: T } | { ok: false; status: number; error: string };
