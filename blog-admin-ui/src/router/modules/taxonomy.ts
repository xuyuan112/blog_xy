import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  { path: 'category', name: 'Category', component: () => import('@/views/taxonomy/CategoryListView.vue'), meta: { title: '分类管理', icon: 'Collection' } },
  { path: 'tag', name: 'Tag', component: () => import('@/views/taxonomy/TagListView.vue'), meta: { title: '标签管理', icon: 'PriceTag' } }
]

export default routes
