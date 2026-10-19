import { adminGet, adminPost, adminPut, adminDel } from '@/utils/request'
import type { PageQuery, PageResult } from '@/types/api'
export interface AdminGallery { id:number; name:string; description?:string; cover?:string; shootDate?:string; sort:number; photoCount:number; createTime:string }
export function getAdminGalleryPage(p:PageQuery & {keyword?:string}){return adminGet<PageResult<AdminGallery>>('/galleries',p)}
export function createGallery(p:Partial<AdminGallery>){return adminPost<number>('/galleries',p)}
export function updateGallery(id:number,p:Partial<AdminGallery>){return adminPut<void>(`/galleries/${id}`,p)}
export function deleteGallery(id:number){return adminDel<void>(`/galleries/${id}`)}
export function listGalleryPhotos(id:number){return adminGet<any[]>(`/galleries/${id}/photos`)}
export function uploadGalleryPhotos(id:number,files:File[]){const data=new FormData();files.forEach(f=>data.append('files',f));return adminPost<any>(`/galleries/${id}/photos`,data)}
export function deletePhoto(id:number){return adminDel<void>(`/photos/${id}`)}
