import type { CommentConfig } from "../types/commentConfig";

// Third-party comment providers are deliberately disabled. Public comments are
// served by blog-web through the API client and BackendComments.svelte.
export const commentConfig: CommentConfig = {
	type: "none",
};
