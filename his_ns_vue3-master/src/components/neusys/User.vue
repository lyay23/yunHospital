<template>
	<!-- 搜索栏 start -->
	<el-row>
		<el-col :span="3" style="padding-right: 20px;">
			<el-select v-model="userType" class="m-2" filterable 
				clearable @clear="loadData(1)"
				placeholder="用户类型"  @change="loadData(1)" >
				<el-option v-for="item in userTypes"
					:key="item.id"
					:label="item.constantCode+item.constantName"
					:value="item.id"/>
			</el-select>
		</el-col>
		
		<el-col :span="3" style="padding-right: 20px;">
			<el-select v-model="dept" class="m-2" filterable
				clearable @clear="loadData(1)"
				placeholder="所在科室"  @change="loadData(1)" >
			    <el-option
			      v-for="item in depts"
			      :key="item.id"
			      :label="item.deptCode+item.deptName"
			      :value="item.id"/>
			</el-select>
		</el-col>
		<el-col :span="3" style="padding-right: 20px;">
			<el-select v-model="docType" class="m-2" filterable
				clearable @clear="loadData(1)"
				placeholder="医生职称"  @change="loadData(1)" >
			    <el-option
			      v-for="item in docTypes"
			      :key="item.id"
			      :label="item.constantCode+item.constantName"
			      :value="item.id"/>
			</el-select>
			
		</el-col>
		<el-col :span="5" style="padding-right: 20px;">
			<el-input v-model.trim="kw" 
				  placeholder="请输入用户名或姓名" class="w-100 m-2"/>
		</el-col>
		<el-col :span="3">
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</el-col>
		<el-col :span="4"></el-col>
		<el-col :span="3">
			<el-button type="danger" @click="addDialogVisible=true">新增用户</el-button>
		</el-col>
	</el-row>
	
	<div style="margin-top: 40px;"></div>
	<!-- 搜索栏 end -->
	<!-- 表格栏 start -->
	<el-row class="m-2">
		<el-col :span="24">
			<el-table :data="data.records" style="width:100%;"
			 v-loading="loading">
			    <el-table-column fixed prop="id" label="序号" width="80" align="center" />
			    <el-table-column prop="userName" label="用户名"  align="center" />
				<el-table-column prop="realName" label="姓名" align="center"/>
				<el-table-column prop="useTypeName" label="用户类型"  align="center" />
			    <el-table-column prop="docTitle" label="医生职称"  align="center" />
				<el-table-column prop="dept" label="所在科室"  align="center" />
				<el-table-column prop="isScheduling" label="排班"  align="center" />
				<el-table-column prop="registLe" label="挂号级别"  align="center" />
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
	
	
	
	<!-- 编辑数据项对话框 start -->
	<el-dialog v-model="editDialogVisible" title="编辑" @close="closeDialog">
	    <el-form :model="editform" :rules="rules" label-width="100px">
			<el-form-item label="编号"  prop="id" >
			  <el-input v-model="editform.id" readonly autocomplete="off" />
			</el-form-item>
			<el-form-item label="用户类型"  prop="useType">
				<el-select v-model="editform.useType" class="m-2" placeholder="用户类型"   >
					<el-option v-for="item in userTypes"
						:key="item.id"
						:label="item.constantCode+item.constantName"
						:value="item.id"/>
				</el-select>
			</el-form-item>	
			<el-form-item label="所在科室"  prop="deptID">
				<el-select v-model="editform.deptID"  filterable class="m-2" placeholder="用户类型"   >
					<el-option v-for="item in depts"
						:key="item.id"
						:label="item.deptCode+item.deptName"
						:value="item.id"/>
				</el-select>
			</el-form-item>
			<el-form-item label="医生职称"  prop="docTitleID">
				<el-select v-model="editform.docTitleID" class="m-2" filterable placeholder="用户类型"   >
					<el-option v-for="item in docTypes"
						:key="item.id"
						:label="item.constantCode+item.constantName"
						:value="item.id"/>
				</el-select>
			</el-form-item>
			<el-form-item label="挂号级别"  prop="registLeID">
				<el-select v-model="editform.registLeID" class="m-2" filterable placeholder="挂号级别"   >
					<el-option v-for="item in registLes"
						:key="item.id"
						:label="item.registCode+item.registName"
						:value="item.id"/>
				</el-select>
			</el-form-item>
			<el-form-item label="用户名" prop="userName" >
			  <el-input v-model="editform.userName"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="姓名"  prop="realName" >
			  <el-input v-model="editform.realName"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="是否排班"  prop="isScheduling" >
				 <el-radio-group v-model="editform.isScheduling">
				    <el-radio label="是">是</el-radio>
				    <el-radio label="否">否</el-radio>
				  </el-radio-group>
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
	  <!-- 编辑数据项对话框  end-->
	  
	<!-- 新增常数据项对话框 start -->
	<el-dialog v-model="addDialogVisible" title="新增" @close="closeDialog">
	    <el-form :model="editform" :rules="rules" label-width="100px">
	    	<el-form-item label="编号"  prop="id" >
	    	  <el-input v-model="editform.id" readonly autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="用户类型"  prop="useType">
	    		<el-select v-model="editform.useType" class="m-2" filterable placeholder="用户类型"   >
	    			<el-option v-for="item in userTypes"
	    				:key="item.id"
	    				:label="item.constantCode+item.constantName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>	
	    	<el-form-item label="所在科室"  prop="deptID">
	    		<el-select v-model="editform.deptID" filterable class="m-2" placeholder="所在科室"   >
	    			<el-option v-for="item in depts"
	    				:key="item.id"
	    				:label="item.deptCode+item.deptName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>
	    	<el-form-item label="医生职称"  prop="docTitleID">
	    		<el-select v-model="editform.docTitleID" filterable class="m-2" placeholder="用户类型"   >
	    			<el-option v-for="item in docTypes"
	    				:key="item.id"
	    				:label="item.constantCode+item.constantName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>
	    	<el-form-item label="挂号级别"  prop="registLeID">
	    		<el-select v-model="editform.registLeID" filterable class="m-2" placeholder="挂号级别"   >
	    			<el-option v-for="item in registLes"
	    				:key="item.id"
	    				:label="item.registCode+item.registName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>
	    	<el-form-item label="用户名" prop="userName" >
	    	  <el-input v-model="editform.userName"  autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="姓名"  prop="realName" >
	    	  <el-input v-model="editform.realName"  autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="是否排班"  prop="isScheduling" >
	    		 <el-radio-group v-model="editform.isScheduling">
	    		    <el-radio label="是">是</el-radio>
	    		    <el-radio label="否">否</el-radio>
	    		  </el-radio-group>
	    	</el-form-item>
	    </el-form>
	    <template #footer>
	      <span class="dialog-footer">
	        <el-button @click="addDialogVisible = false">取消</el-button>
	        <el-button type="primary" @click="save">
	          新增
	        </el-button>
	      </span>
	    </template>
	</el-dialog>
	<!-- 新增常数据类别对话框  end-->  
