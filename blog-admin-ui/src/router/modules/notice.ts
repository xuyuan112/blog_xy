import type { RouteRecordRaw } from 'vue-router'
const routes: RouteRecordRaw[] = [
  { path: 'notice', name: 'Notice', component: () => import('@/views/notice/NoticeListView.vue'), meta: { title: '公告管理', icon: 'Bell' } },
  { path: 'word', name: 'Word', component: () => import('@/views/notice/WordListView.vue'), meta: { title: '一言管理', icon: 'ChatDotRound' } }
]
export default routes
