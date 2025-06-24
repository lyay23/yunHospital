import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  // //跨域代理
  // server: {
  //     proxy: {
  //       // Using the proxy instance
  //       '/his': {
  //         target: 'http://localhost:8080',
  //         changeOrigin: true,
		//   rewrite:(path) => path.replace(/^\/his/, ''),
  //       }
		// }
  //   },
server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端服务地址
        changeOrigin: true,
        ws: true
      }
    }
  }


})
