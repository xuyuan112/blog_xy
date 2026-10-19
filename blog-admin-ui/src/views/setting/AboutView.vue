<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAbout, updateAbout, type AboutData } from '@/api/about'
import { uploadImage } from '@/api/file'

type Skill = { name: string; level: number; category?: string }
type Milestone = { year: string; title: string; description?: string }
const loading = ref(false)
const saving = ref(false)
const avatarInput = ref<HTMLInputElement>()
const uploadingAvatar = ref(false)
const form = reactive<Required<Pick<AboutData, 'nickname' | 'avatar' | 'signature' | 'intro'>> & { skills: Skill[]; milestones: Milestone[] }>({ nickname: '', avatar: '', signature: '', intro: '', skills: [], milestones: [] })

async function load() {
  loading.value = true
  try {
    const data = await getAbout()
    Object.assign(form, { nickname: data.nickname || '', avatar: data.avatar || '', signature: data.signature || '', intro: data.intro || '', skills: (data.skills || []).map((item: any) => ({ name: item.name || '', level: Number(item.level) || 0, category: item.category || '' })), milestones: (data.milestones || []).map((item: any) => ({ year: item.year || '', title: item.title || '', description: item.description || '' })) })
  } finally { loading.value = false }
}
function pickAvatar() { avatarInput.value?.click() }
async function uploadAvatar(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (!file) return
  uploadingAvatar.value = true
  try {
    const result = await uploadImage(file, 'avatar')
    form.avatar = result.url
    ElMessage.success('头像上传成功')
  } catch {
    ElMessage.error('头像上传失败，请重试')
  } finally {
    uploadingAvatar.value = false
    if (avatarInput.value) avatarInput.value.value = ''
  }
}
function addSkill() { form.skills.push({ name: '', level: 0, category: '' }) }
function removeSkill(index: number) { form.skills.splice(index, 1) }
function addMilestone() { form.milestones.push({ year: '', title: '', description: '' }) }
function removeMilestone(index: number) { form.milestones.splice(index, 1) }
async function save() {
  saving.value = true
  try {
    await updateAbout({ nickname: form.nickname.trim(), avatar: form.avatar.trim(), signature: form.signature.trim(), intro: form.intro, skills: form.skills.map((item) => ({ ...item, name: item.name.trim(), category: item.category?.trim() })), milestones: form.milestones.map((item) => ({ ...item, year: item.year.trim(), title: item.title.trim(), description: item.description?.trim() })) })
    ElMessage.success('保存成功')
  } catch { ElMessage.error('保存失败，请检查网络或输入内容') } finally { saving.value = false }
}
onMounted(load)
</script>

<template>
  <div class="page"><el-card v-loading="loading" shadow="never"><template #header>关于我</template>
    <el-form label-position="top" class="about-form">
      <div class="form-grid"><el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item><el-form-item label="头像"><div class="avatar-editor"><el-avatar :size="72" :src="form.avatar" class="avatar-preview" /><div class="avatar-actions"><el-button type="primary" plain :loading="uploadingAvatar" @click="pickAvatar">上传头像</el-button><input ref="avatarInput" type="file" accept="image/*" hidden @change="uploadAvatar" /><el-input v-model="form.avatar" placeholder="或手动填写头像 URL" /></div></div></el-form-item><el-form-item label="个性签名"><el-input v-model="form.signature" /></el-form-item></div>
      <el-form-item label="个人介绍（支持 Markdown）"><el-input v-model="form.intro" type="textarea" :rows="10" placeholder="支持 Markdown 标题、列表、链接和代码块" /></el-form-item>
      <section class="editor-section"><div class="section-title"><h3>技能</h3><el-button type="primary" plain @click="addSkill">新增技能</el-button></div><el-empty v-if="!form.skills.length" description="暂无技能" /><div v-for="(skill, index) in form.skills" :key="index" class="repeat-row"><el-input v-model="skill.name" placeholder="技能名称" /><el-input v-model="skill.category" placeholder="分类（可选）" /><el-input-number v-model="skill.level" :min="0" :max="100" controls-position="right" /><el-button type="danger" link @click="removeSkill(index)">删除</el-button></div></section>
      <section class="editor-section"><div class="section-title"><h3>经历时间线</h3><el-button type="primary" plain @click="addMilestone">新增经历</el-button></div><el-empty v-if="!form.milestones.length" description="暂无经历" /><div v-for="(item, index) in form.milestones" :key="index" class="milestone-row"><el-input v-model="item.year" placeholder="年份" /><el-input v-model="item.title" placeholder="标题" /><el-input v-model="item.description" type="textarea" :rows="2" placeholder="描述" /><el-button type="danger" link @click="removeMilestone(index)">删除</el-button></div></section>
      <el-form-item><el-button type="primary" :loading="saving" @click="save">保存关于我</el-button></el-form-item>
    </el-form>
  </el-card></div>
</template>

<style scoped lang="scss">
.page{padding:16px}.about-form{max-width:980px}.form-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:16px}.editor-section{border-top:1px solid var(--admin-border);padding-top:18px;margin:22px 0}.section-title{display:flex;align-items:center;justify-content:space-between;margin-bottom:12px}.section-title h3{margin:0;font-size:16px}.repeat-row{display:grid;grid-template-columns:2fr 1.5fr 130px auto;gap:10px;align-items:center;margin-bottom:10px}.milestone-row{display:grid;grid-template-columns:130px 1.2fr 2fr auto;gap:10px;align-items:start;margin-bottom:10px}.milestone-row :deep(.el-textarea__inner){min-height:58px!important}.avatar-editor{display:flex;align-items:center;gap:14px}.avatar-preview{flex-shrink:0;border:1px solid var(--admin-border)}.avatar-actions{display:flex;flex-direction:column;gap:10px;flex:1;min-width:0}.avatar-actions .el-button{align-self:flex-start}@media(max-width:760px){.form-grid{grid-template-columns:1fr}.repeat-row,.milestone-row{grid-template-columns:1fr}.repeat-row .el-button,.milestone-row .el-button{justify-self:start}}</style>
