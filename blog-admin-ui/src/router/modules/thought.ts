import type { RouteRecordRaw } from 'vue-router'

/**
 * 杂谈管理路由
 * 杂谈本质是 type=2 的文章，后端复用文章管理接口
 */
const routes: RouteRecordRaw[] = [
  {
    path: 'thought',
    name: 'Thought',
    redirect: '/admin/thought/list',
    meta: { title: '杂谈管理', icon: 'Reading' },
    children: [
      {
        path: 'list',
        name: 'ThoughtList',
        component: () => import('@/views/thought/ThoughtListView.vue'),
        meta: { title: '杂谈列表' }
      },
      {
        path: 'edit/:id?',
        name: 'ThoughtEdit',
        component: () => import('@/views/thought/ThoughtEditView.vue'),
        meta: { title: '杂谈编辑', hidden: true }
      }
    ]
  }
]

export default routes
