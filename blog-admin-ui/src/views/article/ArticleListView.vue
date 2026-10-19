<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Delete } from '@element-plus/icons-vue'
import {
  getAdminArticlePage,
  deleteArticles,
  changeArticleStatus,
  type AdminArticle
} from '@/api/admin'
import type { PageQuery, PageResult } from '@/types/api'

const router = useRouter()

const loading = ref(false)
const loadError = ref('')
const tableData = ref<AdminArticle[]>([])
const total = ref(0)
const selected = ref<AdminArticle[]>([])

const query = reactive<PageQuery & { status?: number; type?: number; keyword?: string }>({
  page: 1,
  pageSize: 10,
  status: undefined,
  type: undefined,
  keyword: ''
})

const statusOptions = [
  { label: '草稿', value: 0 },
  { label: '已发布', value: 1 },
  { label: '私密', value: 2 },
  { label: '回收站', value: 3 }
]

const typeOptions = [
  { label: '文章', value: 1 },
  { label: '杂谈', value: 2 }
]

async function loadData() {
  loading.value = true
  loadError.value = ''
  try {
    const res: PageResult<AdminArticle> = await getAdminArticlePage(query)
    tableData.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    // 后端 M1/M3 未就绪时清空表格
    tableData.value = []
    total.value = 0
    loadError.value = error instanceof Error ? error.message : '无法加载文章数据'
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

function handleReset() {
  query.status = undefined
  query.type = undefined
  query.keyword = ''
  query.page = 1
  loadData()
}

function handleCreate() {
  router.push('/admin/article/edit')
}

function handleEdit(row: AdminArticle) {
  router.push(`/admin/article/edit/${row.id}`)
}

async function handleDelete(row: AdminArticle) {
  try {
    await ElMessageBox.confirm(`确定删除文章「${row.title}」吗？`, '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteArticles([row.id])
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 用户取消或接口失败
  }
}

async function handleBatchDelete() {
  if (!selected.value.length) {
    ElMessage.warning('请先选择要删除的文章')
    return
  }
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selected.value.length} 篇文章吗？`, '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteArticles(selected.value.map((r) => r.id))
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 用户取消或接口失败
  }
}

async function handleToggleStatus(row: AdminArticle) {
  const next = row.status === 1 ? 0 : 1
  try {
    await changeArticleStatus(row.id, { status: next })
    ElMessage.success(next === 1 ? '已发布' : '已撤回草稿')
    loadData()
  } catch {
    // 接口失败
  }
}

async function handleToggleTop(row: AdminArticle) {
  try {
    await changeArticleStatus(row.id, { status: row.status, isTop: !row.isTop })
    ElMessage.success(!row.isTop ? '已置顶' : '已取消置顶')
    loadData()
  } catch {
    // 接口失败
  }
}

function handleSizeChange(size: number) {
  query.pageSize = size
  query.page = 1
  loadData()
}

function handleCurrentChange(page: number) {
  query.page = page
  loadData()
}

function handleSelectionChange(rows: AdminArticle[]) {
  selected.value = rows
}

function statusTagType(status: number) {
  return status === 1 ? 'success' : status === 0 ? 'info' : status === 2 ? 'warning' : 'danger'
}

function statusLabel(status: number) {
  return statusOptions.find((s) => s.value === status)?.label || '未知'
}

/** 从 DefaultRow 提取标签列表(类型安全的桥接函数) */
function getRowTags(row: AdminArticle): { id: number; name: string }[] {
  return row.tags || []
}

// el-table 的 row 默认类型为 DefaultRow, 通过 as AdminArticle 显式断言

onMounted(loadData)
</script>

<template>
  <div class="article-list">
    <el-alert v-if="loadError" :title="loadError" type="error" show-icon :closable="false" class="load-error" />
    <!-- 筛选区 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="query">
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            placeholder="标题/摘要"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option
              v-for="opt in statusOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.type" placeholder="全部" clearable style="width: 120px">
            <el-option
              v-for="opt in typeOptions"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <el-button type="primary" :icon="Plus" @click="handleCreate">新建文章</el-button>
        <el-button type="danger" :icon="Delete" :disabled="!selected.length" @click="handleBatchDelete">
          批量删除
        </el-button>
      </div>
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            <el-link type="primary" @click="handleEdit(scope.row as AdminArticle)">
              {{ scope.row.title }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" align="center" />
        <el-table-column label="标签" width="180">
          <template #default="scope">
            <el-tag
              v-for="t in getRowTags(scope.row as AdminArticle)"
              :key="t.id"
              size="small"
              class="tag-item"
            >
              {{ t.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读" width="80" align="center" />
        <el-table-column prop="commentCount" label="评论" width="80" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="statusTagType(scope.row.status as number)">
              {{ statusLabel(scope.row.status as number) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80" align="center">
          <template #default="scope">
            <el-switch
              :model-value="scope.row.isTop"
              @change="handleToggleTop(scope.row as AdminArticle)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" label="发布时间" width="160" align="center" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" link type="primary" @click="handleEdit(scope.row as AdminArticle)">
              编辑
            </el-button>
            <el-button
              size="small"
              link
              type="success"
              @click="handleToggleStatus(scope.row as AdminArticle)"
            >
              {{ scope.row.status === 1 ? '撤回' : '发布' }}
            </el-button>
            <el-button size="small" link type="danger" @click="handleDelete(scope.row as AdminArticle)">
              删除
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无文章数据" />
        </template>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.article-list {
  padding: 16px;
}

.load-error {
  margin-bottom: 16px;
}

.filter-card {
  margin-bottom: 16px;

  :deep(.el-card__body) {
    padding-bottom: 2px;
  }
}

.table-toolbar {
  margin-bottom: 12px;
  display: flex;
  gap: 8px;
}

.tag-item {
  margin-right: 4px;
  margin-bottom: 4px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
