<template>
	<view class="register-page">
		<view class="header">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="title">创建新账户</text>
			<text class="subtitle">加入我们，体验便捷医疗服务</text>
    </view>

		<view class="form-wrapper">
			<uni-forms ref="form" :modelValue="formData">
				<uni-forms-item name="realName">
					<uni-easyinput prefixIcon="person" v-model="formData.realName" placeholder="请输入真实姓名" :inputBorder="false" />
				</uni-forms-item>
				<uni-forms-item name="idnumber">
					<uni-easyinput prefixIcon="auth" v-model="formData.idnumber" placeholder="请输入身份证号" :inputBorder="false" />
				</uni-forms-item>
				<uni-forms-item name="password">
					<uni-easyinput prefixIcon="locked" type="password" v-model="formData.password" placeholder="请输入密码" :inputBorder="false" />
				</uni-forms-item>
				<uni-forms-item name="confirmPassword">
					<uni-easyinput prefixIcon="locked" type="password" v-model="formData.confirmPassword" placeholder="请再次输入密码" :inputBorder="false" />
				</uni-forms-item>
			</uni-forms>
			
			<button class="register-btn" @click="handleRegister">立即注册</button>
    </view>

		<view class="footer">
			<text class="login-link" @click="goToLogin">已有账户？立即登录</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

// 表单数据
const formData = ref({
  realName: '',
  idnumber: '',
  password: '',
  confirmPassword: ''
})

// 注册方法
const handleRegister = () => {
  if (!formData.value.realName || !formData.value.idnumber || !formData.value.password || !formData.value.confirmPassword) {
    uni.showToast({
      title: '请填写完整信息',
      icon: 'none'
    })
    return
  }

  if (formData.value.password !== formData.value.confirmPassword) {
    uni.showToast({
      title: '两次输入的密码不一致',
      icon: 'none'
    })
    return
  }
  
  // 实现注册逻辑
  uni.showLoading({
    title: '注册中...'
  })
  
  const idNumber = formData.value.idnumber;
  let birthdate = '';
  let gender = 0;

  if (idNumber && idNumber.length === 18) {
    birthdate = `${idNumber.substring(6, 10)}-${idNumber.substring(10, 12)}-${idNumber.substring(12, 14)}`;
    const genderDigit = parseInt(idNumber.substring(16, 17));
    gender = genderDigit % 2 !== 0 ? 71 : 72;
  } else {
    uni.hideLoading();
    uni.showToast({
      title: '请输入有效的18位身份证号',
      icon: 'none'
    });
    return;
  }
  
  uni.request({
    url: '/api/customer/add',
    method: 'POST',
    data: {
      realName: formData.value.realName,
      idnumber: formData.value.idnumber,
      password: formData.value.password,
      birthdate: birthdate,
      gender: gender,
      createDate: new Date().toISOString().split('T')[0], // YYYY-MM-DD
      channel: 182
    },
    success: (res) => {
    uni.hideLoading()
      if (res.statusCode === 200 && res.data.result) {
    uni.showToast({
      title: '注册成功',
      icon: 'success',
      success: () => {
        // 注册成功后返回登录页
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      }
    })
      } else {
        uni.showToast({
          title: res.data.message || '注册失败',
          icon: 'none'
        })
      }
    },
    fail: (err) => {
      uni.hideLoading()
      uni.showToast({
        title: '网络请求失败',
        icon: 'none'
      })
    }
  })
}

// 返回登录页
const goToLogin = () => {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.register-page {
  display: flex;
  flex-direction: column;
  align-items: center;
	justify-content: flex-start; /* 从顶部开始排列 */
	min-height: 100vh;
	background-color: #f5f5f5;
	padding: 40rpx;
	box-sizing: border-box;
}

.header {
	text-align: center;
	padding-top: 40rpx;
	padding-bottom: 60rpx;
	width: 100%;

.logo {
		width: 100rpx;
		height: 100rpx;
		border-radius: 20rpx;
	}

  .title {
    font-size: 48rpx;
		font-weight: bold;
    color: #333;
		margin-top: 20rpx;
		display: block;
  }

  .subtitle {
    font-size: 28rpx;
		color: #999;
		margin-top: 10rpx;
		display: block;
  }
}

.form-wrapper {
  width: 100%;
	background-color: #fff;
	padding: 40rpx 30rpx;
    border-radius: 24rpx;
	box-shadow: 0 10rpx 20rpx rgba(0,0,0,0.05);

	.uni-forms-item {
		background-color: #f7f7f7;
		border-radius: 40rpx;
		margin-bottom: 30rpx;
		
		::v-deep .uni-easyinput__content {
			background-color: transparent !important;
			border: none !important;
			height: 90rpx;
			line-height: 90rpx;
		}
		
		::v-deep .uni-input-placeholder {
			color: #b0b0b0;
		}
		
		::v-deep .uni-icons {
			color: #b0b0b0 !important;
		}
  }

	.register-btn {
    width: 100%;
		height: 90rpx;
		line-height: 90rpx;
		background: linear-gradient(to right, #007aff, #0056b3);
    color: #fff;
		border: none;
		border-radius: 45rpx;
    font-size: 32rpx;
		font-weight: bold;
		box-shadow: 0 10rpx 20rpx rgba(0, 122, 255, 0.3);
		margin-top: 20rpx;

    &:active {
      opacity: 0.9;
		}
    }
  }

.footer {
	width: 100%;
    text-align: center;
	padding-top: 60rpx;
	
	.login-link {
		color: #007aff;
    font-size: 28rpx;
  }
}
</style> 