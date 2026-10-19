import type { RouteRecordRaw } from 'vue-router'

/**
 * 相册管理路由(对应 admin-开发任务书 M7 相册管理)
 * 占位页面, 待后端 M7 完成后接入
 */
const routes: RouteRecordRaw[] = [
  {
    path: 'gallery',
    name: 'Gallery',
    redirect: '/admin/gallery/list',
    meta: { title: '相册管理', icon: 'Picture' },
    children: [
      {
        path: 'list',
        name: 'GalleryList',
        component: () => import('@/views/gallery/GalleryListView.vue'),
        meta: { title: '相册列表' }
      }
    ]
  }
]

export default routes
