<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createTag, deleteTag, getTags, updateTag, type AdminTag } from '@/api/tag'

const rows = ref<AdminTag[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  color: ''
})

async function loadData() {
  loading.value = true
  try {
    rows.value = await getTags()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { name: '', color: '' })
  dialogVisible.value = true
}

function openEdit(row: AdminTag) {
  editingId.value = row.id
  Object.assign(form, { name: row.name, color: row.color || '' })
  dialogVisible.value = true
}

async function submit() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入标签名称')
    return
  }
  submitting.value = true
  try {
    if (editingId.value === null) {
      await createTag(form)
    } else {
      await updateTag(editingId.value, form)
    }
    dialogVisible.value = false
    ElMessage.success('保存成功')
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function remove(row: AdminTag) {
  await ElMessageBox.confirm(
    row.articleCount > 0 ? '该标签已绑定文章，删除后关联关系会被移除，确定继续吗？' : '确定删除该标签吗？',
    '删除标签',
    { type: 'warning' }
  )
  await deleteTag(row.id)
  ElMessage.success('删除成功')
  await loadData()
}

onMounted(loadData)
</script>

<template>
  <div class="taxonomy-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>标签管理</span>
          <el-button type="primary" @click="openCreate">新增标签</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="rows" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="名称" min-width="180" />
        <el-table-column label="颜色" width="140" align="center">
          <template #default="scope">
            <el-tag :color="scope.row.color">{{ scope.row.color || '默认' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="articleCount" label="文章数" width="100" align="center" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row as AdminTag)">编辑</el-button>
            <el-button link type="danger" @click="remove(scope.row as AdminTag)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="暂无标签" /></template>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId === null ? '新增标签' : '编辑标签'" width="460px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" maxlength="64" /></el-form-item>
        <el-form-item label="颜色"><el-input v-model="form.color" placeholder="#409EFF" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.taxonomy-page { padding: 16px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
</style>
