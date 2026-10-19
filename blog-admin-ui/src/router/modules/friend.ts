import type { RouteRecordRaw } from 'vue-router'

/**
 * 友链管理路由(对应 admin-开发任务书 M6 友链管理)
 * 占位页面, 待后端 M6 完成后接入
 */
const routes: RouteRecordRaw[] = [
  {
    path: 'friend',
    name: 'Friend',
    component: () => import('@/views/friend/FriendListView.vue'),
    meta: { title: '友链管理', icon: 'Link' }
  }
]

export default routes
