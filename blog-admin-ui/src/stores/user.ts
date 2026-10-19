import { defineStore } from 'pinia'
import { ref } from 'vue'

interface UserInfo {
  userId: number
  username: string
  nickname: string
  avatar: string
  roles: string[]
}

export const useUserStore = defineStore(
  'user',
  () => {
    const accessToken = ref('')
    const refreshToken = ref('')
    const userInfo = ref<UserInfo | null>(null)

    const isLogin = () => !!accessToken.value

    const setTokens = (access: string, refresh: string) => {
      accessToken.value = access
      refreshToken.value = refresh
    }

    const setUserInfo = (info: UserInfo) => {
      userInfo.value = info
    }

    const logout = () => {
      accessToken.value = ''
      refreshToken.value = ''
      userInfo.value = null
    }

    return {
      accessToken,
      refreshToken,
      userInfo,
      isLogin,
      setTokens,
      setUserInfo,
      logout
    }
  },
  {
    persist: {
      key: 'blog-admin-user',
      storage: localStorage
    }
  }
)
