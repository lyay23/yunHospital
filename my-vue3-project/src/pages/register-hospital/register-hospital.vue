<template>
  <view class="page-container">
    <!-- 进度条 -->
    <view class="steps-wrapper">
      <uni-steps :options="steps" :active="currentStep" active-color="#007aff" />
    </view>
    
    <!-- 内容区域 -->
    <scroll-view scroll-y class="content-scroll">
      <!-- Step 0: 选择就诊人 -->
      <view v-if="currentStep === 0" class="step-container">
        <uni-card :is-shadow="false" is-full>
          <text class="uni-h6">请选择一位就诊人</text>
        </uni-card>
        <view class="patient-list">
          <view 
            class="patient-item" 
            v-for="card in medicalCards" 
            :key="card.id" 
            @click="selectCard(card)"
            :class="{ 'is-active': selectedCard && selectedCard.id === card.id }"
          >
            <view class="item-left">
              <view class="avatar">{{ card.realname.charAt(0) }}</view>
              <view class="info">
                <view class="name-line">
                  <text class="name">{{ card.realname }}</text>
                  <uni-tag :text="card.gender === 71 ? '男' : '女'" :type="card.gender === 71 ? 'primary' : 'success'" size="mini" :inverted="true"></uni-tag>
                  <uni-tag :text="getAge(card.birthdate) + '岁'" type="default" size="mini"></uni-tag>
                </view>
                <text class="id-card">身份证: {{ card.idnumber }}</text>
              </view>
            </view>
            <view class="item-right">
              <uni-icons :type="selectedCard && selectedCard.id === card.id ? 'checkbox-filled' : 'circle'" size="24" :color="selectedCard && selectedCard.id === card.id ? '#007aff' : '#dcdcdc'"></uni-icons>
            </view>
          </view>
          <uni-load-more v-if="medicalCards.length === 0" status="noMore" :show-icon="false" content-text="暂无就诊人信息"></uni-load-more>
        </view>
      </view>

      <!-- Step 1: 选择科室 -->
      <view v-if="currentStep === 1" class="step-container department-container">
         <view class="department-layout">
           <scroll-view class="department-menu" scroll-y>
            <view 
               class="menu-item" 
               v-for="dept in departments" 
               :key="dept.id"
              :class="{ active: currentDept === dept.name }"
               @click="selectDepartment(dept)"
            >
              {{ dept.name }}
            </view>
          </scroll-view>
           <scroll-view class="department-content" scroll-y>
            <view 
               class="content-item"
               v-for="subDept in currentSubDepts"
               :key="subDept.id"
               :class="{ active: selectedSubDept && selectedSubDept.id === subDept.id }"
              @click="selectSubDepartment(subDept)"
            >
               {{ subDept.deptName }}
            </view>
             <uni-load-more v-if="currentSubDepts.length === 0" status="noMore" :show-icon="false" content-text="暂无科室信息"></uni-load-more>
          </scroll-view>
        </view>
      </view>

      <!-- Step 2: 选择医生 -->
      <view v-if="currentStep === 2" class="step-container">
        <view class="doctor-filters">
           <picker mode="date" :value="selectedDate" :start="today" @change="onDateChange">
             <view class="filter-item" :class="{'is-active': true}">
               <uni-icons type="calendar" size="16" color="#007aff"></uni-icons>
              <text>{{ selectedDate }}</text>
            </view>
          </picker>

           <view class="filter-tabs">
             <view class="tab-item" :class="{active: selectedPeriod === '上午'}" @click="selectPeriod('上午')">上午</view>
             <view class="tab-item" :class="{active: selectedPeriod === '下午'}" @click="selectPeriod('下午')">下午</view>
          </view>
        </view>

        <uni-card title="选择挂号级别" :is-shadow="false" padding="0">
          <view class="level-list">
            <view 
               class="level-item" 
               v-for="type in registerLevels" 
               :key="type.id"
               :class="{ active: selectedLevel && selectedLevel.id === type.id }"
               @click="selectRegisterType(type)"
            >
               {{ type.registName }} (¥{{ type.registFee }})
            </view>
          </view>
        </uni-card>

        <view class="doctor-list-wrapper">
          <view class="doctor-item-card" v-for="schedule in schedules" :key="schedule.id" @click="handleRegistration(schedule)">
            <view class="doctor-info">
               <view class="avatar">{{ schedule.realName.charAt(0) }}</view>
               <view class="details">
                 <view class="name-line">
                   <text class="name">{{ schedule.realName }}</text>
                   <text class="title">{{ schedule.noon }}</text>
                 </view>
                 <text class="department">{{ schedule.deptName }} | {{ schedule.registName }}</text>
              </view>
            </view>
             <view class="register-action">
               <view class="fee">¥{{ schedule.registFee }}</view>
               <view class="remaining">余 {{ schedule.registQuota - schedule.regNum }}</view>
               <button class="action-btn" :disabled="schedule.registQuota - schedule.regNum <= 0">
                 {{ schedule.registQuota - schedule.regNum > 0 ? '挂号' : '已满' }}
               </button>
              </view>
            </view>
           <uni-load-more v-if="schedules.length === 0" status="noMore" content-text="暂无排班信息"></uni-load-more>
        </view>
      </view>

      <!-- Step 3: 信息确认 -->
      <view v-if="currentStep === 3" class="step-container confirm-container">
        <uni-card :title="registrationComplete ? '✅ 挂号成功' : '请确认挂号信息'" :is-shadow="false">
          <uni-list :border="false">
            <uni-list-item title="姓名" :right-text="selectedCard.realname"></uni-list-item>
            <uni-list-item title="身份证号" :right-text="selectedCard.idnumber"></uni-list-item>
          </uni-list>
          <view class="divider"></view>
          <uni-list :border="false">
            <uni-list-item title="科室" :right-text="selectedSchedule.deptName"></uni-list-item>
            <uni-list-item title="医生" :right-text="selectedSchedule.realName"></uni-list-item>
            <uni-list-item title="号别" :right-text="selectedSchedule.registName"></uni-list-item>
            <uni-list-item title="就诊时间" :right-text="selectedSchedule.schedDate + ' ' + selectedSchedule.noon"></uni-list-item>
            <uni-list-item title="挂号费用">
              <template v-slot:footer>
                <text class="confirm-fee">¥{{ selectedSchedule.registFee }}</text>
              </template>
            </uni-list-item>
          </uni-list>
          <template v-if="registrationComplete">
            <view class="divider"></view>
            <uni-list :border="false">
                <uni-list-item title="状态" right-text="已挂号"></uni-list-item>
            </uni-list>
          </template>
        </uni-card>
        
        <view class="confirm-actions" v-if="!registrationComplete">
           <button class="confirm-btn" @click="confirmRegistration">确认挂号</button>
           <button class="cancel-btn" @click="cancelRegistration">返回修改</button>
        </view>
        <view class="confirm-actions" v-else>
           <button class="confirm-btn" @click="goToHome">返回首页</button>
        </view>
      </view>
    </scroll-view>

    <!-- 底部按钮 -->
    <view class="bottom-bar">
      <button class="bar-btn prev-btn" @click="prevStep" v-if="currentStep > 0 && currentStep < 3">
        <uni-icons type="arrow-left" size="18" color="#333"></uni-icons>
        上一步
      </button>
      <button class="bar-btn refresh-btn" @click="refreshData">
        <uni-icons type="refreshempty" size="18" color="#007aff"></uni-icons>
        刷新
      </button>
      <button class="bar-btn next-btn" @click="nextStep" :disabled="!canNext" v-if="currentStep < 2">
        下一步
        <uni-icons type="arrow-right" size="18" color="#fff"></uni-icons>
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { formatDate, getAge, getTimeRange } from '../../utils/utils.js'

