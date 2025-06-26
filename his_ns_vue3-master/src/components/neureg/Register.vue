<template>
	<!-- 搜索栏 start -->
	<el-row>
	    <el-col :span="6" style="padding-right: 20px;">
			<el-input v-model.trim="kw"
				placeholder="请输入姓名或身份证号码" class="w-100 m-2"/>
		</el-col>
		<el-col :span="2" class="m-2">
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</el-col>
		<el-col :span="4"></el-col>

		<el-col :span="6"  style="padding-right: 20px;">
			<el-select v-model="dept" class="m-2" filterable clearable placeholder="科室" @change="loadUserData(1)" >
				<el-option v-for="item in depts"
				  :key="item.id"
				  :label="item.deptCode+item.deptName"
				  :value="item.id"/>
			</el-select>
		</el-col>
		<el-col :span="6">
			<el-select v-model="user" class="m-2" filterable clearable
				placeholder="医生"  @change="loadSchedulingData(1)" >
				<el-option v-for="item in users"
				  :key="item.id"
				  :label="item.realName"
				  :value="item.id"/>
			</el-select>
		</el-col>
	</el-row>
	<div style="margin-top: 10px;"></div>
	<el-row>
		<el-col :span="4"></el-col>
		<el-col :span="4"></el-col>
		<el-col :span="4"></el-col>
		<el-col :span="6">
			<el-select v-model="regLevel" class="m-2" filterable clearable
				placeholder="挂号级别"  @change="loadSchedulingData(1)" >
				<el-option v-for="item in regLevels.records"
				  :key="item.id"
				  :label="item.registCode+item.registName"
				  :value="item.id"/>
			</el-select>
		</el-col>
		<el-col :span="4"></el-col>
		<el-col :span="2">
			<el-button type="danger" @click="save">现场挂号</el-button>
		</el-col>
	</el-row>
	<div style="margin-top: 40px;"></div>
	<!-- 搜索栏 end -->
	
	<!-- 表格栏 start -->
	<el-row class="m-2">
		<el-col :span="24">
			<el-table ref="multipleTableRef" :data="data.records" style="width:100%;"
			 @select="handleSelectionReg"
			 @selection-change="handleSelectionChangeReg"
			 v-loading="loading">
				<el-table-column type="selection" width="30"  align="left"/>
				<el-table-column type="expand">
						<template #default="scope">
				        <div m="4" style="padding-left: 50px;">
				          <p m="t-0 b-2">关联用户: {{ scope.row.customerName }}</p>
				          <p m="t-0 b-2">关&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系: {{ scope.row.relationshipName }}</p>
				          <p m="t-0 b-2">家庭地址: {{ scope.row.addr }}</p>
				          <p m="t-0 b-2">注册时间: {{ scope.row.createdate }}</p>
						  <p m="t-0 b-2">注册渠道: {{ scope.row.channelName }}</p>
				        </div>
				      </template>
				</el-table-column>
			    <el-table-column prop="id" label="序号" width="55" align="center" />
			    <el-table-column prop="realname" label="姓名"  width="65" align="center"  />
			    <el-table-column prop="gender" label="性别" width="55"  align="center">
					<template #default="scope">
					    {{scope.row.gender==71?"男":"女"}}
					</template>
				</el-table-column>
			    <el-table-column prop="idnumber" label="身份证"  align="center" />
				<el-table-column prop="birthdate" label="出生日期"  align="center" />
			    <el-table-column prop="phone" label="手机号"  align="center" />
				<el-table-column prop="cardtypeName" label="卡类型"  align="center" />
			  </el-table>
		</el-col>
	</el-row>
	<div style="margin-top: 40px;"></div>
	<el-row class="m-2">
		<el-col :span="24">
			<el-table ref="multipleTableRef2" :data="plans.records" style="width:100%;"
				v-loading="loading2"
				@select="handleSelectionDoc"
				@selection-change="handleSelectionChangeDoc" >
				<el-table-column type="selection" width="55"  align="left"/>
			    <el-table-column prop="realName" label="姓名"  align="center"  />
			    <el-table-column prop="schedDate" label="日期" align="center"/>
			    <el-table-column prop="noon" label="午别"  align="center" />
				<el-table-column prop="registName" label="号别"  align="center"/>
				<el-table-column prop="registFee" label="挂号费" align="center" />
				<el-table-column prop="regNum" label="余号"  align="center">
					<template #default="scope">
					    {{scope.row.registQuota-scope.row.regNum}}
					</template>
				</el-table-column>
			  </el-table>
		</el-col>
	</el-row>
	<!-- 表格栏 end -->
	<div style="margin-top: 20px;"></div>
	


	<!--常数据项  start  --> 
	<!--常数据项  end  --> 
