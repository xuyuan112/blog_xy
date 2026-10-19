import type { RouteRecordRaw } from 'vue-router'

/**
 * 说说管理路由(对应 admin-开发任务书 M4 说说管理)
 * 占位页面, 待后端 M4 完成后接入
 */
const routes: RouteRecordRaw[] = [
  {
    path: 'moment',
    name: 'Moment',
    component: () => import('@/views/moment/MomentListView.vue'),
    meta: { title: '说说管理', icon: 'ChatLineRound' }
  }
]

export default routes
