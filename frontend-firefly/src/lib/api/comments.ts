import { get, post } from "./client";
import { apiIdPath, mapComment, numericId } from "./models";

export interface Comment {
	id: number;
	targetType: number;
	targetId: number;
	content: string;
	nickname: string;
	avatar?: string;
	createdAt?: string;
}
export interface CommentPage {
	records: Comment[];
	total: number;
	page: number;
	pageSize: number;
	pages: number;
}
export const listComments = async (
	targetType: number,
	targetId: number,
	page = 1,
	pageSize = 20,
): Promise<CommentPage> => {
	const value = await get<Partial<CommentPage>>(
		`/comments?targetType=${targetType}&targetId=${apiIdPath(targetId)}&page=${page}&pageSize=${pageSize}`,
	);
	return {
		records: (value.records || []).map(mapComment),
		total: numericId(value.total),
		page: numericId(value.page) || page,
		pageSize: numericId(value.pageSize) || pageSize,
		pages: numericId(value.pages),
	};
};
export const createComment = async (payload: {
	targetType: number;
	targetId: number;
	content: string;
	nickname: string;
	email?: string;
	website?: string;
}): Promise<{ id: number }> => {
	const result = await post<{ id?: unknown }>("/comments", payload);
	return { id: numericId(result.id) };
};
