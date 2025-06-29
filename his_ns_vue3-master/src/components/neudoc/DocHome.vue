<template>
<el-container>
	<el-aside  v-show="isShowMenu"  width="250px" class="sidebar-card">
		<el-row>
			<el-col :span="12" style="text-align: left">
			  <el-tag size="mini" style="width: 100%;" class="sidebar-section-title">患者选择：</el-tag>
			</el-col>
			<el-col :span="12" style="text-align: right">
				<el-button type="primary" :icon="Refresh" size="small"  @click="refreshPatientLists"
					style="margin-right: 5px"></el-button>
			</el-col>
		</el-row>
		<!--挂号信息 start -->
		<el-row>
			<el-col :span="24" >
				<el-input v-model="kw" placeholder="请输入内容" class="input-with-select sidebar-search">
					<template #prepend>患者名:</template>
					<template #append><el-button :icon="Search" @click="searchPatients"/></template>
				</el-input>	
			</el-col>
		</el-row>
		<!--挂号信息 end -->
		
		<!-- 未患者列表 start -->
		<el-row>
			<el-col :span="24" style="margin-top: 20px;">
				<el-tag size="mini" style="width: 100%" class="sidebar-section-title">未诊患者：</el-tag>
				<el-table :data="data.records" style="width:100%;"
					v-loading="loading" @row-click="handleRowClick" class="sidebar-table">
					<el-table-column prop="caseNumber" label="病历号" align="center"/>
					<el-table-column prop="realName" label="姓名"  align="center" />
					<el-table-column prop="gender" label="性别"   width="70" align="center">
						<template #default="scope">
							{{scope.row.gender==71?"男":"女"}}
						</template>
					</el-table-column>
					<el-table-column prop="age" label="年龄"   align="center"/>
				</el-table> 
			</el-col>
		</el-row>
		<!-- 未患者列表 end -->
		
		<!-- 已患者列表 start -->
		<el-row>
			<el-col :span="24" style="margin-top: 20px;">
				<el-tag size="mini" style="width: 100%" class="sidebar-section-title">已诊患者：</el-tag>
				<el-table :data="data2.records" style="width:100%;"
					v-loading="loading2" @row-click="handleDiagnosedRowClick" class="sidebar-table">
					<el-table-column prop="caseNumber" label="病历号" align="center"/>
					<el-table-column prop="realName" label="姓名"  align="center" />
					<el-table-column prop="gender" label="性别"   width="70" align="center">
						<template #default="scope">
							{{scope.row.gender==71?"男":"女"}}
						</template>
					</el-table-column>
					<el-table-column prop="age" label="年龄"   align="center"/>
				</el-table> 
			</el-col>
		</el-row>
		<!-- 已患者列表 end -->
		
	</el-aside>
	<el-main style="padding-top: 0px;">
		<div class="patient-info-bar">
			<el-button :icon="isShowMenu ? Hide : View" text circle @click="isShowMenu = !isShowMenu"
				:title="isShowMenu ? '隐藏患者栏' : '显示患者栏'" />

			<div class="patient-details" v-if="currentPatient">
				<el-space :size="20" spacer="|">
					<span><strong>姓名:</strong> {{ currentPatient.realName }}</span>
					<span><strong>病历号:</strong> {{ currentPatient.caseNumber }}</span>
					<span><strong>性别:</strong> {{ currentPatient.gender === 71 ? '男' : '女' }}</span>
					<span><strong>年龄:</strong> {{ currentPatient.age }}</span>
				</el-space>
			</div>
			<div v-else class="placeholder-text">
				请从左侧选择一位患者
			</div>

			<el-button type="success" v-if="currentPatient && isOver" @click="finishConsultation" :icon="Select" round>
				诊毕
			</el-button>
		</div>
		<el-row>
			<el-tabs v-model="activeName" type="card" class="demo-tabs" @tab-click="handleClick">
			    <el-tab-pane label="病历首页" name="1">
					<Medicalrecord ref="medicalRecordComp" :patient="currentPatient" :is-diagnosed="isDiagnosed"></Medicalrecord>
				</el-tab-pane>
			    <el-tab-pane label="检查申请" name="2">
					<Checkapply ref="checkApplyComp" :patient="currentPatient" :is-diagnosed="isDiagnosed" v-if="activeName === '2'"></Checkapply>
				</el-tab-pane>
			    <el-tab-pane label="检验申请" name="3">
					<Testapply ref="testApplyComp" :patient="currentPatient" :is-diagnosed="isDiagnosed" v-if="activeName === '3'"></Testapply>
				</el-tab-pane>
				<el-tab-pane label="门诊确诊" name="4">
					<Doc04 :patient="currentPatient" :is-diagnosed="isDiagnosed" v-if="activeName === '4'" />
				</el-tab-pane>
				<el-tab-pane label="处置申请" name="5">
					<Disposalapply :patient="currentPatient" :is-diagnosed="isDiagnosed" v-if="activeName === '5'" />
				</el-tab-pane>
				<el-tab-pane label="处方申请" name="6">
					<Prescriptionapply :patient="currentPatient" :is-diagnosed="isDiagnosed" v-if="activeName === '6'"/>
				</el-tab-pane>
				<el-tab-pane label="费用查询*" name="7">
					<CostQuery :patient="currentPatient" v-if="activeName === '7'"/>
				</el-tab-pane>
			</el-tabs>
		</el-row>
		
		<el-row>


		</el-row>
		
	</el-main>
