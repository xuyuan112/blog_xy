import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())

  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      host: '0.0.0.0',
      port: 5174,
      strictPort: true,
      proxy: {
        // 后台管理端接口 -> 本地 blog-web-admin (8080)
        // 本地 dev 永远走 localhost:8080，与 .env 的 VITE_API_BASE_URL 路径前缀无关
        '/xuyuan': {
          target: 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },
    preview: {
      // 宝塔部署：npm start 会跑 `vite preview`，默认监听 0.0.0.0:5174
      host: '0.0.0.0',
      port: 5174,
      strictPort: true
    },
    build: {
      chunkSizeWarningLimit: 1024
    }
  }
})
