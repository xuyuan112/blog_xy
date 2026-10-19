/**
 * 评论管理 API 占位 (对应 admin-开发任务书 M5 评论管理)
 *
 * <p>待后端 M5 完成后按以下契约补全:
 * <ul>
 *   <li>GET    /xuyuan/admin/comments             评论分页列表</li>
 *   <li>PUT    /xuyuan/admin/comments/{id}/audit  审核评论(通过/隐藏)</li>
 *   <li>DELETE /xuyuan/admin/comments/{id}        删除评论</li>
 * </ul>
 */
import { adminGet, adminPut, adminDel } from '@/utils/request'
import type { PageQuery, PageResult } from '@/types/api'

export interface AdminComment { id:number; targetType:number; targetId:number; targetTitle?:string; content:string; nickname:string; avatar?:string; email?:string; website?:string; status:number; createTime:string; updateTime?:string }
export interface CommentQuery extends PageQuery { targetType?: number; status?: number }
export function getAdminCommentPage(params: CommentQuery) { return adminGet<PageResult<AdminComment>>('/comments', params) }
export function changeCommentStatus(id:number, status:number) { return adminPut<void>(`/comments/${id}/status`, { status }) }
export function deleteComment(id:number) { return adminDel<void>(`/comments/${id}`) }