// 步骤条
const steps = ref([
  { title: '选择就诊人' },
  { title: '选择科室' },
  { title: '选择医生' },
  { title: '信息确认' }
]);
const currentStep = ref(0);

// --- 数据模型 ---
const medicalCards = ref([]);
const selectedCard = ref(null);

const departments = ref([]);
const currentDept = ref('');
const currentSubDepts = ref([]);
const selectedSubDept = ref(null);

const today = ref(formatDate(new Date()));
const selectedDate = ref(formatDate(new Date()));

const registerLevels = ref([]);
const selectedLevel = ref(null);

const selectedPeriod = ref('上午'); // 默认选择上午

const schedules = ref([]); // 医生排班列表
const selectedSchedule = ref(null); // 最终选择的排班（用于确认）
const userInfo = ref({});
const registrationComplete = ref(false); // 新增：用于标记挂号是否已完成
// --- API 调用 ---

// 获取当前登录的用户信息
const fetchUserInfo = () => {
    const user = uni.getStorageSync('user');
    if (user) {
        userInfo.value = user;
    }
};

// 获取就诊卡列表
const fetchMedicalCards = () => {
  const customerId = uni.getStorageSync('customerId');
  if (!customerId) return;
  
  uni.request({
    url: `/api/medicalcard/list/${customerId}`,
    header: { 'token': uni.getStorageSync('token') },
    success: (res) => {
      if (res.data.result) {
        medicalCards.value = res.data.data;
      }
    }
  });
};

