<template>
	<!-- 搜索栏 start -->
	<el-row class="search-bar">
		<div class="search-controls">
			<el-select v-model="dept" filterable
				clearable @clear="loadData(1)"
				placeholder="所属科室"  @change="loadData(1)" >
				<el-option
				  v-for="item in depts"
				  :key="item.id"
				  :label="item.deptCode+item.deptName"
				  :value="item.id"/>
			</el-select>
			<el-date-picker
					v-model="sdate"
					type="daterange"
					range-separator="至"
					start-placeholder="开始时间"
					end-placeholder="结束时间"
					:size="size"
					format="YYYY/MM/DD"
					value-format="YYYY-MM-DD" 
					style="width: 100%;"
					/>
			<el-input v-model.trim="kw"
				placeholder="请输入姓名或排班名"/>
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</div>
		<el-button type="primary" @click="addDialogVisible=true">新增排班计划</el-button>
	</el-row>
	<div style="margin-top: 40px;"></div>
	<!-- 搜索栏 end -->
<!-- 表格栏 start -->
	<el-row class="m-2">
		<el-col :span="24">
			<el-table :data="data.records" style="width:100%;"
			 v-loading="loading">
			    <el-table-column fixed prop="id" label="序号" width="80"  align="center" />
			    <el-table-column prop="schedDate" label="日期"   align="center" />
				<el-table-column prop="deptName" label="所属科室" align="center"/>
				<el-table-column prop="realName" label="姓名" align="center"/>
				<el-table-column prop="noon" label="午别"  align="center" />
				<el-table-column prop="registName" label="挂号级别"  align="center" />
				<el-table-column prop="registQuota" label="挂号限额"  align="center" />
			    <el-table-column fixed="right" label="操作"  align="center" >
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
	
	<!-- 编辑排班对话框 start -->
	<el-dialog v-model="editDialogVisible" title="编辑排班" @close="handleClose">
	    <el-form :model="form" :rules="editRules" ref="editFormRef" label-width="100px">
			<el-form-item label="医生姓名" prop="realName" >
			  <el-input v-model="form.realName" disabled />
			</el-form-item>
			<el-form-item label="排班日期" prop="schedDate" >
			  <el-date-picker
			          v-model="form.schedDate"
			          type="date"
			          placeholder="选择一个日期"
			          format="YYYY-MM-DD"
			          value-format="YYYY-MM-DD"
			        />
			</el-form-item>
			<el-form-item label="午别" prop="noon" >
			  <el-select v-model="form.noon" placeholder="选择午别">
			      <el-option label="上午" value="上午" />
			      <el-option label="下午" value="下午" />
			  </el-select>
			</el-form-item>
			<el-form-item label="挂号限额" prop="registQuota">
				<el-input-number v-model="form.registQuota" :min="0" />
			</el-form-item>
	    </el-form>
	    <template #footer>
	      <span class="dialog-footer">
	        <el-button @click="editDialogVisible = false">取消</el-button>
	        <el-button type="primary" @click="editSave">
	          保存
	        </el-button>
	      </span>
	    </template>
	</el-dialog>
	<!-- 编辑排班对话框 end -->

