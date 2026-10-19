import { adminGet, adminPost, adminPut, adminDel } from '@/utils/request'
import type { PageResult } from '@/types/api'

export interface AdminWord { id: number; content: string; source?: string; createTime: string }
export function getWordPage(params: { page: number; pageSize: number }) { return adminGet<PageResult<AdminWord>>('/words', params) }
export function createWord(content: string, source?: string) { return adminPost<{ id: number }>('/words', { content, source }) }
export function updateWord(id: number, content: string, source?: string) { return adminPut<void>(`/words/${id}`, { content, source }) }
export function deleteWord(id: number) { return adminDel<void>(`/words/${id}`) }