// 获取所有科室
const fetchDepartments = () => {
  uni.request({
    url: '/api/department/all',
    header: { 'token': uni.getStorageSync('token') },
    success: (res) => {
      if (res.data.result) {
        // 将科室按照deptCategoryID分组
        const deptList = res.data.data;
        const groupedDepts = {};
        
        deptList.forEach(dept => {
          const categoryId = dept.deptCategoryID;
          if (!groupedDepts[categoryId]) {
            groupedDepts[categoryId] = [];
          }
          groupedDepts[categoryId].push(dept);
        });
        
        departments.value = Object.entries(groupedDepts).map(([categoryId, subDepts]) => ({
          id: categoryId,
          name: getCategoryName(categoryId),
          subDepts: subDepts
        }));
        
        if (departments.value.length > 0) {
          selectDepartment(departments.value[0]);
        }
      }
    }
  });
};

// 获取科室类别名称
const getCategoryName = (categoryId) => {
  const categoryMap = {
    11: '内科',
    12: '外科',
    14: '儿科',
    15: '传染科',
    16: '妇产科',
    17: '男科',
    18: '精神科',
    19: '皮肤科',
    20: '中医科',
    21: '肿瘤科',
    22: '骨科',
    23: '五官科',
    24: '康复科',
    25: '麻醉科',
    26: '营养科',
    27: '医技科',
    28: '影像科',
    29: '其他科室'
  };
  return categoryMap[categoryId] || `未知科室(${categoryId})`;
};

// 获取挂号级别
const loadRegisterLevels = () => {
    uni.request({
        url: '/api/registlevel/page?count=100&pn=1',
        header: { 'token': uni.getStorageSync('token') },
        success: (res) => {
            if (res.data.result) {
                registerLevels.value = res.data.data.records;
            }
        }
    });
};

// 获取排班计划（医生列表）
const loadSchedules = () => {
    console.log('--- 开始加载排班信息 ---');
    console.log('当前选中科室:', selectedSubDept.value?.deptName);
    console.log('当前选中日期:', selectedDate.value);
    console.log('当前选中级别:', selectedLevel.value?.registName);
    console.log('当前选中午别:', selectedPeriod.value);

    if (!selectedSubDept.value || !selectedDate.value || !selectedLevel.value || !selectedPeriod.value) {
        console.log('参数不完整，取消查询:', {
            科室: selectedSubDept.value?.deptName,
            日期: selectedDate.value,
            级别: selectedLevel.value?.registName,
            时段: selectedPeriod.value
        });
        schedules.value = [];
        return;
    }
    
    uni.showLoading({ title: '正在查询医生...' });
    
    const params = {
        deptId: selectedSubDept.value.id,
        regLevel: selectedLevel.value.id,
        start: selectedDate.value,
        end: selectedDate.value,
        noon: selectedPeriod.value,
        count: 100,
        pn: 1
    };

    const queryString = Object.entries(params).map(([key, value]) => `${key}=${encodeURIComponent(value)}`).join('&');

    console.log('向后端发起的请求 URL:', `/api/scheduling/page?${queryString}`);

    uni.request({
        url: `/api/scheduling/page?${queryString}`,
        header: { 'token': uni.getStorageSync('token') },
        success: (res) => {
            console.log('后端返回的原始数据:', res);
            if (res.data.result) {
                schedules.value = res.data.data.records;
                console.log('成功获取排班数据:', schedules.value);
            } else {
                schedules.value = [];
                uni.showToast({ title: res.data.errMsg || '查询失败', icon: 'none' });
            }
        },
        fail: (err) => {
            console.error('API 请求失败:', err);
            uni.showToast({ title: '网络请求失败', icon: 'none' });
        },
        complete: () => {
            uni.hideLoading();
        }
    });
};


