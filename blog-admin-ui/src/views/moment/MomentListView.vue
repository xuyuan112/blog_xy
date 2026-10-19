<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createMoment, deleteMoment, getAdminMomentPage, updateMoment, type AdminMoment } from '@/api/moment'
import { uploadImage } from '@/api/file'
import type { PageResult } from '@/types/api'

/** 说说图片上限 (与后端 t_moment.images JSON 数组约束一致) */
const MAX_IMAGES = 9

const rows = ref<AdminMoment[]>([])
const loading = ref(false)
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const submitting = ref(false)
const editingId = ref<number | null>(null)
/** 图片上传中数量 (多图并发上传时给用户反馈) */
const uploadingCount = ref(0)
const form = reactive({ content: '', location: '', mood: '', visible: 0, images: [] as string[] })

async function loadData() {
  loading.value = true
  try {
    const result: PageResult<AdminMoment> = await getAdminMomentPage({ page: page.value, pageSize: pageSize.value })
    rows.value = result.records || []
    total.value = result.total || 0
  } finally {
    loading.value = false
  }
}

function resetForm() {
  Object.assign(form, { content: '', location: '', mood: '', visible: 0, images: [] })
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: AdminMoment) {
  editingId.value = row.id
  // 回显已发表说说的图片数组, 编辑时可继续增删
  Object.assign(form, {
    content: row.content,
    location: row.location || '',
    mood: row.mood || '',
    visible: row.visible,
    images: [...(row.images || [])],
  })
  dialogVisible.value = true
}

/**
 * 选择照片并上传 (仿照相册模块: 顶部「上传照片」按钮 + hidden file input, 支持多选)。
 * 与相册的区别: 相册照片是独立实体即时落库; 说说图片是 JSON 数组,
 * 这里上传拿到 URL 后暂存在 form.images, 保存说说时一起提交。
 */
async function uploadPhotos(event: Event) {
  const input = event.target as HTMLInputElement
  const files = Array.from(input.files || [])
  // 清空 input.value, 否则同一文件无法再次选择
  input.value = ''
  if (!files.length) return

  // 非图片过滤
  const images = files.filter((f) => f.type.startsWith('image/'))
  if (images.length < files.length) ElMessage.warning('已自动跳过非图片文件')

  const room = MAX_IMAGES - form.images.length
  if (room <= 0) {
    ElMessage.warning(`最多上传 ${MAX_IMAGES} 张图片`)
    return
  }
  const toUpload = images.slice(0, room)
  if (images.length > room) {
    ElMessage.warning(`最多 ${MAX_IMAGES} 张, 多出 ${images.length - room} 张未添加`)
  }

  uploadingCount.value = toUpload.length
  for (const file of toUpload) {
    try {
      const result = await uploadImage(file, 'moment')
      form.images.push(result.url)
    } catch {
      ElMessage.error(`图片「${file.name}」上传失败, 请重试`)
    } finally {
      uploadingCount.value--
    }
  }
  if (toUpload.length) ElMessage.success('上传成功')
}

/** 删除一张图片 (仿照相册照片删除: 保存说说后随 images 数组一起更新到后端) */
function removeImage(index: number) {
  form.images.splice(index, 1)
}

async function save() {
  if (!form.content.trim()) {
    ElMessage.warning('请输入说说内容')
    return
  }
  if (uploadingCount.value > 0) {
    ElMessage.warning('图片还在上传中, 请稍候')
    return
  }
  submitting.value = true
  try {
    if (editingId.value === null) {
      await createMoment({ ...form })
    } else {
      await updateMoment(editingId.value, { ...form })
    }
    dialogVisible.value = false
    ElMessage.success(editingId.value === null ? '发布成功' : '保存成功')
    loadData()
  } finally {
    submitting.value = false
  }
}

async function remove(raw: unknown) {
  const row = raw as AdminMoment
  await ElMessageBox.confirm('确定删除该说说吗？', '删除说说', { type: 'warning' })
  await deleteMoment(row.id)
  ElMessage.success('删除成功')
  loadData()
}

