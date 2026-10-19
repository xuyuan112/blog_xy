import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: 'setting', name: 'Setting', component: () => import('@/views/setting/SiteSettingView.vue'), meta: { title: '站点配置', icon: 'Setting' } },
  { path: 'about', name: 'About', component: () => import('@/views/setting/AboutView.vue'), meta: { title: '关于我', icon: 'User' } },
  { path: 'schedule', name: 'Schedule', component: () => import('@/views/setting/ScheduleListView.vue'), meta: { title: '日程管理', icon: 'Calendar' } }
]

export default routes
