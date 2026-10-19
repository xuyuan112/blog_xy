<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useRouteStore } from '@/stores/route'
import { Close } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const routeStore = useRouteStore()

// 当前激活的标签 path
const activePath = ref(route.path)

watch(
  () => route.path,
  (newPath) => {
    activePath.value = newPath
    if (route.meta?.title && !route.meta?.hidden) {
      routeStore.addTag({
        path: newPath,
        title: route.meta.title as string,
        name: route.name as string
      })
    }
  },
  { immediate: true }
)

const handleClick = (path: string) => {
  router.push(path)
}

const handleClose = (path: string) => {
  const isCurrent = path === activePath.value
  routeStore.removeTag(path)
  if (isCurrent) {
    const remaining = routeStore.tags
    if (remaining.length) {
      router.push(remaining[remaining.length - 1].path)
    } else {
      router.push('/admin/home/dashboard')
    }
  }
}

const handleCloseOthers = () => {
  routeStore.closeOthers(activePath.value)
}

const handleCloseAll = () => {
  routeStore.closeAll()
  router.push('/admin/home/dashboard')
}
</script>

<template>
  <div class="tags-view">
    <div class="tags-scroll">
      <div
        v-for="tag in routeStore.tags"
        :key="tag.path"
        class="tag-item"
        :class="{ active: tag.path === activePath }"
        @click="handleClick(tag.path)"
      >
        {{ tag.title }}
        <el-icon class="close-icon" @click.stop="handleClose(tag.path)">
          <Close />
        </el-icon>
      </div>
    </div>
    <div class="tags-actions">
      <el-dropdown trigger="click" @command="(cmd: string) => cmd === 'others' ? handleCloseOthers() : handleCloseAll()">
        <span class="action-trigger">操作<el-icon><ArrowDown /></el-icon></span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="others">关闭其他</el-dropdown-item>
            <el-dropdown-item command="all">关闭全部</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped lang="scss">
.tags-view {
  height: var(--admin-tagsview-height);
  display: flex;
  align-items: center;
  background: var(--admin-navbar-bg);
  border-bottom: 1px solid var(--admin-border);
  padding: 0 8px;
}

.tags-scroll {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 4px;
  overflow-x: auto;
  overflow-y: hidden;
  height: 100%;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  font-size: 12px;
  color: var(--admin-text);
  background: #fff;
  border: 1px solid var(--admin-border);
  border-radius: 3px;
  cursor: pointer;
  white-space: nowrap;
  height: 26px;

  &:hover {
    color: var(--admin-primary);
  }

  &.active {
    color: #fff;
    background: var(--admin-primary);
    border-color: var(--admin-primary);
  }
}

.close-icon {
  font-size: 12px;
  border-radius: 50%;

  &:hover {
    background: rgba(0, 0, 0, 0.2);
    color: #fff;
  }
}

.tags-actions {
  margin-left: 8px;
}

.action-trigger {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--admin-text);
}
</style>
