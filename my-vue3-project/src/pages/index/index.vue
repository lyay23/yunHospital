<template>
	<view class="home-container">
		<!-- 自定义导航栏 -->
		<view class="navbar">
			<text class="hospital-name">智慧医院</text>
			<uni-icons type="notification" size="24" color="#333"></uni-icons>
    </view>

		<!-- 内容区域 -->
		<scroll-view scroll-y class="content-scroll">
			<!-- 轮播图 -->
			<view class="swiper-section">
				<uni-swiper-dot :info="swiperList" :current="swiperCurrent" field="content" :mode="swiperDotMode">
					<swiper class="swiper-box" @change="handleSwiperChange" :autoplay="true" :interval="3000" :duration="500" :circular="true">
						<swiper-item v-for="(item, index) in swiperList" :key="index">
							<view class="swiper-item">
								<image :src="item.url" mode="aspectFill" class="swiper-image" />
        </view>
						</swiper-item>
					</swiper>
				</uni-swiper-dot>
      </view>

			<!-- 功能网格 -->
			<view class="grid-section">
				<uni-grid :column="4" :showBorder="false" :square="true">
					<uni-grid-item v-for="(item, index) in gridList" :index="index" :key="index" @click="handleGridClick(item.url)">
						<view class="grid-item-box">
							<image class="grid-icon" :src="item.icon" mode="aspectFit"></image>
							<text class="grid-text">{{ item.text }}</text>
            </view>
					</uni-grid-item>
				</uni-grid>
      </view>

			<!-- 医院动态 -->
			<view class="news-section">
				<uni-card title="医院动态" extra="更多 >">
					<uni-list>
						<uni-list-item v-for="(item, index) in newsList" :key="index" :title="item.title" :note="item.date" clickable></uni-list-item>
					</uni-list>
				</uni-card>
          </view>
		</scroll-view>
  </view>
</template>

<script setup>
import { ref } from 'vue'

const swiperCurrent = ref(0)
const swiperDotMode = ref('dot')
const swiperList = ref([
	{ url: 'https://bu.dusays.com/2025/06/30/6861dd518bd76.png', content: '内容 A' },
	{ url: 'https://bu.dusays.com/2025/06/30/6861dd5409c76.png', content: '内容 B' },
	{ url: 'https://bu.dusays.com/2025/06/30/6861dd542e81b.png', content: '内容 C' }
])

const gridList = ref([
	{ icon: '/static/grid/register-blue.png', text: '预约挂号', url: '/pages/register-hospital/register-hospital' },
	{ icon: '/static/grid/report.png', text: '报告查询', url: '' },
	{ icon: '/static/grid/doctor.png', text: '医生介绍', url: '' },
	{ icon: '/static/grid/health-blue.png', text: '健康科普', url: '' }
])

const newsList = ref([
  {
    title: '我院成功开展首例微创心脏瓣膜置换术',
    date: '2024-05-20'
  },
  {
    title: '关于2024年端午节门诊安排的通知',
    date: '2024-05-18'
  },
  {
    title: '智慧医院APP全新上线，一键解决就医难题',
    date: '2024-05-15'
  }
])

const handleSwiperChange = (e) => {
	swiperCurrent.value = e.detail.current
}

const handleGridClick = (url) => {
	if (!url) {
		uni.showToast({ title: '功能开发中...', icon: 'none' })
		return
	}
  uni.switchTab({
		url: url,
		fail: () => {
			uni.navigateTo({ url: url })
		}
  })
}
</script>

<style lang="scss" scoped>
.home-container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background: linear-gradient(135deg, #f6f8fc 0%, #e9f0f8 100%);
}

.navbar {
    display: flex;
	justify-content: space-between;
    align-items: center;
	padding: 80rpx 30rpx 20rpx;
	background: linear-gradient(to right, #ffffff, #f8faff);
	box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.03);
	position: relative;
	z-index: 1;
	
	.hospital-name {
		font-size: 40rpx;
      font-weight: bold;
		background: linear-gradient(45deg, #007aff, #0056b3);
		-webkit-background-clip: text;
		color: transparent;
		position: relative;
		
		&::after {
			content: '';
			position: absolute;
			bottom: -6rpx;
			left: 0;
			width: 40%;
			height: 4rpx;
			background: linear-gradient(to right, #007aff, transparent);
			border-radius: 2rpx;
		}
	}
}

.content-scroll {
	flex: 1;
	height: 0;
}

.swiper-section {
	padding: 0 30rpx;
	margin-top: 20rpx;

	.swiper-box {
		height: 300rpx;
		border-radius: 20rpx;
		overflow: hidden;
		box-shadow: 0 8rpx 20rpx rgba(0,0,0,0.08);
		transform: translateY(0);
		transition: transform 0.3s ease;
		
		&:active {
			transform: translateY(2rpx);
		}
	}
	
	.swiper-item {
		width: 100%;
		height: 100%;
		
		.swiper-image {
			width: 100%;
			height: 100%;
			border-radius: 20rpx;
			transition: transform 0.3s ease;
			
			&:active {
				transform: scale(0.98);
      }
    }
	}
	
	::v-deep .uni-swiper__dots-box {
		bottom: 20rpx !important;
	}
}

.grid-section {
	padding: 20rpx;
	margin: 30rpx;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 24rpx;
	box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.05);
	backdrop-filter: blur(10rpx);

	.grid-item-box {
  display: flex;
  flex-direction: column;
          align-items: center;
		justify-content: center;
		height: 100%;
		padding: 20rpx 0;
		transition: transform 0.2s ease;
		
		&:active {
			transform: scale(0.95);
		}
		
		.grid-icon {
			width: 100rpx;
			height: 100rpx;
			margin-bottom: 20rpx;
			transition: transform 0.2s ease;
			
			&:hover {
				transform: translateY(-2rpx);
            }
          }

		.grid-text {
            font-size: 28rpx;
            color: #333;
			font-weight: 500;
        }
      }
    }

.news-section {
	padding: 0 30rpx;
	margin-bottom: 30rpx;
	
	::v-deep .uni-card {
		border-radius: 20rpx !important;
		box-shadow: 0 8rpx 24rpx rgba(0,0,0,0.06) !important;
		border: none !important;
		background: rgba(255, 255, 255, 0.95);
		backdrop-filter: blur(10rpx);
		
		.uni-card__header {
			position: relative;
			
			&::after {
				content: '';
				position: absolute;
				bottom: 0;
				left: 20rpx;
				right: 20rpx;
				height: 1rpx;
				background: linear-gradient(to right, #e0e0e0, transparent);
			}
		}
	}
	
	::v-deep .uni-list-item {
		transition: background-color 0.2s ease;
		
		&:active {
			background-color: #f5f7fa;
}

		.uni-list-item__content-title {
			font-size: 30rpx !important;
			color: #333 !important;
			font-weight: 500;
          }

		.uni-list-item__content-note {
			font-size: 24rpx !important;
			color: #999 !important;
    }
  }
}
</style>
