import { post } from "./client";

export interface LikeToggleResult { liked: boolean; likeCount: number; }
export const toggleLike = (targetType: 1 | 2, targetId: number): Promise<LikeToggleResult> =>
	post<LikeToggleResult>("/likes/toggle", { targetType, targetId });
