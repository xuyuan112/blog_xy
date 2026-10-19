import { get, post } from "./client";
import { normalizePage, type PageResult } from "./types";
import { apiIdPath, mapMoment } from "./models";

export interface Moment { id: number; content: string; images?: string[]; createdAt?: string; likeCount?: number; commentCount?: number; liked?: boolean; }
export async function listMoments(page = 1, pageSize = 10): Promise<PageResult<Moment>> {
	const result = await get<Partial<PageResult<Moment>>>(`/moments?page=${page}&pageSize=${pageSize}`);
	const normalized = normalizePage(result);
	return { ...normalized, records: normalized.records.map(mapMoment) };
}
export const getMoment = async (id: string | number): Promise<Moment> => mapMoment(await get<Moment>(`/moments/${apiIdPath(id)}`));
export const toggleMomentLike = (id: string | number): Promise<{ liked: boolean; likeCount: number }> => post<{ liked: boolean; likeCount: number }>("/likes/toggle", { targetType: 2, targetId: Number(id) });
