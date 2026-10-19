<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Expand, Fold, ArrowDown, SwitchButton, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { logout as logoutApi } from '@/api/auth'
import Breadcrumb from '@/components/Breadcrumb.vue'

interface Props {
  isCollapse: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{ (e: 'toggle-sidebar'): void }>()

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const avatar = computed(() => userStore.userInfo?.avatar || '')
const nickname = computed(() => userStore.userInfo?.nickname || userStore.userInfo?.username || '管理员')

const handleToggle = () => emit('toggle-sidebar')

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      try {
        await logoutApi()
      } catch {
        // 即使后端登出失败也清理本地态
      }
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch {
      // 用户取消
    }
  } else if (command === 'profile') {
    ElMessage.info('个人中心待开发')
  }
}
</script>

<template>
  <header class="navbar">
    <div class="navbar-left">
      <el-icon class="collapse-btn" :size="20" @click="handleToggle">
        <Fold v-if="!props.isCollapse" />
        <Expand v-else />
      </el-icon>
      <Breadcrumb />
    </div>
    <div class="navbar-right">
      <el-dropdown @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" :src="avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <span class="username">{{ nickname }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile" :icon="User">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" :icon="SwitchButton" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<style scoped lang="scss">
.navbar {
  height: var(--admin-navbar-height);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: var(--admin-navbar-bg);
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 100;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  cursor: pointer;
  color: var(--admin-text);

  &:hover {
    color: var(--admin-primary);
  }
}

.navbar-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 0 8px;
  height: var(--admin-navbar-height);
}

.username {
  font-size: 14px;
  color: var(--admin-text);
}
</style>
