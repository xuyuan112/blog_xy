<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

interface Props {
  isCollapse: boolean
}

const props = defineProps<Props>()
const route = useRoute()

// 菜单配置(与 router/modules/* 一一对应)
// path 与路由声明保持一致, 含 /admin 前缀
interface MenuItem {
  path: string
  title: string
  icon?: string
  children?: { path: string; title: string }[]
}

const menuItems = computed<MenuItem[]>(() => [
  {
    path: '/admin/home/dashboard',
    title: '仪表盘',
    icon: 'Odometer'
  },
  {
    path: '/admin/article',
    title: '文章管理',
    icon: 'Document',
    children: [
      { path: '/admin/article/list', title: '文章列表' },
      { path: '/admin/article/edit', title: '新建文章' }
    ]
  },
  {
    path: '/admin/thought',
    title: '杂谈管理',
    icon: 'Reading',
    children: [
      { path: '/admin/thought/list', title: '杂谈列表' },
      { path: '/admin/thought/edit', title: '新建杂谈' }
    ]
  },
  {
    path: '/admin/moment',
    title: '说说管理',
    icon: 'ChatLineRound'
  },
  {
    path: '/admin/comment',
    title: '评论管理',
    icon: 'ChatDotRound'
  },
  {
    path: '/admin/friend',
    title: '友链管理',
    icon: 'Link'
  },
  {
    path: '/admin/gallery',
    title: '相册管理',
    icon: 'Picture',
    children: [{ path: '/admin/gallery/list', title: '相册列表' }]
  },
  {
    path: '/admin/user',
    title: '用户管理',
    icon: 'UserFilled'
  },
  {
    path: '/admin/category',
    title: '分类管理',
    icon: 'Collection'
  },
  {
    path: '/admin/tag',
    title: '标签管理',
    icon: 'PriceTag'
  },
  { path: '/admin/notice', title: '公告管理', icon: 'Bell' },
  { path: '/admin/word', title: '一言管理', icon: 'ChatDotRound' },
  { path: '/admin/about', title: '关于我', icon: 'User' },
  { path: '/admin/schedule', title: '日程管理', icon: 'Calendar' },
  {
    path: '/admin/setting',
    title: '站点设置',
    icon: 'Setting'
  }
])

// 当前激活菜单: 优先精确匹配, 其次匹配最长前缀(用于编辑等隐藏子路由)
const activeMenu = computed(() => {
  const path = route.path
  // 精确匹配已声明菜单
  const flat = menuItems.value.flatMap((m) => [m, ...(m.children || [])])
  const exact = flat.find((m) => m.path === path)
  if (exact) return exact.path
  // 前缀匹配(如 /admin/article/edit/123 命中 /admin/article)
  const prefix = menuItems.value
    .filter((m) => path.startsWith(m.path))
    .sort((a, b) => b.path.length - a.path.length)[0]
  return prefix?.path || path
})

// 默认展开的子菜单
const defaultOpeneds = computed(() => menuItems.value.filter((m) => m.children).map((m) => m.path))
</script>

<template>
  <aside class="sidebar" :class="{ 'is-collapse': props.isCollapse }">
    <div class="logo">
      <span v-if="!props.isCollapse" class="logo-text">Xuyuan Admin</span>
      <span v-else class="logo-text-mini">XA</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      :default-openeds="defaultOpeneds"
      :collapse="props.isCollapse"
      :collapse-transition="false"
      class="sidebar-menu"
      background-color="#001529"
      text-color="#b7bdc7"
      active-text-color="#ffffff"
      router
    >
      <template v-for="item in menuItems" :key="item.path">
        <el-sub-menu v-if="item.children?.length" :index="item.path">
          <template #title>
            <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </template>
          <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
            {{ child.title }}
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item v-else :index="item.path">
          <el-icon v-if="item.icon"><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </template>
    </el-menu>
  </aside>
</template>

<style scoped lang="scss">
.sidebar {
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  width: var(--admin-sidebar-width);
  background: var(--admin-sidebar-bg);
  transition: width 0.28s;
  z-index: 1001;
  overflow: hidden;

  &.is-collapse {
    width: var(--admin-sidebar-collapse-width);
  }
}

.logo {
  height: var(--admin-navbar-height);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  background: #002140;
}

.logo-text {
  font-size: 18px;
}

.logo-text-mini {
  font-size: 18px;
}

.sidebar-menu {
  border-right: none;
  height: calc(100% - var(--admin-navbar-height));
  overflow-y: auto;
  overflow-x: hidden;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: var(--admin-sidebar-width);
}
</style>
