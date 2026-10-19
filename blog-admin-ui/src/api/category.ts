import { adminGet, adminPost, adminPut, adminDel } from '@/utils/request'
export interface AdminCategory { id:number; name:string; description?:string; sort:number; articleCount:number }
export function getCategories(){return adminGet<AdminCategory[]>('/categories')}
export function createCategory(data:Partial<AdminCategory>){return adminPost<{id:number}>('/categories',data)}
export function updateCategory(id:number,data:Partial<AdminCategory>){return adminPut<void>(`/categories/${id}`,data)}
export function deleteCategory(id:number){return adminDel<void>(`/categories/${id}`)}
