import { createRouter, createWebHashHistory, type RouteRecordRaw } from 'vue-router'
import loginRoutes from './modules/login'
import homeRoutes from './modules/home'
import articleRoutes from './modules/article'
import commentRoutes from './modules/comment'
import friendRoutes from './modules/friend'
import galleryRoutes from './modules/gallery'
import momentRoutes from './modules/moment'
import userRoutes from './modules/user'
import settingRoutes from './modules/setting'
import taxonomyRoutes from './modules/taxonomy'
import noticeRoutes from './modules/notice'
import thoughtRoutes from './modules/thought'

// 业务路由(均挂在 /admin 下, 使用 AdminLayout)
const adminRoutes: RouteRecordRaw = {
  path: '/admin',
  component: () => import('@/layouts/AdminLayout.vue'),
  redirect: '/admin/home/dashboard',
  children: [
    ...homeRoutes,
    ...articleRoutes,
    ...commentRoutes,
    ...friendRoutes,
    ...galleryRoutes,
    ...momentRoutes,
    ...userRoutes,
    ...settingRoutes
    , ...taxonomyRoutes, ...noticeRoutes, ...thoughtRoutes
  ]
}

const routes: RouteRecordRaw[] = [
  // 登录页独立路由(无 layout)
  ...loginRoutes,
  adminRoutes,
  // 兜底
  {
    path: '/',
    redirect: '/admin/home/dashboard'
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFoundView.vue'),
    meta: { title: '404', hidden: true }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

export default router
