import { adminGet, adminPut, adminDel } from '@/utils/request'
import type { PageResult } from '@/types/api'
export interface AppUserAdmin { id:number; username:string; nickname:string; avatar?:string; email?:string; status:number; lastLoginAt?:string; createTime:string }
export interface AppUserQuery { current?:number; size?:number; username?:string; nickname?:string; status?:number }
export function getAppUserPage(params:AppUserQuery){return adminGet<PageResult<AppUserAdmin>>('/users',params)}
export function changeAppUserStatus(id:number,status:number){return adminPut<void>(`/users/${id}/status`,{status})}
export function resetAppUserPassword(id:number,newPassword:string){return adminPut<void>(`/users/${id}/reset-password`,{newPassword})}
export function deleteAppUser(id:number){return adminDel<void>(`/users/${id}`)}
