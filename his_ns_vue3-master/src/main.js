import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import ElementPlus from 'element-plus';
import 'element-plus/theme-chalk/index.css';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import App from './App.vue'
import Axios from 'axios'



const app=createApp(App)
app.use(router)
app.use(createPinia())
app.use(ElementPlus, { locale: zhCn })
app.mount('#app')