</template>

<script setup>
import { ref,onMounted } from 'vue'
import { fetchData,postReq } from '../../utils/api'
import { ElMessageBox } from 'element-plus'

//加载页码
const loading=ref(false)
//分页信息
const data = ref({})

//搜索栏 下拉框数据集
//科室分类集合
const userTypes=ref([])
//职称集合
const docTypes=ref([])
//科室集合
const depts=ref([])
//挂号级别集合
const registLes=ref([])
//搜索栏 文本框
const kw=ref('')
//搜索栏 下拉框选中值
const userType=ref('')
const docType=ref('')
const dept=ref('')
//每页行数
const ps=ref(10)
//行数集
const pageSizes=[10,20,30,50]


//新增对话框是否显示
const addDialogVisible = ref(false)
//编辑对话框是否显示
const editDialogVisible = ref(false)
//表单对象
const editform=ref({
	"id": '',
	"userName": "",
	"realName": "",
	"useType": '',
	"useTypeName": "",
	"docTitleID": '',
	"docTitle": "",
	"isScheduling": "",
	"deptID": '',
	"dept": "",
	"registLeID": '',
	"registLe": ""
})
//表单验证
const rules=ref({
	userName: [
		{ required: true, message: '请输入用户名', trigger: 'blur' }
	],
	realName: [
		{ required: true, message: '请输入姓名', trigger: 'blur' }
	]
})

//组件挂载后事件
onMounted(async() => {
  loadData(1)
  //科室
  loadDeptData()
  //医生职称
  loadTypeData(8,100,docTypes)
  //用户类型
  loadTypeData(13,100,userTypes)
  //挂号级别
  loadRegLevelData()
});


async function loadTypeData(cid,count,ay){
	let url=`/constantitem/page?ctype=${cid}&count=${count}&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		ay.value=result.data.records 
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')
	}	
}

async function loadRegLevelData(){
	let url=`/registlevel/page?count=100&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		registLes.value=result.data.records 
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')
	}	
}

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

//加载用户数据
async function loadData(pn){
	loading.value=true
	let url=`/user/page?count=${ps.value}&pn=${pn}`
	if(kw!='')
		url+=`&keyword=${kw.value}`
	if(userType!='')
		url+=`&userType=${userType.value}`
	if(dept!='')
		url+=`&dept=${dept.value}`
	if(docType!='')
		url+=`&docType=${docType.value}`
	const result = await fetchData(url,null);
	if(result.result){
		data.value = result.data
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
//显示编辑框
function showEdit(item){
	editDialogVisible.value=true
	editform.value=item
}
function  closeDialog(){
	editform.value={
		"id": '',
		"userName": "",
		"realName": "",
		"useType": '',
		"useTypeName": "",
		"docTitleID": '',
		"docTitle": "",
		"isScheduling": "",
		"deptID": '',
		"dept": "",
		"registLeID": '',
		"registLe": ""
	}
}

//保存编辑内容
function editSave(){
	console.log(editform.value.deptType)
	postReq("/user/update",editform.value).then(resp=>{
		if(resp.data.result){
			editDialogVisible.value=false
			loadData(data.value.current)
			ElMessageBox.alert('修改成功', '提示',{})
			
		}else{
			if(result.errMsg=='未登录')
				router.push('/login')
			ElMessageBox.alert(resp.data.errMsg, '提示',{})
		}
		
	})
}
//保存编辑内容
function save(){
	postReq("/user/add",editform.value).then(resp=>{
		if(resp.data.result){
			addDialogVisible.value=false
			ElMessageBox.alert('新增成功', '提示',{})
			
		}else{
			if(result.errMsg=='未登录')
				router.push('/login')
			ElMessageBox.alert(resp.data.errMsg, '提示',{})
		}	
	})
}

function del(id){
	ElMessageBox.confirm(
	    '确认是否删除?',
	    'Warning',
	    {
	      confirmButtonText: 'OK',
	      cancelButtonText: 'Cancel',
	      type: 'warning',
	    }
	  )
	.then(() => {
		var formData = new FormData();
		formData.append("id", id);
		postReq("/user/del",formData).then(resp=>{
			if(resp.data.result){
				loadData(data.value.current)
				ElMessageBox.alert('删除成功', '提示',{})
				
			}else{
				if(result.errMsg=='未登录')
					router.push('/login')
				ElMessageBox.alert(resp.data.errMsg, '提示',{})
			}
		})
	})
}

</script>

<style>
</style>