/** 统一响应结构 */
export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
  pages: number
}

/** 分页查询参数 */
export interface PageQuery {
  page?: number
  pageSize?: number
}

/** 文章状态 */
export enum ArticleStatus {
  DRAFT = 0,
  PUBLISHED = 1,
  PRIVATE = 2,
  RECYCLE = 3
}

/** 内容类型 */
export enum ContentType {
  ARTICLE = 1,
  THOUGHT = 2
}
