import { authPost, authGet } from '@/utils/request'

export interface CaptchaResult {
  captchaKey: string
  captchaImage: string
}

export interface LoginResult {
  accessToken: string
  refreshToken: string
  expiresIn: number
  tokenType: string
  userInfo: {
    userId: number
    username: string
    nickname: string
    avatar: string
  }
}

export interface RefreshResult {
  accessToken: string
  refreshToken: string
  expiresIn: number
  tokenType: string
}

export interface UserInfoResult {
  userId: number
  username: string
  nickname: string
  avatar: string
  roles: string[]
}

/** 获取图形验证码 (API_SPEC /xuyuan/auth/captcha) */
export function getCaptcha() {
  return authGet<CaptchaResult>('/captcha')
}

/** 登录 (API_SPEC /xuyuan/auth/login) */
export function login(data: {
  username: string
  password: string
  captchaKey: string
  captchaCode: string
}) {
  return authPost<LoginResult>('/login', data)
}

/** 刷新 Token (API_SPEC /xuyuan/auth/refresh) */
export function refreshToken(refreshToken: string) {
  return authPost<RefreshResult>('/refresh', { refreshToken })
}

/** 登出 (API_SPEC /xuyuan/auth/logout) */
export function logout() {
  return authPost<void>('/logout')
}

/** 获取当前用户信息 (API_SPEC /xuyuan/auth/userinfo) */
export function getUserInfo() {
  return authGet<UserInfoResult>('/userinfo')
}
