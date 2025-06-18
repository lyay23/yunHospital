<template>
	<!-- 搜索栏 start -->
	<el-row>
		<el-col :span="3"  style="padding-right: 20px;">
			<el-select v-model="dept" class="m-2" filterable
				clearable @clear="loadData(1)"
				placeholder="所属科室"  @change="loadData(1)" >
				<el-option
				  v-for="item in depts"
				  :key="item.id"
				  :label="item.deptCode+item.deptName"
				  :value="item.id"/>
			</el-select>
		</el-col>
		<el-col :span="8"  style="padding-right: 20px;">
			<el-date-picker
					v-model="sdate"
					type="daterange"
					range-separator="To"
					start-placeholder="Start date"
					end-placeholder="End date"
					:size="size"
					format="YYYY/MM/DD"
					value-format="YYYY-MM-DD"/>
		</el-col>
		<el-col :span="4" style="padding-right: 20px;">
			<el-input v-model.trim="kw"
				placeholder="请输入姓名或排班名" class="w-100 m-2"/>
		</el-col>
		<el-col :span="4">
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</el-col>
		<el-col :span="2">
		</el-col>
		<el-col :span="3">
			<el-button type="danger" @click="addDialogVisible=true">新增排班计划</el-button>
		</el-col>
	</el-row>
	<div style="margin-top: 40px;"></div>
	<!-- 搜索栏 end -->
<!-- 表格栏 start -->
	<el-row class="m-2">
		<el-col :span="24">
			<el-table :data="data.records" style="width:100%;"
			 v-loading="loading">
			    <el-table-column fixed prop="id" label="序号" width="80"  align="center" />
			    <el-table-column prop="schedDate" label="日期"  align="center" />
				<el-table-column prop="deptName" label="所属科室" align="center"/>
				<el-table-column prop="realName" label="姓名" align="center"/>
				<el-table-column prop="noon" label="午别"  align="center" />
				<el-table-column prop="registName" label="挂号级别"  align="center" />
				<el-table-column prop="registQuota" label="挂号限额"  align="center" />
			    <el-table-column fixed="right" label="Operations"  align="center" >
			      <template #default="scope">
			        <el-button link type="primary" @click="showEdit(scope.row)">编辑</el-button>
					<el-button link type="danger" @click="del(scope.row.id)">删除</el-button>
			      </template>
			    </el-table-column>
			  </el-table>
		</el-col>
	</el-row>
	<!-- 表格栏 end -->
	<div style="margin-top: 20px;"></div>
	<!-- page start -->
	<el-row>
		<el-col :span="20">
			<el-pagination background layout="prev, pager, next" 
			:total="data.total" :page-count="data.pages"
			@current-change="handleCurrentChange"/>
		</el-col>
		<el-col :span="4">
			<el-select v-model="ps" class="m-2" placeholder="每页行数" @change="loadData(1)" >
			    <el-option
			      v-for="item in pageSizes"
			      :key="item"
			      :label="item"
			      :value="item"/>
			</el-select>
		</el-col>
	</el-row>
	<!-- page end -->
	
	

<!-- 新增常数据项对话框 start -->
	<el-dialog v-model="addDialogVisible" title="新增" @close="closeDialog">
	   <el-form :model="plans" :rules="rules" label-width="100">
	   	<el-form-item label="所属科室"  prop="deptID">
	   		<el-select v-model="plans.deptId" class="m-2" filterable 
				clearable placeholder="所属科室"  @change="loadRules(1)"  >
	   		    <el-option
	   		      v-for="item in depts"
	   		     :key="item.id"
	   		     :label="item.deptCode+item.deptName"
	   		     :value="item.id"/>
	   		</el-select>		
	   	</el-form-item> 
		<el-form-item label="排班规则"  prop="">
	   	<el-table :data="rules" style="width:100%;"  v-loading="loading"   
			@selection-change="handleSelectionChange">
			<el-table-column type="selection" width="50"  align="left"/>
	   	    <el-table-column prop="ruleName" label="规则名称"  align="center" />
	   		<el-table-column prop="deptName" label="所属科室" align="center"/>
	   		<el-table-column prop="realName" label="姓名" align="center"/>
	   		<el-table-column prop="week" label="规则" width="150"  align="center" />
	   	  </el-table>
		</el-form-item>
		<el-form-item label="日期"  prop="newDate">
			<el-date-picker v-model="plans.ndate"
					type="daterange"
					range-separator="To"
					start-placeholder="Start date"
					end-placeholder="End date"
					:size="size"
					format="YYYY/MM/DD"
					value-format="YYYY-MM-DD"/>		
		</el-form-item>
	   </el-form>
		<template #footer>
	      <span class="dialog-footer">
	        <el-button @click="addDialogVisible = false">取消</el-button>
	        <el-button type="primary" :disabled="plans.ruleSelection.length==0" @click="save">
	          新增排班计划
	        </el-button>
	      </span>
	    </template>
	</el-dialog>
	<!-- 新增常数据类别对话框  end-->  
