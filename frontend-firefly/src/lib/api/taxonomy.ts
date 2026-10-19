import { get } from "./client";

export interface TagSummary { id?: number; name: string; color?: string; weight?: number; articleCount?: number; }
export interface CategorySummary { id?: number; name: string; description?: string; articleCount?: number; }

export const listTags = (): Promise<TagSummary[]> => get<TagSummary[]>("/tags");
export const listCategories = (): Promise<CategorySummary[]> => get<CategorySummary[]>("/categories");
