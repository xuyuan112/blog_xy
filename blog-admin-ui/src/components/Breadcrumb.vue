<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// 基于 route.matched 过滤出有 title 的路由层级
const breadcrumbs = computed(() =>
  route.matched.filter((r) => r.meta?.title && !r.meta?.hidden).map((r) => r.meta?.title as string)
)
</script>

<template>
  <el-breadcrumb class="breadcrumb" separator="/">
    <el-breadcrumb-item v-for="(item, idx) in breadcrumbs" :key="idx">
      {{ item }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<style scoped lang="scss">
.breadcrumb {
  display: inline-flex;
  align-items: center;
  font-size: 14px;
}
</style>
