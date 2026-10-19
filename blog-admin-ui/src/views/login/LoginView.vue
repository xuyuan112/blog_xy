<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Picture, Loading } from '@element-plus/icons-vue'
import { getCaptcha, login } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import { setTokens } from '@/utils/auth'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const captchaImage = ref('')
const captchaLoading = ref(false)
const submitLoading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  captchaKey: '',
  captchaCode: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' }
  ],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

/** 加载验证码 */
async function loadCaptcha() {
  captchaLoading.value = true
  try {
    const res = await getCaptcha()
    loginForm.captchaKey = res.captchaKey
    captchaImage.value = res.captchaImage
  } catch {
    // 后端 M1 未就绪时验证码会 404, 提示但不阻塞页面渲染
    captchaImage.value = ''
  } finally {
    captchaLoading.value = false
  }
}

/** 提交登录 */
async function handleLogin() {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      const result = await login({
        username: loginForm.username,
        password: loginForm.password,
        captchaKey: loginForm.captchaKey,
        captchaCode: loginForm.captchaCode
      })
      // 持久化到 store + localStorage (axios 拦截器也从 localStorage 读)
      userStore.setTokens(result.accessToken, result.refreshToken)
      setTokens(result.accessToken, result.refreshToken)
      userStore.setUserInfo({
        userId: result.userInfo.userId,
        username: result.userInfo.username,
        nickname: result.userInfo.nickname,
        avatar: result.userInfo.avatar,
        roles: []
      })
      ElMessage.success('登录成功')
      const redirect = (route.query.redirect as string) || '/admin/home/dashboard'
      router.replace(redirect)
    } catch {
      // 验证码失效或登录失败, 刷新验证码
      loadCaptcha()
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(loadCaptcha)
</script>

<template>
  <div class="login-page">
    <div class="login-left">
      <div class="brand">
        <h1>Xuyuan Blog</h1>
        <p>个人全栈博客系统 · 后台管理</p>
        <p class="hint">Vue 3 + Element Plus + Spring Boot</p>
      </div>
    </div>
    <div class="login-right">
      <div class="login-card">
        <h2 class="login-title">账户登录</h2>
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          size="large"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>
          <el-form-item prop="captchaCode">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.captchaCode"
                placeholder="请输入验证码"
                :prefix-icon="Picture"
                clearable
              />
              <div class="captcha-image" @click="loadCaptcha">
                <el-icon v-if="captchaLoading" class="loading-icon"><Loading /></el-icon>
                <img v-else-if="captchaImage" :src="captchaImage" alt="验证码" />
                <span v-else class="captcha-placeholder">点击加载</span>
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="submitLoading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-tip">
          <span>初始管理员账号: admin / 123456</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-page {
  display: flex;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;

  .brand {
    text-align: center;

    h1 {
      font-size: 42px;
      font-weight: 700;
      margin-bottom: 12px;
      letter-spacing: 2px;
    }

    p {
      font-size: 16px;
      opacity: 0.9;
      margin-bottom: 6px;
    }

    .hint {
      font-size: 13px;
      opacity: 0.7;
      margin-top: 20px;
    }
  }
}

.login-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.login-card {
  width: 360px;
  padding: 40px 0;
}

.login-title {
  font-size: 22px;
  color: var(--admin-text);
  margin-bottom: 32px;
  text-align: center;
}

.captcha-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.captcha-image {
  width: 120px;
  height: 40px;
  border: 1px solid var(--admin-border);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  flex-shrink: 0;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.captcha-placeholder {
  font-size: 12px;
  color: var(--admin-text-secondary);
}

.loading-icon {
  animation: rotate 1.5s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.login-btn {
  width: 100%;
}

.login-tip {
  text-align: center;
  margin-top: 16px;
  font-size: 12px;
  color: var(--admin-text-secondary);
}
</style>
