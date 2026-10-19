import router from '@/router'
import { useUserStore } from '@/stores/user'

/**
 * 全局路由守卫
 *
 * <p>职责:
 * <ul>
 *   <li>未登录访问受保护路由 -> 跳 /login 并记录 redirect 参数</li>
 *   <li>已登录访问 /login -> 跳首页</li>
 *   <li>设置 document.title</li>
 * </ul>
 */
const WHITE_LIST = ['/login']

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  const title = to.meta?.title as string | undefined
  document.title = title ? `${title} - 后台管理系统` : '后台管理系统'

  if (userStore.accessToken) {
    if (to.path === '/login') {
      next('/admin/home/dashboard')
    } else {
      next()
    }
  } else {
    if (WHITE_LIST.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    }
  }
})

export default router