</el-container>
</template>

<script setup>
import Medicalrecord from '../neudoc/his/Doc01.vue'
import Checkapply from '../neudoc/his/Doc02.vue'
import Testapply from '../neudoc/his/Doc03.vue'
import Doc04 from './his/Doc04.vue'
import Disposalapply from './his/Doc05.vue'
import Prescriptionapply from './his/Doc06.vue'
import CostQuery from './his/doc07.vue'

import { ref,onMounted } from 'vue'
import { fetchData,postReq } from '../../utils/api'
import router from '../../router';
import { formatDate } from '../../utils/utils.js'
import { Search,Refresh, View, Hide, Select } from '@element-plus/icons-vue'	
import { useUserStore } from '../../store/user.js'
import { ElMessageBox, ElMessage } from 'element-plus'




//获取用户仓库对象
const userStore=useUserStore()
//data start
const loading=ref(false)
const loading2=ref(false)
const data = ref({})
const data2 = ref({})
const medicalRecordComp = ref(null)
const checkApplyComp = ref(null)
const testApplyComp = ref(null)
//搜索框
const kw=ref('')
//是否显示患者列表
const isShowMenu=ref(true)
const currentPatient = ref(null)
const isDiagnosed = ref(false);
//是否显示诊毕
const isOver=ref(true)

onMounted(async () => {
  loadData(1)
  loadData2(1)
});


//加载当天未就诊患者数据 (包括已挂号 state=1 和已缴费 state=2)
async function loadData(pn){
	loading.value = true;
	const date = formatDate(new Date());
	const commonParams = `count=100&pn=${pn}&regDate=${date}&deptId=${userStore.getUserInfo.value.deptID}&docId=${userStore.getUserInfo.value.id}${kw.value ? `&keyword=${kw.value}` : ''}`;
	
	try {
		// 并行获取 state=1 和 state=2 的患者
		const [result1, result2] = await Promise.all([
			fetchData(`/register/page?state=1&${commonParams}`, null),
			fetchData(`/register/page?state=2&${commonParams}`, null)
		]);

		if (result1.result && result2.result) {
			const records1 = result1.data.records || [];
			const records2 = result2.data.records || [];
			
			const combinedRecords = [...records1, ...records2];
			// 按挂号ID降序排列，ID越大越新
			combinedRecords.sort((a, b) => b.id - a.id);

			data.value = {
				...result1.data, // 使用第一个结果作为分页基础
				records: combinedRecords,
				total: result1.data.total + result2.data.total,
			};

		} else {
			ElMessage.error('加载未诊患者列表失败');
			if(result1.errMsg === '未登录' || result2.errMsg === '未登录') {
				router.push('/login');
			}
		}
	} catch (error) {
		ElMessage.error('网络错误，加载未诊患者列表失败');
		console.error(error);
	} finally {
		loading.value = false;
	}
}


//加载当天已就诊患者数据
async function loadData2(pn){
	loading2.value=true
	var date=formatDate(new Date())
	let url=`/register/page?count=100&pn=${pn}&state=3&regDate=${date}`
	url+=`&deptId=${userStore.getUserInfo.value.deptID}`
	url+=`&docId=${userStore.getUserInfo.value.id}`
	if(kw.value)
		url+=`&keyword=${kw.value}`

	const result = await fetchData(url,null);
	if(result.result){
		data2.value = result.data
		loading2.value=false
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')		
	}	
}

