import type { RouteRecordRaw } from 'vue-router'

/**
 * 登录路由(无需鉴权, 不挂 AdminLayout)
 */
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录', hidden: true }
  }
]

export default routes
