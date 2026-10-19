import { get } from "./client";
import { listArticles } from "./articles";

export interface ArchiveEntry { id: string | number; title: string; publishedAt?: string; }
export interface ArchiveYear { year: string; months: Array<{ month: string; articles: ArchiveEntry[] }> }
export async function getTimeline(filters: { categoryId?: number; tagId?: number } = {}): Promise<ArchiveYear[]> {
	if (!filters.categoryId && !filters.tagId) return get<ArchiveYear[]>("/archives/timeline");
	const page = await listArticles({ page: 1, pageSize: 100, ...(filters.categoryId ? { categoryId: filters.categoryId } : {}), ...(filters.tagId ? { tagId: filters.tagId } : {}) });
	const years = new Map<string, Map<string, ArchiveEntry[]>>();
	for (const article of page.records) {
		const date = new Date(article.publishedAt || 0);
		const year = String(date.getFullYear());
		const month = `${year}-${String(date.getMonth() + 1).padStart(2, "0")}`;
		if (!years.has(year)) years.set(year, new Map());
		const months = years.get(year)!;
		if (!months.has(month)) months.set(month, []);
		months.get(month)!.push({ id: article.id, title: article.title, publishedAt: article.publishedAt });
	}
	return [...years.entries()].sort((a, b) => b[0].localeCompare(a[0])).map(([year, months]) => ({ year, months: [...months.entries()].map(([month, articles]) => ({ month, articles })) }));
}
