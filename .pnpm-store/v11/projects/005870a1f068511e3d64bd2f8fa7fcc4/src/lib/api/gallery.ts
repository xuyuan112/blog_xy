import { get } from "./client";
import { normalizePage, type PageResult } from "./types";
import { apiIdPath, mapGallery, mapPhoto } from "./models";

export interface Photo { id: number; galleryId?: number; thumbnail?: string; original?: string; shootTime?: string; exif?: Record<string, string | number>; }
export interface Gallery { id: number; name: string; description?: string; cover?: string; photoCount?: number; shootDate?: string; }
export async function listGalleries(page = 1, pageSize = 12): Promise<PageResult<Gallery>> {
	const result = await get<Partial<PageResult<Gallery>>>(`/galleries?page=${page}&pageSize=${pageSize}`);
	const normalized = normalizePage(result);
	return { ...normalized, records: normalized.records.map(mapGallery) };
}
export const getGallery = async (id: string | number): Promise<Gallery & { photos: Photo[] }> => { const value = await get<Gallery & { photos: Photo[] }>(`/galleries/${apiIdPath(id)}`); return { ...mapGallery(value), photos: (value.photos || []).map(mapPhoto) }; };
export const getPhoto = async (id: string | number): Promise<Photo> => mapPhoto(await get<Photo>(`/photos/${apiIdPath(id)}`));
