<script setup lang="ts">
import { ref } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import Navbar from '@/components/Navbar.vue'
import TagsView from '@/components/TagsView.vue'

// 侧边栏折叠状态(提升到 layout 层, navbar 切换 + sidebar 响应)
const isCollapse = ref(false)

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}
</script>

<template>
  <div class="admin-layout">
    <Sidebar :is-collapse="isCollapse" />
    <div class="layout-container" :class="{ 'is-collapse': isCollapse }">
      <Navbar :is-collapse="isCollapse" @toggle-sidebar="toggleSidebar" />
      <TagsView />
      <main class="app-main">
        <RouterView v-slot="{ Component, route }">
          <keep-alive>
            <component :is="Component" :key="route.fullPath" />
          </keep-alive>
        </RouterView>
      </main>
    </div>
  </div>
</template>

<style scoped lang="scss">
.admin-layout {
  display: flex;
  width: 100%;
  height: 100%;
}

.layout-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  width: calc(100% - var(--admin-sidebar-width));
  margin-left: var(--admin-sidebar-width);
  transition: width 0.28s, margin-left 0.28s;

  &.is-collapse {
    width: calc(100% - var(--admin-sidebar-collapse-width));
    margin-left: var(--admin-sidebar-collapse-width);
  }
}
</style>
