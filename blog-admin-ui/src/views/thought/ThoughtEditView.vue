<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAdminArticleDetail, createArticle, updateArticle, type ArticleSaveParam } from '@/api/admin'
import { getCategories } from '@/api/category'
import { getTags } from '@/api/tag'
import { uploadImage } from '@/api/file'
import { ArticleStatus, ContentType } from '@/types/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const isEdit = computed(() => Boolean(route.params.id))
const articleId = computed(() => Number(route.params.id) || undefined)

// 杂谈固定 type=2
const form = reactive<ArticleSaveParam>({
  title: '',
  cover: '',
  summary: '',
  content: '',
  categoryId: undefined,
  tagIds: [],
  type: ContentType.THOUGHT,
  status: ArticleStatus.DRAFT,
  isTop: false
})
const categoryOptions = ref<{ id: number; name: string }[]>([])
const tagOptions = ref<{ id: number; name: string }[]>([])

async function load() {
  categoryOptions.value = (await getCategories()).map((x) => ({ id: x.id, name: x.name }))
  tagOptions.value = (await getTags()).map((x) => ({ id: x.id, name: x.name }))
  if (!articleId.value) return
  loading.value = true
  try {
    Object.assign(form, await getAdminArticleDetail(articleId.value))
    // 确保杂谈类型不变
    form.type = ContentType.THOUGHT
  } finally {
    loading.value = false
  }
}

async function uploadCover(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (file) form.cover = (await uploadImage(file, 'article')).url
}

async function importMarkdown(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  if (!/\.(md|markdown)$/i.test(file.name)) {
    ElMessage.warning('请选择 Markdown 文件')
    return
  }
  form.content = await file.text()
  ElMessage.success('Markdown 正文已导入')
}

async function submit(status: number) {
  if (!form.title.trim() || !form.content.trim()) {
    ElMessage.warning('标题和正文不能为空')
    return
  }
  submitting.value = true
  try {
    const data = { ...form, status, type: ContentType.THOUGHT }
    if (isEdit.value && articleId.value) await updateArticle(articleId.value, data)
    else await createArticle(data)
    ElMessage.success('保存成功')
    router.push('/admin/thought/list')
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<template>
  <div v-loading="loading" class="thought-edit">
    <el-card shadow="never">
      <template #header>{{ isEdit ? '编辑杂谈' : '新建杂谈' }}</template>
      <el-form label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" clearable placeholder="请选择分类">
            <el-option v-for="x in categoryOptions" :key="x.id" :label="x.name" :value="x.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="form.tagIds" multiple placeholder="请选择标签">
            <el-option v-for="x in tagOptions" :key="x.id" :label="x.name" :value="x.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面">
          <el-input v-model="form.cover" placeholder="可选" />
          <input type="file" accept="image/*" @change="uploadCover" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" />
        </el-form-item>
        <el-form-item label="正文">
          <el-input v-model="form.content" type="textarea" :rows="18" placeholder="支持 Markdown 原文" />
          <input type="file" accept=".md,.markdown,text/markdown" @change="importMarkdown" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="form.isTop" />
        </el-form-item>
        <el-form-item>
          <el-button @click="router.back()">取消</el-button>
          <el-button @click="submit(0)" :loading="submitting">保存草稿</el-button>
          <el-button type="primary" @click="submit(1)" :loading="submitting">发布</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.thought-edit {
  padding: 16px;
}
.el-form {
  max-width: 900px;
}
</style>
