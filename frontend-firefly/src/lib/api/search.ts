import { get } from "./client";
import { normalizePage, type PageResult } from "./types";
import { mapArticle } from "./models";

export interface SearchResult { id: number; title: string; snippet?: string; summary?: string; url?: string; type?: number; }
export async function search(query: string, page = 1, size = 10): Promise<PageResult<SearchResult>> {
	const params = new URLSearchParams({ keyword: query, page: String(page), pageSize: String(size) });
	const normalized = normalizePage(await get<Partial<PageResult<SearchResult>>>(`/search?${params}`));
	return { ...normalized, records: normalized.records.map((item) => { const mapped = mapArticle(item); return { ...mapped, snippet: mapped.snippet || mapped.summary }; }) };
}
