import { adminGet, adminPost, adminPut, adminDel } from '@/utils/request'
import type { PageQuery, PageResult } from '@/types/api'
export interface AdminFriend { id:number; name:string; url:string; avatar?:string; description?:string; group?:string; email?:string; status:number; sort:number; createTime:string }
export interface FriendQuery extends PageQuery { status?:number }
export function getAdminFriendPage(params: FriendQuery) { return adminGet<PageResult<AdminFriend>>('/friends', params) }
export function createFriend(data: Partial<AdminFriend>) { return adminPost<number>('/friends', data) }
export function updateFriend(id:number, data: Partial<AdminFriend>) { return adminPut<void>(`/friends/${id}`, data) }
export function deleteFriend(id:number) { return adminDel<void>(`/friends/${id}`) }
export function auditFriend(id:number, status:number) { return adminPut<void>(`/friends/${id}/audit`, { status }) }
export function updateFriendSort(id:number, sort:number) { return adminPut<void>(`/friends/${id}/sort`, { sort }) }
