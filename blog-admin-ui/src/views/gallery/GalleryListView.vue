<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createGallery, deleteGallery, deletePhoto, getAdminGalleryPage, listGalleryPhotos, uploadGalleryPhotos, updateGallery, type AdminGallery } from '@/api/gallery'

const rows = ref<AdminGallery[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const photoDialogVisible = ref(false)
const editingId = ref<number | null>(null)
const currentGallery = ref<AdminGallery | null>(null)
const photos = ref<any[]>([])
const form = reactive({ name: '', description: '', cover: '', shootDate: '' })

async function loadData() {
  loading.value = true
  try {
    const result = await getAdminGalleryPage({ page: page.value, pageSize: pageSize.value })
    rows.value = result.records || []
    total.value = result.total || 0
  } finally { loading.value = false }
}
function openCreate() { editingId.value = null; Object.assign(form, { name: '', description: '', cover: '', shootDate: '' }); dialogVisible.value = true }
function openEdit(row: AdminGallery) { editingId.value = row.id; Object.assign(form, row); dialogVisible.value = true }
async function save() { if (!form.name.trim()) { ElMessage.warning('请输入相册名称'); return }; if (editingId.value === null) await createGallery(form); else await updateGallery(editingId.value, form); dialogVisible.value = false; ElMessage.success('保存成功'); loadData() }
async function remove(row: AdminGallery) { await ElMessageBox.confirm('确定删除相册及其照片吗？', '删除相册', { type: 'warning' }); await deleteGallery(row.id); ElMessage.success('删除成功'); loadData() }
async function showPhotos(row: AdminGallery) { currentGallery.value = row; photos.value = await listGalleryPhotos(row.id); photoDialogVisible.value = true }
async function upload(event: Event) { if (!currentGallery.value) return; const files = Array.from((event.target as HTMLInputElement).files || []); if (!files.length) return; await uploadGalleryPhotos(currentGallery.value.id, files); ElMessage.success('上传成功'); photos.value = await listGalleryPhotos(currentGallery.value.id); loadData() }
async function removePhoto(photo: any) { await ElMessageBox.confirm('确定删除这张照片吗？', '删除照片', { type: 'warning' }); await deletePhoto(photo.id); photos.value = photos.value.filter(item => item.id !== photo.id); ElMessage.success('删除成功'); loadData() }
onMounted(loadData)
</script>

<template>
  <div class="gallery-list">
    <el-card shadow="never">
      <template #header><div class="header"><span>相册管理</span><el-button type="primary" @click="openCreate">新增相册</el-button></div></template>
      <el-table v-loading="loading" :data="rows" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="相册名称" min-width="160" />
        <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
        <el-table-column prop="photoCount" label="照片数" width="90" align="center" />
        <el-table-column prop="shootDate" label="拍摄日期" width="130" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="scope"><el-button link type="primary" @click="openEdit(scope.row as AdminGallery)">编辑</el-button><el-button link @click="showPhotos(scope.row as AdminGallery)">照片</el-button><el-button link type="danger" @click="remove(scope.row as AdminGallery)">删除</el-button></template>
        </el-table-column>
        <template #empty><el-empty description="暂无相册" /></template>
      </el-table>
      <el-pagination v-model:current-page="page" v-model:page-size="pageSize" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="loadData" @current-change="loadData" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="editingId === null ? '新增相册' : '编辑相册'" width="520px"><el-form label-width="90px"><el-form-item label="名称"><el-input v-model="form.name" /></el-form-item><el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item><el-form-item label="封面 URL"><el-input v-model="form.cover" /></el-form-item><el-form-item label="拍摄日期"><el-date-picker v-model="form.shootDate" type="date" value-format="YYYY-MM-DD" /></el-form-item></el-form><template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template></el-dialog>
    <el-dialog v-model="photoDialogVisible" :title="`${currentGallery?.name || ''} - 照片管理`" width="760px"><div class="photo-toolbar"><label class="upload-button">上传照片<input type="file" accept="image/*" multiple hidden @change="upload" /></label></div><el-empty v-if="!photos.length" description="暂无照片"/><div v-else class="photo-grid"><div v-for="photo in photos" :key="photo.id" class="photo-item"><img :src="photo.thumbnail || photo.original" :alt="photo.original"/><el-button link type="danger" @click="removePhoto(photo)">删除</el-button></div></div></el-dialog>
  </div>
</template>

<style scoped lang="scss">
.gallery-list { padding: 16px; }
.header { display: flex; justify-content: space-between; align-items: center; }
.el-pagination { margin-top: 16px; justify-content: flex-end; }
.photo-toolbar { margin-bottom: 16px; }
.upload-button { display: inline-block; padding: 8px 14px; color: #fff; background: var(--el-color-primary); border-radius: 4px; cursor: pointer; }
.photo-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
.photo-item img { width: 100%; height: 130px; object-fit: cover; display: block; margin-bottom: 4px; }
</style>
