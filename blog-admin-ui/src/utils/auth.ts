/**
 * JWT Token 本地存储工具
 *
 * <p>供请求拦截器与路由守卫使用, 与 Pinia user store 的持久化保持一致。
 * store 与 localStorage 双写, 是为了让 axios 拦截器在 store 实例化前也能读取到 token。</p>
 */

const ACCESS_TOKEN_KEY = 'accessToken'
const REFRESH_TOKEN_KEY = 'refreshToken'

export function getAccessToken(): string {
  return localStorage.getItem(ACCESS_TOKEN_KEY) || ''
}

export function getRefreshToken(): string {
  return localStorage.getItem(REFRESH_TOKEN_KEY) || ''
}

export function setTokens(accessToken: string, refreshToken: string): void {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken)
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
}

export function clearTokens(): void {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}
