<template>
	<view class="login-page">
		<view class="header">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="title">智慧医院</text>
			<text class="subtitle">欢迎登录</text>
		</view>

		<view class="form-wrapper">
			<uni-forms ref="form" :modelValue="formData">
				<uni-forms-item name="idnumber">
					<uni-easyinput prefixIcon="person" v-model="formData.idnumber" placeholder="请输入身份证号" :inputBorder="false" />
				</uni-forms-item>
				<uni-forms-item name="password">
					<uni-easyinput prefixIcon="locked" type="password" v-model="formData.password" placeholder="请输入密码" :inputBorder="false" />
				</uni-forms-item>
			</uni-forms>
			
			<view class="extra-links">
				<text class="link" @click="goToForgot">忘记密码？</text>
				<text class="link" @click="goToRegister">立即注册</text>
			</view>

			<button class="login-btn" @click="handleLogin">登 录</button>
		</view>
	</view>
</template>

<script setup>
import { ref } from 'vue'

// 表单数据
const formData = ref({
  idnumber: '',
  password: ''
})

// 登录方法
const handleLogin = () => {
  if (!formData.value.idnumber || !formData.value.password) {
    uni.showToast({
      title: '请输入身份证号和密码',
      icon: 'none'
    });
    return;
  }

  uni.showLoading({
    title: '登录中...'
  });

  uni.request({
    url: '/api/yunapp/customer/login',
    method: 'POST',
    data: {
      idnumber: formData.value.idnumber,
      password: formData.value.password
    },
    success: (res) => {
      uni.hideLoading();
      if (res.statusCode === 200 && res.data.result) {
        uni.setStorageSync('token', res.data.data.token);
        uni.setStorageSync('customerId', res.data.data.customer.id);
        uni.showToast({
          title: '登录成功',
          icon: 'success'
        });
        setTimeout(() => {
            uni.switchTab({
              url: '/pages/index/index'
            });
        }, 1500);
      } else {
        uni.showToast({
          title: res.data.message || '登录失败',
          icon: 'none'
        });
      }
    },
    fail: (err) => {
      uni.hideLoading();
      uni.showToast({
        title: '网络请求失败',
        icon: 'none'
      });
    }
  });
}

// 跳转到注册页面
const goToRegister = () => {
  try {
    uni.navigateTo({
      url: '/pages/register/register',
      fail: (err) => {
        console.error('Navigation failed:', err)
        uni.showToast({
          title: '页面跳转失败',
          icon: 'none'
        })
      }
    })
  } catch (error) {
    console.error('Navigation error:', error)
    uni.showToast({
      title: '页面跳转失败',
      icon: 'none'
    })
  }
}

// 跳转到忘记密码页面
const goToForgot = () => {
  uni.navigateTo({
    url: '../forgot/forgot'
  })
}
</script>

<style lang="scss" scoped>
.login-page {
	display: flex;
	flex-direction: column;
	align-items: center;
	min-height: 100vh;
	background: linear-gradient(135deg, #f6f8fc 0%, #e9f0f8 100%);
	padding: 40rpx;
	box-sizing: border-box;
	position: relative;
	
	&::before {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 40%;
		background: linear-gradient(45deg, #007aff22 0%, #0056b322 100%);
		z-index: 0;
	}
}

.header {
	text-align: center;
	padding-top: 60rpx;
	padding-bottom: 80rpx;
	position: relative;
	z-index: 1;
	animation: fadeInDown 0.8s ease;
	
	.logo {
		width: 120rpx;
		height: 120rpx;
		border-radius: 24rpx;
		box-shadow: 0 8rpx 24rpx rgba(0,0,0,0.1);
		transform: translateY(0);
		transition: transform 0.3s ease;
		
		&:active {
			transform: translateY(2rpx);
		}
	}
	
	.title {
		font-size: 48rpx;
		font-weight: bold;
		background: linear-gradient(45deg, #007aff, #0056b3);
		-webkit-background-clip: text;
		color: transparent;
		margin-top: 20rpx;
		display: block;
	}
	
	.subtitle {
		font-size: 32rpx;
		color: #666;
		margin-top: 10rpx;
		display: block;
		position: relative;
		
		&::after {
			content: '';
			position: absolute;
			bottom: -10rpx;
			left: 50%;
			transform: translateX(-50%);
			width: 40rpx;
			height: 4rpx;
			background: linear-gradient(to right, #007aff, transparent);
			border-radius: 2rpx;
		}
	}
}

.form-wrapper {
	width: 100%;
	background: rgba(255, 255, 255, 0.9);
	padding: 40rpx 30rpx;
	border-radius: 24rpx;
	box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.08);
	backdrop-filter: blur(10rpx);
	position: relative;
	z-index: 1;
	animation: fadeInUp 0.8s ease;

	.uni-forms-item {
		background-color: #f7f7f7;
		border-radius: 40rpx;
		margin-bottom: 30rpx;
		transform: translateX(0);
		transition: transform 0.3s ease, box-shadow 0.3s ease;
		
		&:focus-within {
			transform: translateX(4rpx);
			box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.05);
		}
		
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
	
	.extra-links {
		display: flex;
		justify-content: space-between;
		font-size: 26rpx;
		color: #666;
		margin-bottom: 40rpx;
		padding: 0 10rpx;
		.link {
			color: #007aff;
			position: relative;
			transition: opacity 0.3s ease;
			
			&:active {
				opacity: 0.7;
			}
			
			&::after {
				content: '';
				position: absolute;
				bottom: -4rpx;
				left: 0;
				width: 100%;
				height: 2rpx;
				background: #007aff;
				transform: scaleX(0);
				transition: transform 0.3s ease;
				transform-origin: right;
			}
			
			&:active::after {
				transform: scaleX(1);
				transform-origin: left;
			}
		}
	}

	.login-btn {
		width: 100%;
		height: 90rpx;
		line-height: 90rpx;
		background: linear-gradient(to right, #007aff, #0056b3);
		color: #fff;
		border: none;
		border-radius: 45rpx;
		font-size: 32rpx;
		font-weight: bold;
		box-shadow: 0 10rpx 20rpx rgba(0, 122, 255, 0.2);
		transform: translateY(0);
		transition: all 0.3s ease;
		
		&:active {
			transform: translateY(2rpx);
			box-shadow: 0 5rpx 10rpx rgba(0, 122, 255, 0.1);
			opacity: 0.9;
		}
	}
}

@keyframes fadeInDown {
	from {
		opacity: 0;
		transform: translateY(-20rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

@keyframes fadeInUp {
	from {
		opacity: 0;
		transform: translateY(20rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}
</style>