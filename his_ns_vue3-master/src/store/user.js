import { defineStore } from 'pinia'
import { ref ,computed } from 'vue'
import { postReq } from '../utils/api'

//用户仓库  提供数据、计算属性、方法
export const useUserStore=defineStore("user",()=>{
	//ref = state  //存放用户登录后的信息
	const userInfo=ref({
		id:"",
		token:"",
		isAuth:false,
	})

	// 初始化时从 localStorage 恢复状态
	const storedUser = localStorage.getItem("user");
	if (storedUser) {
		try {
			const user = JSON.parse(storedUser);
			if (user && user.token && user.isAuth) {
				userInfo.value = user;
			}
		} catch (e) {
			// 如果解析出错，清除损坏的数据
			localStorage.removeItem("user");
			localStorage.removeItem("token");
			localStorage.removeItem("isAuth");
		}
	}
	
	const isLogin=ref(false)
	
	//computed() = getters
	//返回用户登录信息
	const getUserInfo = computed(()=>{
		return userInfo;
	})

	const isAuth = computed(() => userInfo.value.isAuth)
	 
	//function() = actions
	//设置登录状态，获取登录信息
	const setAuthenticated=(u)=>{
		userInfo.value=u;
		//同步localStorage
		localStorage.setItem("user",JSON.stringify(u));
		localStorage.setItem("token",u.token);
		localStorage.setItem("isAuth",u.isAuth);
	}

	// const setLoginState(f)=>{
	// 	isLogin.value=f
	// }

	//刷新token
	const refreshToken=()=>{
		// refreshTokenApi().then(response=>{
		// 	userStore.setAuthenticated({
		// 		token:response.data.access_token,
		// 		isAuth:true
		// 	})
		// })
	}
	//退出登录
	const logOut=()=>{
		userInfo.value = { id: "", token: "", isAuth: false };
		//LocalStorage:除非主动删除，否则会永久存储在浏览器中。
		localStorage.removeItem("user");
		localStorage.removeItem("token");
		localStorage.removeItem("isAuth");
		//SessionStorage:只在当前所在窗口关闭前有效，窗口关闭后其存储数据也就会被自动清除。
		sessionStorage.clear()	
	}
	
	return {userInfo, isAuth, getUserInfo,setAuthenticated,logOut}
})