</template>

<script setup>
import { ref,onMounted } from 'vue'
import { fetchData,postReq } from '../../utils/api'
import { ElMessageBox,ElTable, ElMessage } from 'element-plus'
import { formatDate,getAge,getTimeRange } from '../../utils/utils.js'
import router from '../../router';
//导入用户仓库
import { useUserStore } from '../../store/user.js'
//获取用户仓库对象
const userStore=useUserStore()
//data start
const loading=ref(false)
const loading2=ref(false)
//就诊卡集合
const data = ref({})
const multipleTableRef = ref()
//排班计划
const plans=ref({})
const multipleTableRef2 = ref()
//挂号级别集合
const regLevels=ref({})
//科室集合
const depts=ref({})
//医生集合
const users=ref({})
//搜索框
//用户搜索框值
const kw=ref('')
//选中科室
const dept=ref('')
//选中医生
const user=ref('')
//选中挂号级别
const regLevel=ref('')
//每页行数
const ps=ref(10)
const pageSizes=[10,20,30,50]

//挂号人
const reg=([])
//被医生
const doc=([])

//编辑框是否显示
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)


//data end


onMounted(async () => {
  loadDeptData(1)
  loadRegLevelData();
});

//加载数据
async function loadData(pn){
	loading.value=true
	let url=`/medicalcard/page?count=${ps.value}&pn=${pn}`
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


//加载挂号级别数据
async function loadRegLevelData(){
	let url=`/registlevel/page?count=100&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		regLevels.value = result.data
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')	
	}	
}

//加载科室数据
async function loadDeptData(){
	let url=`/department/page?&count=200&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		depts.value=result.data.records 
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')	
	}	
}

//加载科室医生数据
async function loadUserData(){
	let url=`/user/page?&dept=${dept.value}&userType=172&count=200&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		users.value=result.data.records 
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')
	}	
}

async function loadSchedulingData(pn){
	loading2.value=true
	var date=new Date()
	var sdate=formatDate(date)

	let url=`/scheduling/page?count=100&pn=${pn}&start=${sdate}&end=${sdate}`
	if(user!='')
		url+=`&userId=${user.value}`
	if(regLevel!='')
		url+=`&regLevel=${regLevel.value}`
	if(date.getHours()>12){
		url+=`&noon=下午`
	}	
	const result = await fetchData(url,null);
	if(result.result){
		plans.value = result.data
		loading2.value=false
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')
	}	
}


//保存编辑内容
function save(){
	// console.log(reg.value[0].realname)
	if( reg.value==null || reg.value.length==0){
		ElMessageBox.alert('请选择挂号就诊卡', '提示',{})
		return;
	}
	if(doc.value==null || doc.value.length==0){
		ElMessageBox.alert('请选择挂号信息', '提示',{})
		return;
	}
	console.log(reg.value)
	// var login=JSON.parse(sessionStorage.getItem("user"))
	var param={
		"register":{
				"medicalCardId":reg.value[0].id,
				"RealName":reg.value[0].realname,
				"age":getAge(reg.value[0].birthdate),
				"ageType":"岁",
				"visitDate":doc.value[0].schedDate,
				"noon":doc.value[0].noon,
				"deptID":doc.value[0].deptID,
				"userID":doc.value[0].userID,
				"registLeID":doc.value[0].registlevelId,
				"settleID":reg.value[0].cardtype,
				"isBook":0,
				"registerID":userStore.getUserInfo.value.id,
				"timeInterval":getTimeRange(),
				"channel":186
			},
		"scheduling":doc.value[0]
	}

	postReq("/register/add",param).then(resp => {
		loading.value = false;
		if (resp.data.result) {
			ElMessage.success('挂号成功')
			// 清空选择，为下次挂号做准备
			multipleTableRef.value.clearSelection()
			multipleTableRef2.value.clearSelection()
			// 重新加载排班数据，刷新余号
			loadSchedulingData(1)
		} else {
			ElMessage.error(resp.data.errMsg)
		}
	})
}


//新增排班计划 选中
function handleSelectionChangeReg(val){
	reg.value=[]
	reg.value=val
	console.log(multipleTableRef.value)
}
function handleSelectionReg(selection,row){
	multipleTableRef.value.clearSelection()
	if(selection.length==0) return
	multipleTableRef.value.toggleRowSelection(row,true);
}

function handleSelectionChangeDoc(val){
	doc.value=val
}

function handleSelectionDoc(selection,row){
	multipleTableRef2.value.clearSelection()
	if(selection.length==0) return
	multipleTableRef2.value.toggleRowSelection(row,true);
}
</script>

<style>
</style>