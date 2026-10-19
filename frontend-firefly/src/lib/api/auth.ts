import { get, post } from "./client";
import { numericId, optionalText, textValue } from "./models";

export interface User {
	userId: number;
	username: string;
	nickname: string;
	avatar?: string;
	email?: string;
	role?: string;
	status?: number;
}
export interface LoginResult {
	accessToken: string;
	refreshToken: string;
	expiresIn?: number;
	tokenType?: string;
	userInfo?: User;
}
type AuthDto = Record<string, unknown>;

const asRecord = (value: unknown): AuthDto =>
	value && typeof value === "object" && !Array.isArray(value)
		? (value as AuthDto)
		: {};
const mapUser = (raw: unknown): User => {
	const value = asRecord(raw);
	return {
		userId: numericId(value.userId ?? value.id),
		username: textValue(value.username ?? value.name),
		nickname: textValue(value.nickname ?? value.username ?? value.name),
		avatar: optionalText(value.avatar),
		email: optionalText(value.email),
		role: optionalText(value.role),
		status: value.status == null ? undefined : numericId(value.status),
	};
};
const mapLoginResult = (raw: unknown): LoginResult => {
	const value = asRecord(raw);
	const userInfo = value.userInfo == null ? undefined : mapUser(value.userInfo);
	return {
		accessToken: textValue(value.accessToken),
		refreshToken: textValue(value.refreshToken),
		expiresIn: value.expiresIn == null ? undefined : numericId(value.expiresIn),
		tokenType: optionalText(value.tokenType),
		userInfo,
	};
};

function storeTokens(
	result: Pick<LoginResult, "accessToken" | "refreshToken">,
): void {
	if (typeof localStorage === "undefined") return;
	if (result.accessToken)
		localStorage.setItem("accessToken", result.accessToken);
	if (result.refreshToken)
		localStorage.setItem("refreshToken", result.refreshToken);
}

export const login = async (payload: {
	username: string;
	password: string;
}): Promise<LoginResult> => {
	const result = mapLoginResult(await post<unknown>("/auth/login", payload));
	storeTokens(result);
	return result;
};
export const register = async (payload: {
	username: string;
	password: string;
	nickname: string;
	email?: string;
}): Promise<LoginResult> => {
	const result = mapLoginResult(await post<unknown>("/auth/register", payload));
	storeTokens(result);
	return result;
};
export const refreshToken = async (): Promise<void> => {
	const token =
		typeof localStorage !== "undefined"
			? localStorage.getItem("refreshToken")
			: null;
	const result = mapLoginResult(
		await post<unknown>("/auth/refresh", { refreshToken: token }),
	);
	storeTokens(result);
};
export const logout = async (): Promise<void> => {
	await post<void>("/auth/logout", {
		refreshToken:
			typeof localStorage !== "undefined"
				? localStorage.getItem("refreshToken")
				: null,
	});
	if (typeof localStorage !== "undefined") {
		localStorage.removeItem("accessToken");
		localStorage.removeItem("refreshToken");
	}
};
export const currentUser = async (): Promise<User> =>
	mapUser(await get<unknown>("/auth/userinfo"));
export const changePassword = (
	oldPassword: string,
	newPassword: string,
): Promise<void> => post<void>("/auth/password", { oldPassword, newPassword });