</template>

<script setup>
import { ref,onMounted,watchEffect} from 'vue'
import { fetchData,postReq } from '../../utils/api'
import { ElMessageBox } from 'element-plus'	
import {nextDay,isBefore,getDay,formatDate} from '../../utils/utils'

//加载页码
const loading=ref(false)
//分页信息
const data = ref({})
//搜索栏 下拉框数据集
const depts=ref([])
const rules=ref([])
//搜索栏 文本框
const kw=ref('')
//搜索栏 下拉框选中值
const dept=ref('')
const sdate=ref(["",""])

//每页行数
const ps=ref(10)
//行数集
const pageSizes=[10,20,30,50]

//新增排班计划 对话框
//新增对话框是否显示
const addDialogVisible = ref(false)
//新增排班计划
const plans=ref({
	deptId:"",
	start:"",
	end:"",
	ndate:["",""],
	ruleSelection:[]
})
const schedulings=ref([])

function save(){
	var next=new Date(plans.value.ndate[0])
	var end=new Date(plans.value.ndate[1])
	
	plans.value.ruleSelection.forEach((item,index)=>{
		var next=new Date(plans.value.ndate[0])
		var end=new Date(plans.value.ndate[1])
		while(isBefore(next,end)){
			var day=getDay(next)
			if(item.week[(day-1)*2]==1){
				schedulings.value.push({
					id:0,
					schedDate:formatDate(next),
					deptID:plans.value.deptId,
					ruleID:item.id,
					userID:item.userID,
					noon:"上午"
				})
			}
			if(item.week[(day-1)*2+1]==1){
				schedulings.value.push({
					id:0,
					schedDate:formatDate(next),
					deptID:plans.value.deptId,
					ruleID:item.id,
					userID:item.userID,
					noon:"下午"
				})
			}
			next=new Date(nextDay(next))
		}	
	})
	postReq("/scheduling/add",schedulings.value).then(resp=>{
		if(resp.data.result){
			addDialogVisible.value=false
			loadData(data.value.current)
			ElMessageBox.alert('添加成功', '提示',{})
			
		}else{
			if(resp.result.errMsg=='未登录')
				router.push('/login')
				
			ElMessageBox.alert(resp.data.errMsg, '提示',{})
		}
		
	})
}

//组件挂载后事件
onMounted(async() => {
  loadData(1)
  loadDeptData()
});

//加载科室数据
async function loadDeptData(){
	let url=`/department/page?&count=200&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		depts.value=result.data.records 
	}else{
		if(result.errMsg=='未登录'){
			router.push('/login')
		}
	}	
}

//加载数据
async function loadData(pn){
	loading.value=true
	let url=`/scheduling/page?count=${ps.value}&pn=${pn}`
	if(kw!='')
		url+=`&keyword=${kw.value}`

	if(dept!='')
		url+=`&deptId=${dept.value}`

	if(sdate.value[0]!=''&&sdate.value[1]!='')
		url+=`&start=${sdate.value[0]}&end=${sdate.value[1]}`
	
	const result = await fetchData(url,null);
	if(result.result){
		data.value = result.data
		loading.value=false
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')
	}
}

//加载排班数据
async function loadRules(){
	loading.value=true
	let url=`/rule/page?count=1000&pn=1`
	if(dept!='')
		url+=`&deptId=${plans.value.deptId}`
	
	const result = await fetchData(url,null);
	if(result.result){
		rules.value=result.data.records 
		loading.value=false
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')
	}
}

//分页
function handleCurrentChange (number)  {
	loadData(number)
}

//新增排班计划 选中
function handleSelectionChange(val){
	plans.value.ruleSelection=val
	//console.log(plans.value.ruleSelection)
}


</script>

<style>
	
</style>