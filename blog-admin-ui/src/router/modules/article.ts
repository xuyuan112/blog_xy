import type { RouteRecordRaw } from 'vue-router'

/**
 * 文章管理路由(对应 admin-开发任务书 M3 文章管理)
 */
const routes: RouteRecordRaw[] = [
  {
    path: 'article',
    name: 'Article',
    redirect: '/admin/article/list',
    meta: { title: '文章管理', icon: 'Document' },
    children: [
      {
        path: 'list',
        name: 'ArticleList',
        component: () => import('@/views/article/ArticleListView.vue'),
        meta: { title: '文章列表' }
      },
      {
        path: 'edit/:id?',
        name: 'ArticleEdit',
        component: () => import('@/views/article/ArticleEditView.vue'),
        meta: { title: '文章编辑', hidden: true }
      }
    ]
  }
]

export default routes
