<template>
<el-container>
	<el-aside  v-show="isShowMenu"  width="250px">
		<el-row>
			<el-col :span="12" style="text-align: left">
			  <el-tag size="mini" style="width: 100%;">患者选择：</el-tag>
			</el-col>
			<el-col :span="12" style="text-align: right">
				<el-button type="primary" :icon="Refresh" size="small"  @click="refreshPatientLists"
					style="margin-right: 5px"></el-button>
			</el-col>
		</el-row>
		<!--挂号信息 start -->
		<el-row>
			<el-col :span="24" >
				<el-input v-model="kw" placeholder="请输入内容" class="input-with-select">
					<template #prepend>患者名:</template>
					<template #append><el-button :icon="Search" @click="searchPatients"/></template>
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
					v-loading="loading2" @row-click="handleDiagnosedRowClick">
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
			<el-button type="primary" size="small" v-if="isOver" :span="2" @click="finishConsultation">
				诊毕
			</el-button>
		</el-row>
		<el-row>
			<el-tabs v-model="activeName" type="card" class="demo-tabs"
			     @tab-click="handleClick">
			    <el-tab-pane label="病历首页">
					<medicalrecord ref="medicalRecordComp" :is-diagnosed="isDiagnosed" :patient="currentPatient"></medicalrecord>
				</el-tab-pane>
			    <el-tab-pane label="检查申请">
					<checkapply ref="checkApplyComp" :patient="currentPatient"></checkapply>
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
		
		
		
		
		<el-row>


		</el-row>
		
	</el-main>
</el-container>
</template>

<script setup>
import medicalrecord from '../neudoc/his/Doc01.vue'
import checkapply from '../neudoc/his/Doc02.vue'

import { ref,onMounted } from 'vue'
import { fetchData,postReq } from '../../utils/api'
import router from '../../router';
import { formatDate } from '../../utils/utils.js'
import { Search,Refresh } from '@element-plus/icons-vue'	
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
//搜索框
const kw=ref('')
//是否显示患者列表
const isShowMenu=ref(true)
const currentPatient = ref(null)
const isDiagnosed = ref(false);
//看诊人信息
const patientInfo=ref('请先选择一位患者')
//是否显示诊毕
const isOver=ref(true)

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
	if(kw.value)
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
	// var loginUser=JSON.parse(sessionStorage.getItem("user"))
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

const activeName = ref('first')
//门诊tab 切换 
const handleClick=()=>{
	
}

const handleRowClick=(val, column, event)=>{
	patientInfo.value=`患者姓名：${val.realName}    病历号：${val.caseNumber}    年龄：${val.age}    性别：${val.gender==71?"男":"女"}`
	isOver.value=true
	currentPatient.value = val;
	isDiagnosed.value = false;	
}

const handleDiagnosedRowClick=(val, column, event)=>{
	patientInfo.value=`患者姓名：${val.realName}    病历号：${val.caseNumber}    年龄：${val.age}    性别：${val.gender==71?"男":"女"}`
	isOver.value=false
	currentPatient.value = val;
	isDiagnosed.value = true;
}

function searchPatients() {
    loadData(1);
    loadData2(1);
}

function refreshPatientLists() {
    kw.value = '';
    loadData(1);
    loadData2(1);
}

async function finishConsultation() {
    if (!currentPatient.value) {
        ElMessage.warning('请先选择一位患者');
        return;
    }

    ElMessageBox.confirm(
        '确认要结束本次诊断吗?',
        '提醒',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
    .then(async () => {
        const resp = await postReq("/neudoc/finish", currentPatient.value);
        if(resp.data.result){
            ElMessage.success('操作成功');
            loadData(1); 
            loadData2(1);
            patientInfo.value = '请先选择一位患者';
            isOver.value = false;
            currentPatient.value = null;
            if (medicalRecordComp.value) {
                medicalRecordComp.value.clearForm();
            }
            if (checkApplyComp.value) {
                checkApplyComp.value.clearList();
            }
        } else {
            ElMessage.error(resp.data.errMsg || '操作失败');
        }
    })
    .catch(() => {
        // catch cancel action
    });
}
</script>

<style scoped>
/* Reset browser defaults */
html,
body {
	margin: 0;
	padding: 0;
	height: 100%;
	width: 100%;
	overflow: hidden;
	/* Prevent body scrolling */
}

/* Use a more predictable box-sizing model */
* {
	box-sizing: border-box;
}

.home_container {
	height: 100vh;
	width: 100%;
}

/* Override Element Plus container behavior for our layout */
.main-container,
.content-container {
	display: block;
	height: 100%;
	width: 100%;
}

.top-header {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	height: 60px;
	padding: 0 20px;
	background-color: #20a0ff;
	color: #fff;
	display: flex;
	align-items: center;
	justify-content: space-between;
	z-index: 100;
}

.sidebar {
	position: fixed;
	top: 60px;
	left: 0;
	bottom: 0;
	width: 200px;
	background-color: #fff;
	border-right: 1px solid #e6e6e6;
	overflow-y: hidden;
	/* This will hide the scrollbar */
	z-index: 99;
}

.el-menu-vertical {
	height: 100%;
	/* Make menu fill the sidebar */
	border-right: none;
}

.main-content {
	margin-top: 60px;
	margin-left: 200px;
	padding: 20px;
	height: calc(100vh - 60px);
	/* Full height minus header */
	overflow-y: auto;
	/* Allow main content to scroll */
	width: calc(100% - 200px);
}

.home_title {
	color: #fff;
	font-size: 22px;
	font-weight: bold;
}
</style>