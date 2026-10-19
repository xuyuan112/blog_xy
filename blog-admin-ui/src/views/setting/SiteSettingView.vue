<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getSiteConfigs, updateSiteConfigs } from '@/api/admin'

const loading = ref(false)
const saving = ref(false)
const form = reactive({
  siteName: '',
  siteDesc: '',
  icp: '',
  notice: ''
})

async function load() {
  loading.value = true
  try {
    Object.assign(form, await getSiteConfigs())
  } finally {
    loading.value = false
  }
}

async function save() {
  saving.value = true
  try {
    await updateSiteConfigs({ ...form })
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="settings-page">
    <el-card shadow="never" v-loading="loading">
      <template #header>站点配置</template>
      <el-form label-width="100px" class="settings-form">
        <el-form-item label="站点名称"><el-input v-model="form.siteName" /></el-form-item>
        <el-form-item label="站点描述"><el-input v-model="form.siteDesc" /></el-form-item>
        <el-form-item label="ICP备案号"><el-input v-model="form.icp" /></el-form-item>
        <el-form-item label="站点公告"><el-input v-model="form.notice" type="textarea" :rows="4" /></el-form-item>
        <el-form-item><el-button type="primary" :loading="saving" @click="save">保存配置</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.settings-page { padding: 16px; }
.settings-form { max-width: 680px; }
</style>