// --- 用户交互 ---

const selectCard = (card) => {
  selectedCard.value = card;
};

const selectDepartment = (dept) => {
  currentDept.value = dept.name;
  currentSubDepts.value = dept.subDepts || [];
  selectedSubDept.value = null;
};

const selectSubDepartment = (subDept) => {
  selectedSubDept.value = subDept;
  // 如果已经在第二步，自动触发排班查询
  if (currentStep.value === 2) {
    loadSchedules();
  }
};

const onDateChange = (e) => {
  selectedDate.value = e.detail.value;
};

const selectRegisterType = (type) => {
  selectedLevel.value = type;
};

const selectPeriod = (period) => {
  selectedPeriod.value = period;
};


// 最终确认挂号
const confirmRegistration = () => {
    uni.showLoading({ title: '正在提交挂号...' });

    const param = {
        "register": {
            "medicalCardId": selectedCard.value.id,
            "age": getAge(selectedCard.value.birthdate),
            "ageType": "岁",
            "visitDate": selectedSchedule.value.schedDate,
            "noon": selectedSchedule.value.noon,
            "deptID": selectedSchedule.value.deptID,
            "userID": selectedSchedule.value.userID,
            "registLeID": selectedSchedule.value.registlevelId,
            "settleID": selectedCard.value.cardtype || 1, // 如果就诊卡没有结算类型，默认设置为1(自费)
            "isBook": "1", // 1代表预约
            "registerID": null, // 小程序挂号没有操作员
            "timeInterval": getTimeRange(),
            "channel": 187 // 187代表小程序
        },
        "scheduling": selectedSchedule.value
    };

    uni.request({
        url: '/api/register/add',
        method: 'POST',
        header: { 'token': uni.getStorageSync('token'), 'Content-Type': 'application/json' },
        data: param,
        success: (res) => {
            if (res.data.result) {
                uni.showToast({ title: '挂号成功！', icon: 'success' });
                registrationComplete.value = true;
                // 成功后不再跳转，而是留在当前页显示成功状态
            } else {
                uni.showToast({ title: res.data.errMsg || '挂号失败', icon: 'none' });
            }
        },
        fail: () => {
            uni.showToast({ title: '请求失败', icon: 'none' });
        },
        complete: () => {
            uni.hideLoading();
        }
    });
};

// 点击医生列表中的"挂号"按钮
const handleRegistration = (schedule) => {
    if (schedule.registQuota - schedule.regNum <= 0) {
        uni.showToast({ title: '该医生号已满', icon: 'none' });
        return;
    }
    selectedSchedule.value = schedule;
    currentStep.value = 3; // 跳转到信息确认页
};


const cancelRegistration = () => {
  currentStep.value = 2; // 返回医生选择页面
};

const goToHome = () => {
  uni.switchTab({
    url: '/pages/index/index'
  });
};


// --- 流程控制 ---

const canNext = computed(() => {
  if (currentStep.value === 0) {
    return !!selectedCard.value;
  }
  if (currentStep.value === 1) {
    return !!selectedSubDept.value;
  }
  return false;
});

