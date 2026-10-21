import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 后端接口地址前缀：http://localhost:8081/api
// 通过 Vite 代理转发，避免开发环境跨域问题
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 8089,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
