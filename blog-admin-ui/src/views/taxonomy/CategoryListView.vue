<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createCategory, deleteCategory, getCategories, updateCategory, type AdminCategory } from '@/api/category'

const rows = ref<AdminCategory[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const editingId = ref<number | null>(null)

const form = reactive({
  name: '',
  description: '',
  sort: 0
})

async function loadData() {
  loading.value = true
  try {
    rows.value = await getCategories()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingId.value = null
  Object.assign(form, { name: '', description: '', sort: 0 })
  dialogVisible.value = true
}

function openEdit(row: AdminCategory) {
  editingId.value = row.id
  Object.assign(form, { name: row.name, description: row.description || '', sort: row.sort || 0 })
  dialogVisible.value = true
}

async function submit() {
  if (!form.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  submitting.value = true
  try {
    if (editingId.value === null) {
      await createCategory(form)
    } else {
      await updateCategory(editingId.value, form)
    }
    dialogVisible.value = false
    ElMessage.success('保存成功')
    await loadData()
  } finally {
    submitting.value = false
  }
}

async function remove(row: AdminCategory) {
  await ElMessageBox.confirm(
    row.articleCount > 0 ? '该分类下存在文章，删除可能会失败，确定继续吗？' : '确定删除该分类吗？',
    '删除分类',
    { type: 'warning' }
  )
  await deleteCategory(row.id)
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
          <span>分类管理</span>
          <el-button type="primary" @click="openCreate">新增分类</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="rows" border stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="名称" min-width="160" />
        <el-table-column prop="description" label="描述" min-width="240" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="90" align="center" />
        <el-table-column prop="articleCount" label="文章数" width="100" align="center" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row as AdminCategory)">编辑</el-button>
            <el-button link type="danger" @click="remove(scope.row as AdminCategory)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="暂无分类" /></template>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId === null ? '新增分类' : '编辑分类'" width="500px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" maxlength="64" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" maxlength="255" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" :max="9999" /></el-form-item>
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