/** 可见性数字转文案 (表格列展示) */
function visibleText(v: number): string {
  return v === 1 ? '隐藏' : v === 2 ? '私密' : '公开'
}

onMounted(loadData)
</script>

<template>
  <div class="moment-list">
    <el-card shadow="never">
      <template #header>
        <div class="header">
          <span>说说管理</span>
          <el-button type="primary" @click="openCreate">新增说说</el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="rows" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
        <el-table-column label="图片" width="120">
          <template #default="scope">
            <div v-if="scope.row.images && scope.row.images.length" class="table-images">
              <el-image
                :src="scope.row.images[0]"
                fit="cover"
                class="table-thumb"
                :preview-src-list="scope.row.images"
                preview-teleported
              />
              <el-tag v-if="scope.row.images.length > 1" size="small" type="info" class="img-count">
                {{ scope.row.images.length }} 张
              </el-tag>
            </div>
            <span v-else class="no-image">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="120" />
        <el-table-column prop="mood" label="心情" width="100" />
        <el-table-column label="可见性" width="90">
          <template #default="scope">{{ visibleText(scope.row.visible) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row as AdminMoment)">编辑</el-button>
            <el-button link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="暂无说说" /></template>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId === null ? '新增说说' : '编辑说说'" width="680px">
      <el-form label-width="80px">
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="5" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="图片">
          <div class="photo-panel">
            <!-- 工具栏: 仿照相册模块「上传照片」按钮 -->
            <div class="photo-toolbar">
              <label class="upload-button">
                {{ uploadingCount > 0 ? `上传中 ${uploadingCount} 张...` : '上传照片' }}
                <input type="file" accept="image/*" multiple hidden @change="uploadPhotos" />
              </label>
              <span class="photo-count">{{ form.images.length }}/{{ MAX_IMAGES }} 张</span>
            </div>
            <el-empty v-if="!form.images.length" description="暂无照片" :image-size="60" />
            <div v-else class="photo-grid">
              <div v-for="(url, index) in form.images" :key="url" class="photo-item">
                <el-image
                  :src="url"
                  fit="cover"
                  class="photo-img"
                  :preview-src-list="form.images"
                  :initial-index="index"
                  preview-teleported
                  hide-on-click-modal
                />
                <el-button link type="danger" @click="removeImage(index)">删除</el-button>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="位置"><el-input v-model="form.location" placeholder="如: 杭州西湖" /></el-form-item>
        <el-form-item label="心情"><el-input v-model="form.mood" placeholder="如: happy" /></el-form-item>
        <el-form-item label="可见性">
          <el-select v-model="form.visible">
            <el-option label="公开" :value="0" />
            <el-option label="隐藏" :value="1" />
            <el-option label="私密" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.moment-list {
  padding: 16px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

/* 列表表格缩略图 */
.table-images {
  position: relative;
  display: inline-block;
}
.table-thumb {
  width: 56px;
  height: 56px;
  border-radius: 6px;
  cursor: pointer;
}
.img-count {
  position: absolute;
  right: -8px;
  bottom: -8px;
}
.no-image {
  color: var(--el-text-color-placeholder);
}

/* 弹窗图片编辑 (样式仿照相册模块照片管理) */
.photo-panel {
  width: 100%;
}
.photo-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.upload-button {
  display: inline-block;
  padding: 8px 14px;
  color: #fff;
  background: var(--el-color-primary);
  border-radius: 4px;
  cursor: pointer;
  user-select: none;
}
.upload-button:hover {
  opacity: 0.9;
}
.photo-count {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
.photo-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}
.photo-item {
  text-align: center;
}
.photo-img {
  width: 100%;
  height: 110px;
  border-radius: 6px;
  overflow: hidden;
  display: block;
  cursor: pointer;
}
.photo-item .el-button {
  margin-top: 2px;
}
</style>
