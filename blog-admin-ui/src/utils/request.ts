import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResult } from '@/types/api'

const clientType = 'admin'
// In production the admin UI may be hosted separately from blog-web-admin.
// Keep the relative URL default so the existing same-origin proxy continues to work.
const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')

function createService(baseURL: string): AxiosInstance {
  const service = axios.create({ baseURL: `${apiBaseUrl}${baseURL}`, timeout: 15000 })

  // 请求拦截器：注入 JWT Bearer token 与客户端类型标识
  service.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('accessToken')
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      config.headers['X-Client-Type'] = clientType
      return config
    },
    (error) => Promise.reject(error)
  )

  // 响应拦截器：解包业务 data，按业务码处理鉴权失败
  service.interceptors.response.use(
    (response: AxiosResponse<ApiResult>) => {
      const { code, message, data } = response.data
      if (code === 200) {
        return data as never
      }
      // 601 = token 无效, 602 = token 过期, 401 = 未认证
      if (code === 401 || code === 601 || code === 602) {
        clearTokensAndRedirect()
        ElMessage.error(message || '登录已过期，请重新登录')
        return Promise.reject(new Error(message))
      }
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    },
    (error) => {
      const status = error.response?.status
      if (status === 429) {
        ElMessage.error('操作过于频繁，请稍后再试')
      } else {
        ElMessage.error(error.message || '网络错误')
      }
      return Promise.reject(error)
    }
  )

  return service
}

/** 后台管理端服务 (blog-web-admin), 前缀 /xuyuan/admin */
export const adminService = createService('/xuyuan/admin')

/** 认证服务 (blog-web-admin), 前缀 /xuyuan/auth */
export const authService = createService('/xuyuan/auth')

/** 泛型请求方法, 返回值已解包为业务 data */
export function request<T = unknown>(service: AxiosInstance, config: AxiosRequestConfig): Promise<T> {
  return service.request(config) as Promise<T>
}

// ---- 后台管理快捷方法 (统一前缀 /xuyuan/admin) ----
export function adminGet<T = unknown>(url: string, params?: object): Promise<T> {
  return request<T>(adminService, { method: 'get', url, params })
}

export function adminPost<T = unknown>(url: string, data?: object): Promise<T> {
  return request<T>(adminService, { method: 'post', url, data })
}

export function adminPut<T = unknown>(url: string, data?: object): Promise<T> {
  return request<T>(adminService, { method: 'put', url, data })
}

export function adminDel<T = unknown>(url: string, data?: object): Promise<T> {
  return request<T>(adminService, { method: 'delete', url, data })
}

// ---- 认证快捷方法 (统一前缀 /xuyuan/auth) ----
export function authGet<T = unknown>(url: string, params?: object): Promise<T> {
  return request<T>(authService, { method: 'get', url, params })
}

export function authPost<T = unknown>(url: string, data?: object): Promise<T> {
  return request<T>(authService, { method: 'post', url, data })
}

/** 清理 token 并跳转登录页 (401/601/602 触发) */
function clearTokensAndRedirect() {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  if (location.hash !== '#/login') {
    location.hash = '#/login'
  }
}

export default adminService
