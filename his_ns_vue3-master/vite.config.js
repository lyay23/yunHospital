// import { defineConfig } from 'vite'
// import vue from '@vitejs/plugin-vue'
// import path from 'path'

// // https://vitejs.dev/config/
// export default defineConfig({
//   plugins: [vue()],
//   resolve: {
//     alias: {
//       '@': path.resolve(__dirname, './src')
//     }
//   },
// server: {
//     proxy: {
//       '/api': {
//         target: 'http://localhost:8080', // 后端服务地址
//         changeOrigin: true,
//         rewrite: (path) => path.replace(/^\/api/, ''),
//         ws: true
//       }
//     }
//   }


// })



import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
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
    port: 3000,
    proxy: {
      '/ai': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      }
    }
  }
})


