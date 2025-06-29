<template>
    <div class="login-bg">
      <div class="login-wrapper">
        <el-form :model="loginForm" :rules="rules" ref="ruleForm" class="login-container login-form" label-position="left"
            label-width="80px" v-loading="loading" status-icon>
            <div class="login-logo">
              <!-- 可替换LOGO图片 -->
              <!-- <img src="/logo.png" alt="logo" /> -->
            </div>
            <h3 class="login_title">东软云医院系统登录</h3>
            <el-form-item label="用户名" prop="userName">
                <el-input v-model="loginForm.userName" placeholder="用户名" class="modern-input"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="password">
                <el-input type="password" v-model="loginForm.password" placeholder="密码" class="modern-input"></el-input>
            </el-form-item>
            <div style="display: flex; justify-content: center; width: 100%; margin-top: 24px;">
                    <el-button type="primary" class="modern-btn" style="width: 60%" @click.native.prevent="submitClick">登录</el-button>
            </div>
        </el-form>
      </div>
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
.login-bg {
  min-height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #e3f0ff 0%, #f8fafc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;
}
.login-container {
  border-radius: 22px;
  background-clip: padding-box;
  width: 420px;
  padding: 48px 38px 32px 38px;
  background: #fff;
  border: none;
  box-shadow: 0 8px 32px rgba(64,158,255,0.10), 0 1.5px 8px rgba(0,0,0,0.04);
  transition: box-shadow 0.3s;
  position: relative;
}
.login-container:hover {
  box-shadow: 0 12px 40px rgba(64,158,255,0.16), 0 2px 12px rgba(0,0,0,0.06);
}
.login-logo {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 12px;
}
.login-logo img {
  width: 56px;
  height: 56px;
  border-radius: 12px;
}
.login_title {
  margin: 0px auto 36px auto;
  text-align: center;
  color: #409EFF;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 1.5px;
}
.login-form .el-form-item {
  margin-bottom: 26px;
}
.login-form .el-form-item__label {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}
.modern-input .el-input__wrapper {
  border-radius: 10px;
  border: 1.5px solid #e0eaff;
  background: #f8fafc;
  font-size: 16px;
  transition: border 0.2s, box-shadow 0.2s;
}
.modern-input .el-input__wrapper:focus-within {
  border: 1.5px solid #409EFF;
  box-shadow: 0 0 0 2px #e3f0ff;
}
.modern-btn {
  border-radius: 10px;
  background: linear-gradient(90deg, #409EFF 0%, #66b1ff 100%);
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
  box-shadow: 0 2px 8px rgba(64,158,255,0.10);
  transition: background 0.2s, box-shadow 0.2s, transform 0.2s;
  height: 44px;
}
.modern-btn:hover {
  background: linear-gradient(90deg, #337ecc 0%, #409EFF 100%);
  box-shadow: 0 4px 16px rgba(64,158,255,0.16);
  transform: translateY(-2px) scale(1.03);
}
</style>
    