const nextStep = () => {
  if (canNext.value && currentStep.value < steps.value.length - 1) {
    currentStep.value++;
    // 进入选择医生步骤时，加载挂号级别
    if (currentStep.value === 2) {
      loadRegisterLevels();
      // 由于我们已经设置了默认值，不需要在这里重复设置
      // 但是需要检查是否有选择科室
      if (selectedSubDept.value) {
        loadSchedules();
      }
    }
  }
};

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--;
  }
};

const refreshData = () => {
    uni.showLoading({ title: '刷新中...' });
    if (currentStep.value === 0) fetchMedicalCards();
    if (currentStep.value === 1) fetchDepartments();
    if (currentStep.value === 2) loadSchedules();
    setTimeout(() => uni.hideLoading(), 500);
};

// --- 生命周期与监听 ---

// 监听关键参数变化，自动加载排班信息
watch([selectedSubDept, selectedDate, selectedLevel, selectedPeriod], ([newSubDept, newDate, newLevel, newPeriod], [oldSubDept, oldDate, oldLevel, oldPeriod]) => {
  console.log('参数变化:', {
    科室: newSubDept?.deptName,
    日期: newDate,
    级别: newLevel?.registName,
    时段: newPeriod
  });
  
  if (currentStep.value === 2 && newSubDept && newDate && newLevel && newPeriod) {
    console.log('开始加载排班信息');
    loadSchedules();
  }
}, { deep: true, immediate: true });

onShow(() => {
  fetchUserInfo();
  fetchMedicalCards();
  if (currentStep.value === 1) {
    fetchDepartments();
  }
});
</script>

<style scoped>
/* 全局页面样式 */
.page-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.steps-wrapper {
    padding: 20rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #ebebeb;
  }

.content-scroll {
    flex: 1;
  height: 0; /*  重要：让 scroll-view 在 flex 布局中正确计算高度 */
    }

.step-container {
  padding: 24rpx;
}

/* 底部操作栏 */
.bottom-bar {
        display: flex;
        justify-content: space-between;
        align-items: center;
  padding: 20rpx 24rpx;
  background-color: #ffffff;
  border-top: 1rpx solid #ebebeb;
}
.bar-btn {
        display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
            font-size: 28rpx;
  border-radius: 40rpx;
  padding: 0 40rpx;
  height: 80rpx;
  line-height: 80rpx;
}
.bar-btn uni-icons {
  margin-right: 8rpx;
}
.prev-btn {
  background-color: #f0f0f0;
            color: #333;
}
.refresh-btn {
  color: #007aff;
  background: transparent;
  border: 1rpx solid #007aff;
}
.next-btn {
  background-color: #007aff;
  color: #ffffff;
  flex-grow: 1;
  margin-left: 20rpx;
      }
.next-btn[disabled] {
  background-color: #cce5ff;
  color: #ffffff;
}
.next-btn uni-icons {
  margin-left: 8rpx;
  margin-right: 0;
}


/* 步骤 0: 就诊人 */
.patient-list {
  margin-top: 20rpx;
}
.patient-item {
          display: flex;
  align-items: center;
          justify-content: space-between;
  background-color: #ffffff;
  padding: 30rpx;
  border-radius: 16rpx;
        margin-bottom: 20rpx;
  transition: all 0.2s ease;
  border: 2rpx solid #ffffff;
}
.patient-item.is-active {
  border-color: #007aff;
  background-color: #e6f2ff;
}
.item-left {
          display: flex;
  align-items: center;
}
.avatar {
  width: 80rpx;
            height: 80rpx;
            line-height: 80rpx;
            text-align: center;
  background-color: #007aff;
  color: #ffffff;
  font-size: 36rpx;
  font-weight: bold;
  border-radius: 50%;
  margin-right: 24rpx;
}
.info .name-line {
  display: flex;
  align-items: center;
  margin-bottom: 8rpx;
}
.info .name {
  font-size: 32rpx;
  font-weight: bold;
  margin-right: 16rpx;
}
.info .id-card {
  font-size: 24rpx;
  color: #999999;
            }


