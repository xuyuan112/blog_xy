import { adminGet, adminPost, adminPut, adminDel } from '@/utils/request'
import type { PageResult } from '@/types/api'

export interface AdminNotice { id: number; content: string; createTime: string; updateTime?: string }
export function getNoticePage(params: { page: number; pageSize: number }) { return adminGet<PageResult<AdminNotice>>('/notices', params) }
export function createNotice(content: string) { return adminPost<{ id: number }>('/notices', { content }) }
export function updateNotice(id: number, content: string) { return adminPut<void>(`/notices/${id}`, { content }) }
export function deleteNotice(id: number) { return adminDel<void>(`/notices/${id}`) }