<!-- 新增常数据项对话框 start -->
	<el-dialog v-model="addDialogVisible" title="新增排班计划" @close="closeDialog" width="60%">
	   <el-form :model="plans" :rules="formRules" label-width="100px">
	   	<el-form-item label="所属科室"  prop="deptId">
	   		<el-select v-model="plans.deptId" filterable 
				clearable placeholder="所属科室"  @change="loadRules"  >
	   		    <el-option
	   		      v-for="item in depts"
	   		     :key="item.id"
	   		     :label="item.deptName"
	   		     :value="item.id"/>
	   		</el-select>		
	   	</el-form-item> 
		<el-form-item label="排班规则"  prop="ruleSelection">
	   	<el-table :data="rules" style="width:100%;"  v-loading="loadingRules"   
			@selection-change="handleSelectionChange">
			<el-table-column type="selection" width="50"  align="left"/>
	   	    <el-table-column prop="ruleName" label="规则名称"  align="center" />
	   		<el-table-column prop="deptName" label="所属科室" align="center"/>
	   		<el-table-column prop="realName" label="姓名" align="center"/>
	   		<el-table-column prop="week" label="规则" width="150"  align="center" />
	   	  </el-table>
		</el-form-item>
		<el-form-item label="排班日期"  prop="ndate">
			<el-date-picker v-model="plans.ndate"
					type="daterange"
					range-separator="至"
					start-placeholder="开始日期"
					end-placeholder="结束日期"
					:size="size"
					format="YYYY/MM/DD"
					value-format="YYYY-MM-DD"/>		
		</el-form-item>
	   </el-form>
		<template #footer>
	      <span class="dialog-footer">
	        <el-button @click="addDialogVisible = false">取消</el-button>
	        <el-button type="primary" :disabled="plans.ruleSelection.length==0" @click="save">
	          生成排班计划
	        </el-button>
	      </span>
	    </template>
	</el-dialog>
	<!-- 新增常数据类别对话框  end-->  
</template>

<script setup>
import { ref,onMounted,watchEffect} from 'vue'
import { fetchData,postReq } from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'	
import {nextDay,isBefore,getDay,formatDate} from '../../utils/utils'
import router from '../../router';

//加载页码
const loading=ref(false)
const loadingRules=ref(false)
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
const currentPage = ref(1)

//新增排班计划 对话框
//新增对话框是否显示
const addDialogVisible = ref(false)

// 编辑排班计划对话框
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const form = ref({
	id: '',
	realName: '',
	schedDate: '',
	noon: '',
	registQuota: 0
})
const editRules = ref({
	registQuota: [
		{ required: true, message: '请输入挂号限额', trigger: 'blur' },
		{ type: 'number', message: '挂号限额必须为数字'}
	],
	schedDate: [
		{ required: true, message: '请选择排班日期', trigger: 'change' }
	],
	noon: [
		{ required: true, message: '请选择午别', trigger: 'change' }
	]
})

const defaultPlans = {
	deptId:"",
	ndate:[],
	ruleSelection:[]
}
//新增排班计划
const plans=ref(JSON.parse(JSON.stringify(defaultPlans)))

const formRules=ref({
	deptId: [{ required: true, message: '请选择科室', trigger: 'change' }],
	ndate: [{ required: true, message: '请选择排班日期范围', trigger: 'change' }],
	ruleSelection: [{ required: true, type: 'array', message: '请至少选择一个排班规则', trigger: 'change' }]
})

function loadData(pageNo = 1) {
	loading.value = true
	currentPage.value = pageNo

	const params = new URLSearchParams()
	params.append('pn', pageNo)
	params.append('count', ps.value)

	if (kw.value) {
		params.append('keyword', kw.value)
	}
	if (dept.value) {
		params.append('deptId', dept.value)
	}

	if (sdate.value && sdate.value.length === 2) {
		params.append('start', sdate.value[0])
		params.append('end', sdate.value[1])
	}
	
	fetchData(`/scheduling/page?${params.toString()}`)
		.then(res => {
			if (res.result) {
				data.value = res.data
			} else {
				if (res.errMsg === '未登录') {
					router.push('/login')
				} else {
					ElMessage.error(res.errMsg || '数据加载失败')
				}
			}
		})
		.catch(err => {
			console.error(err)
			ElMessage.error('请求异常，请稍后重试')
		})
		.finally(() => {
			loading.value = false
		})
}

