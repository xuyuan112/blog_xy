export interface ApiEnvelope<T> {
	code: number;
	message?: string;
	data?: T;
}

export interface PageResult<T> {
	records: T[];
	pages: number;
	total: number;
}

export type ApiStatus = "forbidden" | "not-found" | "rate-limited" | "unavailable" | "unauthorized";

export class ApiError extends Error {
	readonly status?: number;
	readonly kind: ApiStatus | "network" | "timeout" | "business";

	constructor(message: string, kind: ApiError["kind"], status?: number) {
		super(message);
		this.name = "ApiError";
		this.kind = kind;
		this.status = status;
	}
}

export function normalizePage<T>(value: Partial<PageResult<T>> | null | undefined): PageResult<T> {
	const records = Array.isArray(value?.records) ? value.records : [];
	const pages = typeof value?.pages === "number" && value.pages > 0 ? value.pages : 0;
	const total = typeof value?.total === "number" && value.total > 0 ? value.total : 0;
	return { records, pages, total };
}
