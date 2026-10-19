import type { RouteRecordRaw } from 'vue-router'

/**
 * 用户管理路由(对应 admin-开发任务书 M10 前台用户管理)
 * 占位页面, 待后端 M10 完成后接入
 */
const routes: RouteRecordRaw[] = [
  {
    path: 'user',
    name: 'User',
    component: () => import('@/views/user/UserListView.vue'),
    meta: { title: '用户管理', icon: 'UserFilled' }
  }
]

export default routes
