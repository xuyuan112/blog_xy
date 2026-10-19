import type { RouteRecordRaw } from 'vue-router'

/**
 * 仪表盘路由(对应 admin-开发任务书 M2 仪表盘)
 */
const routes: RouteRecordRaw[] = [
  {
    path: 'home',
    redirect: '/admin/home/dashboard'
  },
  {
    path: 'home/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/home/DashboardView.vue'),
    meta: { title: '仪表盘', icon: 'Odometer' }
  }
]

export default routes
