<template>
	<view class="forgot-page">
		<view class="header">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="title">重置密码</text>
			<text class="subtitle">请输入您的账户信息以重置密码</text>
		</view>

		<view class="form-wrapper">
			<uni-forms ref="form" :modelValue="formData">
				<uni-forms-item name="username">
					<uni-easyinput prefixIcon="person" v-model="formData.username" placeholder="请输入账号" :inputBorder="false" />
				</uni-forms-item>
				<uni-forms-item name="password">
					<uni-easyinput prefixIcon="locked" type="password" v-model="formData.password" placeholder="请输入新密码" :inputBorder="false" />
				</uni-forms-item>
				<uni-forms-item name="confirmPassword">
					<uni-easyinput prefixIcon="locked" type="password" v-model="formData.confirmPassword" placeholder="请再次输入新密码" :inputBorder="false" />
				</uni-forms-item>
			</uni-forms>
			
			<button class="submit-btn" @click="handleReset">确认重置</button>
		</view>

		<view class="footer">
			<text class="login-link" @click="goToLogin">返回登录</text>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'

// 表单数据
const formData = ref({
  username: '',
  password: '',
  confirmPassword: ''
})

// 重置密码方法
const handleReset = () => {
  if (!formData.value.username || !formData.value.password || !formData.value.confirmPassword) {
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
  
  // TODO: 实现重置密码逻辑
  uni.showLoading({
    title: '提交中...'
  })
  
  // 模拟重置密码请求
  setTimeout(() => {
    uni.hideLoading()
    uni.showToast({
      title: '密码重置成功',
      icon: 'success',
      success: () => {
        // 重置成功后返回登录页
        setTimeout(() => {
          uni.navigateBack()
        }, 1500)
      }
    })
  }, 1500)
}

// 返回登录页
const goToLogin = () => {
  uni.navigateBack()
}
</script>

<style lang="scss" scoped>
.forgot-page {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: flex-start;
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
	
	.submit-btn {
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