<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search, Delete, Check, Hide } from '@element-plus/icons-vue'
import { changeCommentStatus, deleteComment, getAdminCommentPage, type AdminComment } from '@/api/comment'
import type { PageResult } from '@/types/api'
const loading = ref(false); const rows = ref<AdminComment[]>([]); const total = ref(0)
const query = reactive({ page: 1, pageSize: 10, targetType: undefined as number | undefined, status: undefined as number | undefined })
const statusLabel = (s: number) => ({ 0: '待审核', 1: '已发布', 2: '已隐藏' }[s] || '未知')
const statusType = (s: number) => s === 1 ? 'success' : s === 2 ? 'warning' : 'info'
const targetLabel = (t: number) => t === 1 ? '文章' : '说说'
async function loadData() { loading.value = true; try { const r: PageResult<AdminComment> = await getAdminCommentPage(query); rows.value = r.records || []; total.value = r.total || 0 } finally { loading.value = false } }
function search() { query.page = 1; loadData() }
function reset() { query.targetType = undefined; query.status = undefined; search() }
async function updateStatus(raw: unknown, status: number) { const row = raw as AdminComment; await changeCommentStatus(row.id, status); ElMessage.success(status === 1 ? '评论已发布' : '评论已隐藏'); loadData() }
async function remove(raw: unknown) { const row = raw as AdminComment; await ElMessageBox.confirm('删除后评论将不可恢复，确定继续吗？', '删除评论', { type: 'warning' }); await deleteComment(row.id); ElMessage.success('删除成功'); if (rows.value.length === 1 && query.page > 1) query.page -= 1; loadData() }
onMounted(loadData)
</script>
<template>
  <div class="comment-list">
    <el-card class="filter-card" shadow="never"><el-form :inline="true"><el-form-item label="目标类型"><el-select v-model="query.targetType" clearable placeholder="全部"><el-option label="文章" :value="1"/><el-option label="说说" :value="2"/></el-select></el-form-item><el-form-item label="状态"><el-select v-model="query.status" clearable placeholder="全部"><el-option label="待审核" :value="0"/><el-option label="已发布" :value="1"/><el-option label="已隐藏" :value="2"/></el-select></el-form-item><el-form-item><el-button type="primary" :icon="Search" @click="search">查询</el-button><el-button :icon="Refresh" @click="reset">重置</el-button></el-form-item></el-form></el-card>
    <el-card shadow="never"><el-table v-loading="loading" :data="rows" border stripe><el-table-column prop="id" label="ID" width="70"/><el-table-column label="目标" min-width="180"><template #default="s"><el-tag size="small">{{ targetLabel(s.row.targetType) }}</el-tag> #{{ s.row.targetId }}</template></el-table-column><el-table-column prop="content" label="评论内容" min-width="260" show-overflow-tooltip/><el-table-column prop="nickname" label="评论者" width="130"/><el-table-column label="状态" width="100"><template #default="s"><el-tag :type="statusType(s.row.status)">{{ statusLabel(s.row.status) }}</el-tag></template></el-table-column><el-table-column prop="createTime" label="时间" width="170"/><el-table-column label="操作" width="210" fixed="right"><template #default="s"><el-button v-if="s.row.status !== 1" link type="success" :icon="Check" @click="updateStatus(s.row, 1)">发布</el-button><el-button v-if="s.row.status !== 2" link type="warning" :icon="Hide" @click="updateStatus(s.row, 2)">隐藏</el-button><el-button link type="danger" :icon="Delete" @click="remove(s.row)">删除</el-button></template></el-table-column><template #empty><el-empty description="暂无评论"/></template></el-table><div class="pagination"><el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize" :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" @size-change="search" @current-change="loadData"/></div></el-card>
  </div>
</template>
<style scoped lang="scss">.comment-list{padding:16px}.filter-card{margin-bottom:16px}.pagination{margin-top:16px;display:flex;justify-content:flex-end}</style>