function save(){
	const schedulings = []
	const [startDate, endDate] = plans.value.ndate
	if (!startDate || !endDate) {
		ElMessage.error('请选择有效的日期范围');
		return;
	}

	let currentDate = new Date(startDate)
	const finalDate = new Date(endDate)

	plans.value.ruleSelection.forEach(rule => {
		let iterDate = new Date(currentDate)
		while(isBefore(iterDate, finalDate)){
			const day = getDay(iterDate) // 假设 getDay 返回 1-7 (周一到周日)
			const weekPattern = rule.week;

			// 上午
			if(weekPattern[(day-1)*2] === '1'){
				schedulings.push({
					schedDate: formatDate(iterDate),
					deptID: plans.value.deptId,
					userID: rule.userID,
					noon: "上午",
					ruleID: rule.id
				})
			}
			// 下午
			if(weekPattern[(day-1)*2+1] === '1'){
				schedulings.push({
					schedDate: formatDate(iterDate),
					deptID: plans.value.deptId,
					userID: rule.userID,
					noon: "下午",
					ruleID: rule.id
				})
			}
			iterDate = nextDay(iterDate)
		}
	})

	if (schedulings.length > 0) {
		postReq("/scheduling/add", schedulings).then(resp => {
			if(resp.data.result){
				addDialogVisible.value = false
				loadData(1)
				ElMessage.success('排班计划生成成功')
			} else {
				ElMessage.error(resp.data.errMsg || '排班计划生成失败')
			}
		}).catch(error => {
			ElMessage.error('请求失败')
		})
	} else {
		ElMessage.warning('根据您的规则和日期范围，没有生成任何排班条目。')
	}
}

//组件挂载后事件
onMounted(async() => {
  loadData(1)
  loadDeptData()
});

//加载科室数据
async function loadDeptData(){
	let url=`/department/page?count=200&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		depts.value=result.data.records 
	}else{
		if(result.errMsg=='未登录'){
			router.push('/login')
		}
	}	
}

//加载排班规则
async function loadRules(){
	if (!plans.value.deptId) {
		rules.value = [];
		return;
	}
	loadingRules.value = true;
	let url=`/rule/page?count=200&pn=1&deptid=${plans.value.deptId}`
	const result = await fetchData(url,null);
	if(result.result){
		rules.value=result.data.records 
	}else{
		if(result.errMsg=='未登录'){
			router.push('/login')
		}
	}
	loadingRules.value = false;
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

function closeDialog(){
	plans.value = JSON.parse(JSON.stringify(defaultPlans));
	rules.value = [];
}

// 显示编辑框
function showEdit(row) {
	form.value = { 
		id: row.id,
		realName: row.realName,
		schedDate: row.schedDate,
		noon: row.noon,
		registQuota: row.registQuota
	};
	editDialogVisible.value = true;
}

// 关闭编辑/新增对话框
function handleClose() {
	if(editFormRef.value){
		editFormRef.value.resetFields()
	}
	form.value = { id: '', realName: '', schedDate: '', noon: '', registQuota: 0 }
}

// 保存编辑
async function editSave() {
	if (!editFormRef.value) return;
	await editFormRef.value.validate((valid) => {
		if (valid) {
			postReq("/scheduling/update", form.value).then(resp => {
				if(resp.data.result){
					editDialogVisible.value = false
					loadData(data.value.current)
					ElMessage.success('修改成功')
				} else {
					if(resp.data.errMsg=='未登录') router.push('/login')
					ElMessage.error(resp.data.errMsg || '修改失败')
				}
			})
		}
	})
}

//删除
function del(id){
	ElMessageBox.confirm(
	    '确认是否删除?',
	    '警告',
	    {
	      confirmButtonText: '确认',
	      cancelButtonText: '取消',
	      type: 'warning',
	    }
	)
	.then(() => {
		var formData = new FormData();
		formData.append("id", id);
		postReq("/scheduling/del",formData).then(resp=>{
			if(resp.data.result){
				loadData(data.value.current)
				ElMessage.success('删除成功')
			}else{
				if(resp.data.errMsg=='未登录') router.push('/login')
				ElMessage.error(resp.data.errMsg || '删除失败')
			}
		})
	})
}

</script>

<style scoped>
.search-bar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20px;
}

.search-controls {
	display: flex;
	align-items: center;
	gap: 15px;
}
</style>