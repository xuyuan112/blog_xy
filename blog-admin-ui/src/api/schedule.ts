import { adminGet, adminPost, adminPut, adminDel } from '@/utils/request'
import type { PageResult } from '@/types/api'
export interface AdminSchedule { id:number; title:string; targetTime:string; type:string; createTime:string }
export function getSchedulePage(params:{page:number;pageSize:number;type?:string}){return adminGet<PageResult<AdminSchedule>>('/schedules',params)}
export function createSchedule(data:Partial<AdminSchedule>){return adminPost<{id:number}>('/schedules',data)}
export function updateSchedule(id:number,data:Partial<AdminSchedule>){return adminPut<void>(`/schedules/${id}`,data)}
export function deleteSchedule(id:number){return adminDel<void>(`/schedules/${id}`)}
