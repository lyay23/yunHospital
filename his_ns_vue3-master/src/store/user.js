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

	const isLogin=ref(false)
	
	//computed() = getters
	//返回用户登录信息
	const getUserInfo = computed(()=>{
		console.log("getUserInfo:start")
		//pinia存储的用户信息和本地localStorage
		console.log(userInfo.value.isAuth);
		console.log(JSON.parse(localStorage.getItem("isAuth")));
		if(userInfo.value.isAuth==false
			&& JSON.parse(localStorage.getItem("isAuth")) ){
			console.log("getUserInfo:更新用户信息")
			setAuthenticated(JSON.parse(localStorage.getItem("user")))
		}
		console.log("getUserInfo:end")
		return userInfo;
	})

	const isAuth = computed(() => userInfo.value.isAuth)
	 
	//function() = actions
	//设置登录状态，获取登录信息
	const setAuthenticated=(u)=>{
		console.log("setAuthenticated:start")
		userInfo.value=u;
		//同步localStorage
		console.log(u.useType)
		localStorage.setItem("user",JSON.stringify(u));
		localStorage.setItem("token",u.token);
		localStorage.setItem("isAuth",u.isAuth);
		console.log(`setAuthenticated:${userInfo.value.isAuth}`)
		console.log("setAuthenticated:end")
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
		console.log("logOut:start")
		userInfo.value.isAuth=false
		userInfo.value.token=''
		//LocalStorage:除非主动删除，否则会永久存储在浏览器中。
		localStorage.setItem("user",'');
		localStorage.setItem("token",'');
		localStorage.setItem("isAuth",false);
		//SessionStorage:只在当前所在窗口关闭前有效，窗口关闭后其存储数据也就会被自动清除。
		sessionStorage.clear()	
		console.log("logOut:end")
	}
	
	return {userInfo, isAuth, getUserInfo,setAuthenticated,logOut}
})