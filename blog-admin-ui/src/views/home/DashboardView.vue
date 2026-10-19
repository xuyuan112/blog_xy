<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'
import { Document, ChatDotRound, Picture, View, Warning } from '@element-plus/icons-vue'
import { getDashboardStats, getTrend, type DashboardStats, type DashboardTrendPoint } from '@/api/admin'

const stats = ref<DashboardStats | null>(null)
const trendData = ref<DashboardTrendPoint[]>([])
const loading = ref(false)
const loadError = ref('')

const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

const cards = ref([
  { key: 'articleCount', label: '文章总数', value: 0, icon: Document, color: '#409eff' },
  { key: 'momentCount', label: '说说总数', value: 0, icon: ChatDotRound, color: '#67c23a' },
  { key: 'totalViewCount', label: '累计访问', value: 0, icon: View, color: '#e6a23c' },
  { key: 'pendingCommentCount', label: '待审评论', value: 0, icon: Warning, color: '#f56c6c' }
])

async function loadData() {
  loading.value = true
  loadError.value = ''
  try {
    const [statsRes, trendRes] = await Promise.all([
      getDashboardStats(),
      getTrend(30)
    ])
    stats.value = statsRes
    trendData.value = trendRes
    cards.value.forEach((c) => {
      const v = (statsRes as unknown as Record<string, number>)[c.key]
      c.value = typeof v === 'number' ? v : 0
    })
    renderChart()
  } catch (error) {
    // 后端 M1 未就绪时使用 mock 数据展示 UI
    stats.value = null
    trendData.value = []
    cards.value.forEach((c) => { c.value = 0 })
    loadError.value = error instanceof Error ? error.message : '无法加载后台统计数据'
    renderChart()
  } finally {
    loading.value = false
  }
}

function renderChart() {
  if (!chartRef.value) return
  if (!chart) {
    chart = echarts.init(chartRef.value)
  }
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['访问量', '评论数'], bottom: 0 },
    grid: { top: 30, left: 50, right: 30, bottom: 50 },
    xAxis: {
      type: 'category',
      data: trendData.value.map((d) => d.date),
      axisLabel: { fontSize: 10 }
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '访问量',
        type: 'line',
        smooth: true,
        data: trendData.value.map((d) => d.viewCount),
        itemStyle: { color: '#409eff' }
      },
      {
        name: '评论数',
        type: 'line',
        smooth: true,
        data: trendData.value.map((d) => d.commentCount),
        itemStyle: { color: '#67c23a' }
      }
    ]
  })
}

// mock 数据: 后端 M1 未就绪时占位
function mockStats(): DashboardStats {
  return {
    articleCount: 0,
    momentCount: 0,
    galleryCount: 0,
    photoCount: 0,
    friendCount: 0,
    commentCount: 0,
    totalViewCount: 0,
    pendingFriendCount: 0,
    pendingCommentCount: 0
  }
}

function mockTrend(): DashboardTrendPoint[] {
  const today = new Date()
  return Array.from({ length: 30 }).map((_, i) => {
    const d = new Date(today)
    d.setDate(d.getDate() - (29 - i))
    return {
      date: `${d.getMonth() + 1}-${d.getDate()}`,
      viewCount: 0,
      commentCount: 0
    }
  })
}

const handleResize = () => chart?.resize()

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
  chart = null
})

watch(trendData, renderChart, { deep: true })
</script>

<template>
  <div v-loading="loading" class="dashboard">
    <el-alert v-if="loadError" :title="loadError" type="error" show-icon :closable="false" class="load-error" />
    <!-- 顶部统计卡片 -->
    <el-row :gutter="16" class="card-row">
      <el-col v-for="card in cards" :key="card.key" :span="6">
        <div class="stat-card">
          <div class="stat-icon" :style="{ background: card.color }">
            <el-icon :size="28"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 趋势图 -->
    <el-card class="trend-card" shadow="never">
      <template #header>
        <span>近 30 天访问趋势</span>
      </template>
      <div ref="chartRef" class="trend-chart" />
    </el-card>

    <!-- 内容概览 -->
    <el-row :gutter="16" class="card-row">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>内容概览</template>
          <div class="overview-list">
            <div class="overview-item"><span>相册数</span><b>{{ stats?.galleryCount ?? 0 }}</b></div>
            <div class="overview-item"><span>照片数</span><b>{{ stats?.photoCount ?? 0 }}</b></div>
            <div class="overview-item"><span>友链数</span><b>{{ stats?.friendCount ?? 0 }}</b></div>
            <div class="overview-item"><span>评论数</span><b>{{ stats?.commentCount ?? 0 }}</b></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>待办事项</template>
          <div class="overview-list">
            <div class="overview-item"><span>待审友链</span><b>{{ stats?.pendingFriendCount ?? 0 }}</b></div>
            <div class="overview-item"><span>待审评论</span><b>{{ stats?.pendingCommentCount ?? 0 }}</b></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.dashboard {
  padding: 16px;
}

.load-error {
  margin-bottom: 16px;
}

.card-row {
  margin-bottom: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--admin-text);
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: var(--admin-text-secondary);
  margin-top: 4px;
}

.trend-card {
  margin-bottom: 16px;
}

.trend-chart {
  height: 320px;
}

.overview-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.overview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px dashed var(--admin-border);

  &:last-child {
    border-bottom: none;
  }

  span {
    color: var(--admin-text-secondary);
    font-size: 14px;
  }

  b {
    font-size: 16px;
    color: var(--admin-text);
  }
}
</style>
