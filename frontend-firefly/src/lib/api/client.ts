import { apiPath } from "@/config/runtimeConfig";
import { ApiError, type ApiEnvelope } from "./types";

const REQUEST_TIMEOUT_MS = 15_000;
const RETRYABLE_STATUS = new Set([502, 503]);

function errorKind(status: number): ApiError["kind"] {
	if (status === 401) return "unauthorized";
	if (status === 403) return "forbidden";
	if (status === 404) return "not-found";
	if (status === 429) return "rate-limited";
	if (RETRYABLE_STATUS.has(status)) return "unavailable";
	return "business";
}

async function requestOnce<T>(path: string, init: RequestInit): Promise<T> {
	const controller = new AbortController();
	const timer = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);
	try {
		const response = await fetch(apiPath(path), {
			...init,
			credentials: "include",
			headers: {
				Accept: "application/json",
				"X-Client-Type": "web",
				...(typeof localStorage !== "undefined" && localStorage.getItem("accessToken") ? { Authorization: `Bearer ${localStorage.getItem("accessToken")}` } : {}),
				...(init.body ? { "Content-Type": "application/json" } : {}),
				...init.headers,
			},
			signal: controller.signal,
		});
		let payload: ApiEnvelope<T>;
		try {
			payload = (await response.json()) as ApiEnvelope<T>;
		} catch {
			throw new ApiError("Invalid API response", "business", response.status);
		}
		if (response.status === 401) throw new ApiError("Authentication required", "unauthorized", 401);
		if (!response.ok) throw new ApiError(payload.message || response.statusText, errorKind(response.status), response.status);
		if (payload.code !== 200) throw new ApiError(payload.message || "Request failed", "business", response.status);
		return payload.data as T;
	} catch (error) {
		if (error instanceof ApiError) throw error;
		if (error instanceof DOMException && error.name === "AbortError") throw new ApiError("Request timed out", "timeout");
		throw new ApiError("Network request failed", "network");
	} finally {
		clearTimeout(timer);
	}
}

export async function apiRequest<T>(path: string, init: RequestInit = {}): Promise<T> {
	const method = (init.method || "GET").toUpperCase();
	try {
		return await requestOnce<T>(path, init);
	} catch (error) {
		if (error instanceof ApiError && error.kind === "unauthorized" && path !== "/auth/refresh" && path !== "/auth/login") {
			try {
				const refreshed = await requestOnce<{ accessToken?: string }>("/auth/refresh", { method: "POST", body: JSON.stringify({ refreshToken: typeof localStorage !== "undefined" ? localStorage.getItem("refreshToken") : null }) });
				if (typeof localStorage !== "undefined" && refreshed?.accessToken) localStorage.setItem("accessToken", refreshed.accessToken);
				return await requestOnce<T>(path, init);
			} catch {
				if (typeof localStorage !== "undefined") { localStorage.removeItem("accessToken"); localStorage.removeItem("refreshToken"); }
				throw error;
			}
		}
		if (method === "GET" && error instanceof ApiError && (error.kind === "network" || error.kind === "unavailable")) {
			return requestOnce<T>(path, init);
		}
		throw error;
	}
}

export const get = <T>(path: string, init?: RequestInit): Promise<T> => apiRequest<T>(path, { ...init, method: "GET" });
export const post = <T>(path: string, body: unknown, init?: RequestInit): Promise<T> =>
	apiRequest<T>(path, { ...init, method: "POST", body: JSON.stringify(body) });

export const apiErrorMessage = (error: unknown, fallback: string): string =>
	error instanceof ApiError && error.message ? error.message : fallback;
