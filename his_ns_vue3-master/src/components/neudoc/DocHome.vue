<template>
<el-container>
	<el-aside  v-show="isShowMenu"  width="250px">
		<el-row>
			<el-col :span="12" style="text-align: left">
			  <el-tag size="mini" style="width: 100%;">患者选择：</el-tag>
			</el-col>
			<el-col :span="12" style="text-align: right">
				<el-button type="primary" :icon="Refresh" size="small"  
					style="margin-right: 5px"></el-button>
			</el-col>
		</el-row>
		<!--挂号信息 start -->
		<el-row>
			<el-col :span="24" >
				<el-input v-model="kw" placeholder="请输入内容" class="input-with-select">
					<template #prepend>患者名:</template>
					<template #append><el-button :icon="Search" /></template>
				</el-input>	
			</el-col>
		</el-row>
		<!--挂号信息 end -->
		
		<!-- 未患者列表 start -->
		<el-row>
			<el-col :span="24" style="margin-top: 20px;">
				<el-tag size="mini" style="width: 100%">未诊患者：</el-tag>
				<el-table :data="data.records" style="width:100%;"
					v-loading="loading" @row-click="handleRowClick">
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
				<el-tag size="mini" style="width: 100%">已诊患者：</el-tag>
				<el-table :data="data2.records" style="width:100%;"
					v-loading="loading2">
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
		<el-row>
			<el-button type="primary" plain @click="isShowMenu=!isShowMenu" :span="2" size="small">
			    {{isShowMenu?"隐藏患者栏":"显示患者栏"}}
			</el-button>
			<el-tag :span="20" style="margin-left: 20px; margin-right: 20px;">
				{{patientInfo}}
			</el-tag>
			<el-button type="primary" size="small" v-if="isOver" :span="2">
				诊毕
			</el-button>
			<!-- 新增按钮：生成病历分析模块 -->
			<el-button type="success" size="small" @click="showAnalysisModule" style="margin-left: 10px;">
				病历分析
			</el-button>
		</el-row>
		<el-row>
			<el-tabs v-model="activeName" type="card" class="demo-tabs"
			     @tab-click="handleClick">
			    <el-tab-pane label="病历首页">
					<medicalrecord></medicalrecord>
				</el-tab-pane>
			    <el-tab-pane label="检查申请">
					检查申请
				</el-tab-pane>
			    <el-tab-pane label="检验申请*">
					检验申请
				</el-tab-pane>
				<el-tab-pane label="门诊确诊*">
					检验申请
				</el-tab-pane>
				<el-tab-pane label="处置申请*">
					处置申请
				</el-tab-pane>
				<el-tab-pane label="处方申请">
					处方申请
				</el-tab-pane>
				<el-tab-pane label="费用查询*">
					费用查询
				</el-tab-pane>
			</el-tabs>
		</el-row>
		
		<!-- 新增：病历分析模块 -->
		<el-row v-if="showAnalysis" style="margin-top: 20px;">
			<el-col :span="24">
				<el-card>
					<template #header>
						<div style="display: flex; justify-content: space-between; align-items: center;">
							<span>病历分析结果</span>
							<el-button type="danger" size="small" @click="closeAnalysisModule">关闭</el-button>
						</div>
					</template>
					<medical-analysis 
						:patient-info="currentPatient"
						:analysis-data="analysisData"
						:loading="analysisLoading"
						@refresh="loadAnalysisData">
					</medical-analysis>
				</el-card>
			</el-col>
		</el-row>
		
		<el-row>


		</el-row>
		
	</el-main>
</el-container>
</template>

<script setup>
import medicalrecord from '../neudoc/his/Doc01.vue'
import MedicalAnalysis from '../neudoc/his/MedicalAnalysis.vue'

import { ref,onMounted } from 'vue'
import { fetchData,postReq } from '../../utils/api'
import router from '../../router';
import { formatDate } from '../../utils/utils.js'
import { Search,Refresh } from '@element-plus/icons-vue'	
import { useUserStore } from '../../store/user.js'
import { ElMessage } from 'element-plus'




//获取用户仓库对象
const userStore=useUserStore()
//data start
const loading=ref(false)
const loading2=ref(false)
const data = ref({})
const data2 = ref({})
//搜索框
const kw=ref('')
//是否显示患者列表
const isShowMenu=ref(true)
//看诊人信息
const patientInfo=ref('')
//是否显示诊毕
const isOver=ref(true)

