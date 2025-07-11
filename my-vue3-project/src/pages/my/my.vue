<template>
	<view class="my-page">
		<!-- 用户信息头部 -->
		<view class="header-section">
			<view class="user-avatar">
				<text class="avatar-text">{{ formData.name.charAt(0) || 'Hi' }}</text>
			</view>
			<view class="user-info">
				<text class="user-name">{{ formData.name || '访客' }}</text>
				<text class="user-id">ID: {{ formData.idCard || '未认证' }}</text>
			</view>
		</view>

		<scroll-view scroll-y class="content-scroll">
			<!-- 个人资料 -->
			<uni-card title="个人资料" thumbnail="/static/my/profile.png" :is-shadow="false">
				<uni-list :border="false">
					<uni-list-item title="姓名" :right-text="formData.name" :disabled="true"></uni-list-item>
					<uni-list-item title="性别" :right-text="formData.gender" :disabled="true"></uni-list-item>
					<uni-list-item title="身份证号" :right-text="formData.idCard" :disabled="true"></uni-list-item>
					<uni-list-item title="出生日期" :right-text="formData.dob" :disabled="true"></uni-list-item>
				</uni-list>
			</uni-card>
			
			<!-- 联系方式编辑 -->
			<uni-card title="联系方式" thumbnail="/static/my/contact.png" :is-shadow="false">
				<uni-forms :modelValue="formData" label-position="left" label-width="80px">
					<uni-forms-item label="手机号">
						<uni-easyinput v-model="formData.phone" placeholder="请输入手机号" />
					</uni-forms-item>
					<uni-forms-item label="家庭住址">
						<uni-easyinput type="textarea" v-model="formData.address" placeholder="请输入家庭住址" />
					</uni-forms-item>
				</uni-forms>
				<button class="save-btn" @click="handleSave">保存信息</button>
			</uni-card>

			<!-- 退出登录 -->
			<view class="logout-section">
				<button class="logout-btn" @click="handleLogout">退出登录</button>
			</view>
		</scroll-view>
	</view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'

const formData = ref({
  id: null,
  name: '',
  gender: '',
  idCard: '',
  dob: '',
  phone: '',
  address: ''
})

const fetchCustomerInfo = () => {
  const customerId = uni.getStorageSync('customerId');
  const token = uni.getStorageSync('token');

  if (!customerId || !token) {
    uni.showToast({ title: '请先登录', icon: 'none' });
    // 未登录时跳转到登录页
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/login/login' });
    }, 1000);
    return;
  }
  uni.showLoading({ title: '加载中...' });

  // 1. 尝试获取就诊卡信息
  uni.request({
    url: `/api/medicalcard/list/${customerId}`,
    method: 'GET',
    header: { 'token': token },
    success: (res) => {
      if (res.statusCode === 200 && res.data.result && res.data.data.length > 0) {
        // 如果有就诊卡，使用第一张卡的信息
        uni.hideLoading();
        const medicalCard = res.data.data[0];
        formData.value.id = medicalCard.id;
        formData.value.name = medicalCard.realname;
        formData.value.gender = medicalCard.gender === 71 ? '男' : '女';
        formData.value.idCard = medicalCard.idnumber;
        formData.value.dob = medicalCard.birthdate.split('T')[0];
        formData.value.phone = medicalCard.phone;
        formData.value.address = medicalCard.addr || '';
      } else {
        // 2. 如果没有就诊卡，回退获取customer基本信息
        uni.request({
          url: `/api/customer/info/${customerId}`,
          method: 'GET',
          header: { 'token': token },
          success: (customerRes) => {
            uni.hideLoading();
            if (customerRes.statusCode === 200 && customerRes.data.result) {
              const customer = customerRes.data.data;
              formData.value.id = null; // 确保id为空，以便保存时触发新建逻辑
              formData.value.name = customer.realName;
              formData.value.gender = customer.gender === 71 ? '男' : '女';
              formData.value.idCard = customer.idnumber;
              formData.value.dob = customer.birthdate.split('T')[0];
              formData.value.phone = customer.phone;
              formData.value.address = customer.address || '';
            } else {
              uni.showToast({ title: '加载基础信息失败', icon: 'none' });
            }
          },
          fail: () => {
            uni.hideLoading();
            uni.showToast({ title: '网络请求失败', icon: 'none' });
          }
        });
      }
    },
    fail: () => {
      uni.hideLoading();
      uni.showToast({ title: '网络请求失败', icon: 'none' });
    }
  });
};

