<template>
	<div class="login-wrapper">
		<el-form :model="loginForm" :rules="rules" ref="ruleForm" class="login-container" label-position="left"
			label-width="80px" v-loading="loading" status-icon>
			<h3 class="login_title">东软云医院系统登录</h3>
			<el-form-item label="用户名" prop="userName">
				<el-input v-model="loginForm.userName" placeholder="用户名"></el-input>
			</el-form-item>
			<el-form-item label="密码" prop="password">
				<el-input type="password" v-model="loginForm.password" placeholder="密码"></el-input>
			</el-form-item>
			<div style="display: flex; justify-content: center; width: 100%;">
					<el-button type="primary" style="width: 40%" @click.native.prevent="submitClick">登录</el-button>
			</div>
		</el-form>
	</div>
</template>

<script setup>
import { ref } from "vue";
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { postReq } from '../utils/api'
import { ElMessageBox } from 'element-plus'
//导入用户仓库
import { useUserStore } from '../store/user.js'

//路由
const router = useRouter()
//获取用户仓库对象
const userStore=useUserStore()
const loginForm=ref({})

const rules=ref({
	userName: [
		{ required: true, message: '请输入用户名', trigger: 'blur' },
		{ min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
	],
	password: [
		{ required: true, message: '请输入密码', trigger: 'blur' },
		{ min: 6, max: 16, message: '长度在 6 到 16 个字符', trigger: 'blur' }
	]
})


// 提交表单数据的函数
const submitClick=()=>{
	postReq("/user/login",loginForm.value).then(resp=>{
		if(resp.data.result){
			let u=resp.data.data;
			u.token=resp.data.token;
			u.isAuth=true;
			//console.log(u);
			userStore.setAuthenticated(u)
			//登录成功后跳转到指定页面
			router.push('/home')
			
		}else{
			ElMessageBox.alert(resp.data.errMsg, '提示',{})
		}
  	})
}

</script>

<style>
	.login-wrapper {
		display: flex;
		justify-content: center;
		align-items: center;
		width: 100%;
		height: 100vh;
		background-color: #f5f7fa;
	}

	.login-container {
		border-radius: 15px;
		background-clip: padding-box;
		width: 350px;
		padding: 35px 35px 15px 35px;
		background: #fff;
		border: 1px solid #eaeaea;
		box-shadow: 0 0 25px #cac6c6;
	}

	.login_title {
		margin: 0px auto 40px auto;
		text-align: center;
		color: #505458;
	}
</style>