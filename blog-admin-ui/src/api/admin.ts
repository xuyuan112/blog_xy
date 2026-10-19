import { adminGet, adminPost, adminPut, adminDel } from '@/utils/request'
import type { PageQuery, PageResult } from '@/types/api'

/** 后台文章列表项 (对应 API_SPEC admin 端文章管理响应) */
export interface AdminArticle {
  id: number
  title: string
  cover: string
  summary: string
  categoryId: number
  categoryName: string
  tags: { id: number; name: string }[]
  viewCount: number
  commentCount: number
  likeCount: number
  type: number
  isTop: boolean
  publishedAt: string
  status: number
  isPrivate: boolean
  tagIds: number[]
  createTime: string
  updateTime: string
}

/** 后台文章新增/编辑参数 */
export interface ArticleSaveParam {
  title: string
  cover?: string
  summary?: string
  content: string
  categoryId?: number
  tagIds?: number[]
  type: number
  status: number
  isTop?: boolean
}

/** 仪表盘统计 (API_SPEC /xuyuan/admin/dashboard/stats) */
export interface DashboardStats {
  articleCount: number
  momentCount: number
  galleryCount: number
  photoCount: number
  friendCount: number
  commentCount: number
  totalViewCount: number
  pendingFriendCount: number
  pendingCommentCount: number
}

/** 仪表盘趋势点 */
export interface DashboardTrendPoint {
  date: string
  viewCount: number
  commentCount: number
}

// ---- 文章管理 (API_SPEC 第 13 节) ----

export function getAdminArticlePage(
  params: PageQuery & { status?: number; type?: number; keyword?: string }
) {
  return adminGet<PageResult<AdminArticle>>('/articles', params)
}

export function getAdminArticleDetail(id: number) {
  return adminGet<AdminArticle & { content: string }>(`/articles/${id}`)
}

export function createArticle(data: ArticleSaveParam) {
  return adminPost<{ id: number }>('/articles', data)
}

export function updateArticle(id: number, data: ArticleSaveParam) {
  return adminPut<void>(`/articles/${id}`, data)
}

export function deleteArticles(ids: number[]) {
  return adminDel<void>('/articles', { ids })
}

export function changeArticleStatus(id: number, data: { status: number; isTop?: boolean }) {
  return adminPut<void>(`/articles/${id}/status`, data)
}

// ---- 仪表盘 (API_SPEC admin 端仪表盘) ----

export function getDashboardStats() {
  return adminGet<DashboardStats>('/dashboard/stats')
}

export function getTrend(days = 30) {
  return adminGet<DashboardTrendPoint[]>('/dashboard/trend', { days })
}

export function getSiteConfigs() {
  return adminGet<Record<string, string>>('/configs')
}

export function updateSiteConfigs(configs: Record<string, string>) {
  return adminPut<void>('/configs', configs)
}