onShow(() => {
  fetchCustomerInfo();
});

const handleSave = () => {
  const customerId = uni.getStorageSync('customerId');
  const token = uni.getStorageSync('token');

  if (!customerId || !token) {
    uni.showToast({ title: '请先登录', icon: 'none' });
    return;
  }
  uni.showLoading({ title: '保存中...' });

  const isUpdate = !!formData.value.id;
  const url = isUpdate ? '/api/medicalcard/update' : '/api/medicalcard/add';
  const genderMap = { '男': 71, '女': 72 };

  const payload = {
    customerId: customerId,
    realname: formData.value.name,
    gender: genderMap[formData.value.gender] || 0,
    idnumber: formData.value.idCard,
    birthdate: formData.value.dob,
    phone: formData.value.phone,
    addr: formData.value.address
  };
  if (isUpdate) {
    payload.id = formData.value.id;
  }

  uni.request({
    url: url,
    method: 'POST',
    header: { 'token': token },
    data: payload,
    success: (res) => {
      uni.hideLoading();
      if (res.statusCode === 200 && res.data.result) {
        uni.showToast({
          title: isUpdate ? '更新成功' : '就诊卡创建成功',
          icon: 'success'
        });
        if (!isUpdate) {
          fetchCustomerInfo(); // 创建成功后刷新，获取新卡id
        }
      } else {
        uni.showToast({ title: res.data.message || '操作失败', icon: 'none' });
      }
    },
    fail: () => {
      uni.hideLoading();
      uni.showToast({ title: '网络请求失败', icon: 'none' });
    }
  });
};

const handleLogout = () => {
	uni.showModal({
		title: '提示',
		content: '您确定要退出登录吗？',
		success: (res) => {
			if(res.confirm) {
				uni.removeStorageSync('token');
				uni.removeStorageSync('customerId');
				uni.reLaunch({
					url: '/pages/login/login'
				});
			}
		}
	});
};
</script>

<style lang="scss" scoped>
.my-page {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

.header-section {
	display: flex;
	align-items: center;
	padding: 80rpx 40rpx 40rpx;
	background: linear-gradient(to bottom, #007aff, #0056b3);
	
	.user-avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		background-color: rgba(255,255,255,0.3);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 30rpx;
		border: 4rpx solid #fff;
		
		.avatar-text {
			font-size: 50rpx;
			color: #fff;
			font-weight: bold;
		}
	}
	
	.user-info {
		display: flex;
		flex-direction: column;
		color: #fff;
		
		.user-name {
			font-size: 36rpx;
			font-weight: bold;
			margin-bottom: 10rpx;
		}
		
		.user-id {
			font-size: 26rpx;
			opacity: 0.8;
		}
	}
}

.content-scroll {
	flex: 1;
	height: 0;
	
	.uni-card {
		margin: 24rpx !important;
		border-radius: 20rpx !important;
		box-shadow: 0 4rpx 10rpx rgba(0,0,0,0.05) !important;
		border: none !important;

		::v-deep .uni-card__header-title-text {
			font-size: 32rpx !important;
			font-weight: bold;
		}
	}
}

.save-btn {
	height: 80rpx;
	line-height: 80rpx;
	background-color: #007aff;
	color: #fff;
	font-size: 30rpx;
	border-radius: 40rpx;
	margin-top: 20rpx;
	&:active {
		background-color: #0056b3;
	}
}

.logout-section {
	padding: 24rpx;
}

.logout-btn {
	height: 88rpx;
	line-height: 88rpx;
	background-color: #fff;
	color: #ff5733;
	font-size: 32rpx;
	border-radius: 20rpx;
	border: 1rpx solid #fce3de;
	&:active {
		background-color: #fff8f7;
	}
}
</style> 