/* 步骤 1: 科室 */
.department-container {
  background-color: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  height: calc(100vh - 250rpx); /* 根据实际情况调整 */
}
.department-layout {
          display: flex;
  height: 100%;
}
.department-menu {
  width: 200rpx;
  height: 100%;
  background-color: #f5f5f5;
}
.menu-item {
  padding: 30rpx;
            text-align: center;
            font-size: 28rpx;
            color: #666;
  position: relative;
}
.menu-item.active {
  background-color: #ffffff;
  color: #007aff;
  font-weight: bold;
}
.menu-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 8rpx;
  height: 40rpx;
  background-color: #007aff;
}
.department-content {
  flex: 1;
  height: 100%;
  padding: 20rpx;
}
.content-item {
  padding: 24rpx;
  font-size: 28rpx;
  border-radius: 8rpx;
  margin-bottom: 16rpx;
}
.content-item.active {
  background-color: #e6f2ff;
  color: #007aff;
  font-weight: bold;
}

/* 步骤 2: 医生 */
.doctor-filters {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ffffff;
  padding: 20rpx;
  border-radius: 16rpx;
          margin-bottom: 20rpx;
}
.filter-item {
            display: flex;
            align-items: center;
  font-size: 28rpx;
  color: #007aff;
}
.filter-item uni-icons {
  margin-right: 8rpx;
}
.filter-tabs {
  display: flex;
  background-color: #f0f0f0;
  border-radius: 8rpx;
  padding: 4rpx;
            }
.tab-item {
  padding: 12rpx 30rpx;
  font-size: 26rpx;
  color: #666;
  border-radius: 6rpx;
  transition: all 0.2s ease;
}
.tab-item.active {
  background-color: #ffffff;
  color: #007aff;
                font-weight: bold;
}
.level-list {
  display: flex;
  flex-wrap: wrap;
  padding: 20rpx;
              }
.level-item {
  background-color: #f5f5f5;
  color: #333;
  padding: 16rpx 24rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  margin-right: 20rpx;
  margin-bottom: 20rpx;
  border: 1rpx solid #f5f5f5;
}
.level-item.active {
  background-color: #e6f2ff;
  color: #007aff;
  border-color: #007aff;
  font-weight: bold;
          }

.doctor-list-wrapper {
  margin-top: 20rpx;
}
.doctor-item-card {
            display: flex;
            justify-content: space-between;
            align-items: center;
  background-color: #ffffff;
  padding: 30rpx;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
}
.doctor-item-card .doctor-info {
  display: flex;
  align-items: center;
}
.doctor-item-card .details {
  display: flex;
  flex-direction: column;
}
.doctor-item-card .details .name-line {
  display: flex;
  align-items: baseline;
  margin-bottom: 8rpx;
              }
.doctor-item-card .details .name {
  font-size: 32rpx;
  font-weight: bold;
  margin-right: 16rpx;
}
.doctor-item-card .details .title {
  font-size: 24rpx;
  color: #999;
}
.doctor-item-card .details .department {
  font-size: 26rpx;
  color: #666;
          }

.register-action {
  text-align: right;
}
.register-action .fee {
  font-size: 32rpx;
            font-weight: bold;
  color: #ff5733;
}
.register-action .remaining {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
          }
.register-action .action-btn {
  background-color: #007aff;
  color: #fff;
  font-size: 24rpx;
  padding: 8rpx 24rpx;
  border-radius: 30rpx;
  margin: 0;
            line-height: 1.5;
}
.register-action .action-btn[disabled] {
  background-color: #c0c4cc;
}


/* 步骤 3: 确认 */
.confirm-container .uni-card {
  margin: 0;
}
.divider {
  height: 1rpx;
  background-color: #ebebeb;
  margin: 20rpx 0;
}
.confirm-fee {
  font-size: 32rpx;
  color: #ff5733;
  font-weight: bold;
            }
.confirm-actions {
  margin-top: 40rpx;
  padding: 0 24rpx;
}
        .confirm-btn, .cancel-btn {
          width: 100%;
          height: 88rpx;
          line-height: 88rpx;
  font-size: 32rpx;
          border-radius: 44rpx;
        }
        .confirm-btn {
  background-color: #007aff;
  color: white;
        }
        .cancel-btn {
  margin-top: 20rpx;
  background-color: #f0f0f0;
  color: #333;
        }

</style> 