// 新增：病历分析相关状态
const showAnalysis = ref(false)
const analysisLoading = ref(false)
const analysisData = ref({})
const currentPatient = ref({})

onMounted(async () => {
  loadData(1)
  loadData2(1)
});


//加载当天未就诊患者数据
async function loadData(pn){
	loading.value=true
	// var loginUser=JSON.parse(sessionStorage.getItem("user"))
	var date=formatDate(new Date())
	let url=`/register/page?count=100&pn=${pn}&state=1&regDate=${date}`
	url+=`&deptId=${userStore.getUserInfo.value.deptID}`
	url+=`&docId=${userStore.getUserInfo.value.id}`
	if(kw!='')
		url+=`&keyword=${kw.value}`

	const result = await fetchData(url,null);
	if(result.result){
		data.value = result.data
		loading.value=false
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')		
	}	
}


//加载当天已就诊患者数据
async function loadData2(pn){
	loading2.value=true
	var loginUser=JSON.parse(sessionStorage.getItem("user"))
	var date=formatDate(new Date())
	let url=`/register/page?count=100&pn=${pn}&state=2&regDate=${date}`
	url+=`&deptId=${userStore.getUserInfo.value.deptID}`
	url+=`&docId=${userStore.getUserInfo.value.id}`
	if(kw!='')
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

const activeName = ref('first')
//门诊tab 切换 
const handleClick=()=>{
	
}

const handleRowClick=(val, column, event)=>{
	patientInfo.value=`患者姓名：${val.realName}    病历号：${val.caseNumber}    年龄：${val.age}    性别：${val.gender==71?"男":"女"}`
	currentPatient.value = val
	isOver.value=true
}

// 新增：显示病历分析模块
const showAnalysisModule = () => {
	if (!currentPatient.value.caseNumber) {
		ElMessage.warning('请先选择患者')
		return
	}
	showAnalysis.value = true
	loadAnalysisData()
}

// 新增：关闭病历分析模块
const closeAnalysisModule = () => {
	showAnalysis.value = false
	analysisData.value = {}
}

// 新增：加载分析数据
const loadAnalysisData = async () => {
	if (!currentPatient.value.caseNumber) return
	
	analysisLoading.value = true
	try {
		// 通过GET请求获取后端分析数据
		// 注意：这里使用模拟数据，实际项目中应该调用真实的后端API
		const result = await fetchData(`/neudoc/analysis?caseNumber=${currentPatient.value.caseNumber}`, null)
		
		// 如果后端API不可用，使用模拟数据
		if (!result || !result.result) {
			console.log('使用模拟数据')
			// 模拟API响应
			await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟网络延迟
			
			// 根据病历号生成不同的模拟数据
			const mockData = {
				diagnosis: {
					title: "初步诊断建议",
					content: `根据患者${currentPatient.value.realName}的症状和检查结果，建议考虑以下诊断方向：1. 上呼吸道感染 2. 急性支气管炎 3. 需要进一步检查排除其他疾病`
				},
				medication: [
					{
						name: "阿莫西林胶囊",
						dosage: "0.5g",
						frequency: "每日3次",
						duration: "7天",
						note: "饭后服用，注意过敏反应"
					},
					{
						name: "布洛芬缓释胶囊",
						dosage: "0.3g",
						frequency: "每日2次",
						duration: "3天",
						note: "疼痛时服用，避免空腹"
					}
				],
				examination: [
					{
						name: "血常规",
						reason: "了解感染程度和血象变化",
						priority: "高"
					},
					{
						name: "胸部X线片",
						reason: "排除肺部感染",
						priority: "中"
					}
				],
				risk: [
					{
						type: "药物过敏风险",
						level: "低风险",
						description: "患者无已知药物过敏史，但仍需注意观察",
						suggestion: "首次用药时密切观察，如有异常立即停药"
					}
				]
			}
			
			analysisData.value = mockData
		} else {
			analysisData.value = result.data
		}
	} catch (error) {
		console.error('获取分析数据失败:', error)
		ElMessage.error('网络请求失败，使用模拟数据')
		
		// 出错时也使用模拟数据
		const mockData = {
			diagnosis: {
				title: "诊断建议（模拟数据）",
				content: "由于网络问题，显示模拟的诊断建议数据"
			},
			medication: [],
			examination: [],
			risk: []
		}
		analysisData.value = mockData
	} finally {
		analysisLoading.value = false
	}
}
</script>

<style>
</style>