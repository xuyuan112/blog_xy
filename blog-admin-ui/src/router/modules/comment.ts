import type { RouteRecordRaw } from 'vue-router'

/**
 * 评论管理路由(对应 admin-开发任务书 M5 评论管理)
 * 占位页面, 待后端 M5 完成后接入
 */
const routes: RouteRecordRaw[] = [
  {
    path: 'comment',
    name: 'Comment',
    component: () => import('@/views/comment/CommentListView.vue'),
    meta: { title: '评论管理', icon: 'ChatDotRound' }
  }
]

export default routes
