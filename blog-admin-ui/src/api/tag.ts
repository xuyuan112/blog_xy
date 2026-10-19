import { adminGet, adminPost, adminPut, adminDel } from '@/utils/request'
export interface AdminTag { id:number; name:string; color?:string; articleCount:number }
export function getTags(){return adminGet<AdminTag[]>('/tags')}
export function createTag(data:Partial<AdminTag>){return adminPost<{id:number}>('/tags',data)}
export function updateTag(id:number,data:Partial<AdminTag>){return adminPut<void>(`/tags/${id}`,data)}
export function deleteTag(id:number){return adminDel<void>(`/tags/${id}`)}
