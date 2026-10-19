import { get } from "./client";
import { normalizePage, type PageResult } from "./types";
import { apiIdPath, mapArticle } from "./models";

export interface ArticleListItem {
	id: number;
	title: string;
	description?: string;
	cover?: string;
	summary?: string;
	categoryName?: string;
	viewCount?: number;
	commentCount?: number;
	likeCount?: number;
	isTop?: boolean;
	tags?: Array<string | { id: number; name: string }>;
	publishedAt?: string;
	readingTime?: number;
}

export interface ArticleDetail extends ArticleListItem {
	content: string;
	toc?: Array<{ anchor: string; text: string; level: number }>;
	updatedAt?: string;
	prevArticle?: { id: number; title: string } | null;
	nextArticle?: { id: number; title: string } | null;
}

export async function listArticles(params: Record<string, string | number> = {}): Promise<PageResult<ArticleListItem>> {
	const query = new URLSearchParams(Object.entries(params).map(([key, value]) => [key, String(value)]));
	const queryString = query.toString();
	const result = await get<Partial<PageResult<ArticleListItem>>>(`/articles${queryString ? `?${queryString}` : ""}`);
	const page = normalizePage(result);
	return { ...page, records: page.records.map(mapArticle) };
}

export const getArticle = async (id: string | number): Promise<ArticleDetail> => mapArticle(await get<ArticleDetail>(`/articles/${apiIdPath(id)}`));
export const getThought = async (id: string | number): Promise<ArticleDetail> => mapArticle(await get<ArticleDetail>(`/thoughts/${apiIdPath(id)}`));
export const listHotArticles = async (): Promise<ArticleListItem[]> => (await get<ArticleListItem[]>("/articles/hot")).map(mapArticle);
