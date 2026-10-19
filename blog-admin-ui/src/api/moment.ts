import { adminGet, adminPost, adminPut, adminDel } from '@/utils/request'
import type { PageQuery, PageResult } from '@/types/api'
export interface AdminMoment { id:number; content:string; images?:string[]; location?:string; mood?:string; visible:number; likeCount:number; commentCount:number; createTime:string }
export function getAdminMomentPage(p:PageQuery){return adminGet<PageResult<AdminMoment>>('/moments',p)}
export function createMoment(p:Partial<AdminMoment>){return adminPost<{id:number}>('/moments',p)}
export function updateMoment(id:number,p:Partial<AdminMoment>){return adminPut<void>(`/moments/${id}`,p)}
export function deleteMoment(id:number){return adminDel<void>(`/moments/${id}`)}
