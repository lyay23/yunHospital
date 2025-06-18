// store/index.js
import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'

// 状态定义
const state = {
  count: 5,           // 合并原 reactive 的 count
  user: {
    id: '',
    token: '',
    isAuth: false
  },
  isLogin: false
}

// 突变（修改状态的唯一方式）
const mutations = {
  // 计数器相关
  SET_COUNT(state, value) {
    state.count = value
  },
  INCREMENT(state) {
    state.count++
  },
  
  // 用户认证相关
  SET_LOGIN_STATUS(state, flag) {
    state.isLogin = flag
    state.user.isAuth = flag
  },
  SET_USER_INFO(state, userData) {
    state.user = { ...state.user, ...userData }
  },
  LOGOUT(state) {
    state.user = { id: '', token: '', isAuth: false }
    state.isLogin = false
  }
}

// 动作（处理异步操作，提交突变）
const actions = {
  setLoginStatus({ commit }, flag) {
    commit('SET_LOGIN_STATUS', flag)
  },
  
  async login({ commit }, credentials) {
    try {
      // 模拟登录请求
      const response = await fetch('/api/login', {
        method: 'POST',
        body: JSON.stringify(credentials)
      })
      
      const data = await response.json()
      commit('SET_USER_INFO', {
        id: data.userId,
        token: data.token,
        isAuth: true
      })
      commit('SET_LOGIN_STATUS', true)
      return true
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  },
  
  logout({ commit }) {
    commit('LOGOUT')
  }
}

// 计算属性（类似 Vue 的计算属性）
const getters = {
  count: state => state.count,
  isLoggedIn: state => state.isLogin,
  userInfo: state => state.user
}

// 创建 store 并启用持久化插件  需要安装 vuex-persistedstate，这里暂时不启用 
// export default createStore({
//   state,
//   mutations,
//   actions,
//   getters,
//   plugins: [
//     createPersistedState({
//       key: 'vuex-store',
//       storage: localStorage,
//       paths: ['user', 'isLogin'] // 只持久化用户相关状态
//     })
//   ]
// })