const activeName = ref('1')
//门诊tab 切换 
const handleClick=()=>{
	
}

const handleRowClick=(val, column, event)=>{
	currentPatient.value = val
	isOver.value=true
	isDiagnosed.value = false;
}

const handleDiagnosedRowClick=(val, column, event)=>{
	isOver.value=false
	currentPatient.value = val;
	isDiagnosed.value = true;
}

function searchPatients() {
	loadData(1)
	loadData2(1)
}

function refreshPatientLists() {
    loadData(1);
    loadData2(1);
}

const finishConsultation = async () => {
    if (!currentPatient.value) {
        ElMessage.error("请先选择一位患者");
        return;
    }

    try {
		// 先保存病历，并等待结果
		await medicalRecordComp.value.save()
		// 确认诊毕
		await ElMessageBox.confirm(
			`确定要结束对【${currentPatient.value.realName}】的诊断吗？`,
			'诊毕确认',
			{
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning',
			}
		);

        // 发送诊毕请求
        const res = await postReq('/register/finish', { id: currentPatient.value.id });
        if (res.data.result) {
            ElMessage.success('诊毕成功');
            refreshPatientLists();
			currentPatient.value = null;
			isOver.value=false
			activeName.value='1'
        } else {
            ElMessage.error(res.data.errMsg || '诊毕失败');
        }
    } catch (error) {
		// 打印真实错误到控制台，以便调试
		console.error("诊毕过程中发生错误:", error);
		
		if (error && error.message && !error.message.includes('No patient selected') && error !== 'cancel') {
           // ElMessage.error('操作失败或已取消'); // 暂时注释掉，让错误在控制台可见
        } else if (error === 'cancel') {
			ElMessage.info('操作已取消');
		}
    }
};

</script>
<style>
.input-with-select .el-input-group__prepend {
  min-width: 60px;
  white-space: nowrap;
  font-size: 15px;
  padding: 0 8px;
}

.el-main{
	padding-left:5px;
	padding-right:5px;
}

.patient-info-bar {
	display: flex;
	align-items: center;
	padding: 8px 12px;
	background-color: #ecf5ff;
	/* A light blue background */
	border: 1px solid #d9ecff;
	border-radius: 8px;
	margin-bottom: 16px;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.patient-info-bar .el-button.is-text {
	font-size: 20px;
	color: #409eff;
}

.patient-details {
	flex-grow: 1;
	margin: 0 20px;
	font-size: 14px;
	text-align: center;
}

.patient-details .el-space span {
	color: #303133;
}

.patient-details .el-space span strong {
	color: #909399;
	font-weight: normal;
	margin-right: 5px;
}

.placeholder-text {
	flex-grow: 1;
	text-align: center;
	color: #909399;
	font-size: 14px;
	margin: 0 20px;
}

.sidebar-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  padding: 24px 16px 16px 16px;
  margin-right: 16px;
  min-height: 96vh;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.sidebar-section-title {
  font-weight: 700;
  font-size: 15px;
  color: #409EFF !important;
  background: #f4f8ff !important;
  border: none !important;
  margin: 10px 0 4px 0;
  display: flex;
  align-items: center;
  border-radius: 8px !important;
  padding-left: 10px !important;
}
.sidebar-section-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 18px;
  background: #409EFF;
  border-radius: 2px;
  margin-right: 8px;
}
.sidebar-search .el-input__wrapper {
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(64,158,255,0.08);
  border: 1px solid #e0eaff;
  background: #f8fafc;
}
.sidebar-search .el-input__inner {
  font-size: 15px;
}
.sidebar-search .el-button {
  border-radius: 8px;
  background: #409EFF;
  color: #fff;
}
.sidebar-table {
  border-radius: 10px;
  overflow: hidden;
  font-size: 15px;
}
.sidebar-table th {
  background: #f5f7fa !important;
  font-weight: 600;
  color: #666;
  font-size: 14px;
  padding: 6px 0;
}
.sidebar-table td {
  background: #f8fafc;
  border-radius: 8px;
  font-size: 15px;
  color: #222;
  padding: 6px 0;
  transition: background 0.2s;
}
.sidebar-table tr:hover td {
  background: #e6f0ff !important;
}
</style>