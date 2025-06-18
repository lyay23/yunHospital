<template>
	<!-- 搜索栏 start -->
	<el-row>
		<el-col :span="3" style="padding-right: 20px;">
			<el-select v-model="ctype" class="m-2" filterable 
				clearable @clear="loadData(1)"
				placeholder="科目类型"  @change="loadData(1)" >
				<el-option v-for="item in types"
					:key="item.id"
					:label="item.constantCode+item.constantName"
					:value="item.id"/>
			</el-select>
		</el-col>
		
		<el-col :span="3" style="padding-right: 20px;">
			<el-select v-model="dept" class="m-2" filterable
				clearable @clear="loadData(1)"
				placeholder="执行科室"  @change="loadData(1)" >
			    <el-option
			      v-for="item in depts"
			      :key="item.id"
			      :label="item.deptCode+item.deptName"
			      :value="item.id"/>
			</el-select>
		</el-col>
		<el-col :span="3" style="padding-right: 20px;">
			<el-select v-model="expens" class="m-2" filterable
				clearable @clear="loadData(1)"
				placeholder="费用科目"  @change="loadData(1)" >
			    <el-option
			      v-for="item in expenss"
			      :key="item.id"
			      :label="item.expCode+item.expName"
			      :value="item.id"/>
			</el-select>
			
		</el-col>
		<el-col :span="5" style="padding-right: 20px;">
			<el-input v-model.trim="kw" 
				  placeholder="请输入费用编码或名称" class="w-100 m-2"/>
		</el-col>
		<el-col :span="3">
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</el-col>
		<el-col :span="3"></el-col>
		<el-col :span="4">
			<el-button type="danger" @click="addDialogVisible=true">新增非药品收费项目</el-button>
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
			    <el-table-column prop="itemCode" label="项目编码"  align="center" />
				<el-table-column prop="itemName" label="项目名称" align="center"/>
				<el-table-column prop="format" label="规格"  align="center" />
			    <el-table-column prop="price" label="单价"  align="center" />
				<el-table-column prop="deptName" label="执行科室"  align="center" />
				<el-table-column prop="mnemonicCode" label="助记码"  align="center" />
				<el-table-column prop="recordTypeName" label="项目类型"  align="center" />
			
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
			<el-form-item label="项目类型"  prop="recordType">
				<el-select v-model="editform.recordType" class="m-2" placeholder="项目类型"   >
					<el-option v-for="item in types"
						:key="item.id"
						:label="item.constantCode+item.constantName"
						:value="item.id"/>
				</el-select>
			</el-form-item>	
			<el-form-item label="执行科室"  prop="deptID">
				<el-select v-model="editform.deptID"  filterable class="m-2" placeholder="执行科室"   >
					<el-option v-for="item in depts"
						:key="item.id"
						:label="item.deptCode+item.deptName"
						:value="item.id"/>
				</el-select>
			</el-form-item>
			<el-form-item label="费用科目"  prop="docTitleID">
				<el-select v-model="editform.expClassID" class="m-2" filterable placeholder="费用科目"   >
					<el-option v-for="item in expenss"
						:key="item.id"
						:label="item.expCode+item.expName"
						:value="item.id"/>
				</el-select>
			</el-form-item>
		
			<el-form-item label="项目编码" prop="itemCode" >
			  <el-input v-model="editform.itemCode"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="项目名称"  prop="itemName" >
			  <el-input v-model="editform.itemName"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="规格"  prop="format" >
			  <el-input v-model="editform.format"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="单价"  prop="price" >
			  <el-input v-model="editform.price"  autocomplete="off" />
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
	    	<el-form-item label="项目类型"  prop="recordType">
	    		<el-select v-model="editform.recordType" class="m-2" placeholder="项目类型"   >
	    			<el-option v-for="item in types"
	    				:key="item.id"
	    				:label="item.constantCode+item.constantName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>	
	    	<el-form-item label="执行科室"  prop="deptID">
	    		<el-select v-model="editform.deptID"  filterable class="m-2" placeholder="执行科室"   >
	    			<el-option v-for="item in depts"
	    				:key="item.id"
	    				:label="item.deptCode+item.deptName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>
	    	<el-form-item label="费用科目"  prop="docTitleID">
	    		<el-select v-model="editform.expClassID" class="m-2" filterable placeholder="费用科目"   >
	    			<el-option v-for="item in expenss"
	    				:key="item.id"
	    				:label="item.expCode+item.expName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>	
	    	<el-form-item label="项目编码" prop="itemCode" >
	    	  <el-input v-model="editform.itemCode"  autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="项目名称"  prop="itemName" >
	    	  <el-input v-model="editform.itemName"  autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="规格"  prop="format" >
	    	  <el-input v-model="editform.format"  autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="单价"  prop="price" >
	    	  <el-input v-model="editform.price"  autocomplete="off" />
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
//项目类型集合
const types=ref([])
//科室集合
const depts=ref([])
//费用科目
const expenss=ref([])
//搜索栏 文本框
const kw=ref('')
//搜索栏 下拉框选中值
const ctype=ref('')
const dept=ref('')
const expens=ref('')
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
	"id": "",
	"itemCode": "",
	"itemName": "",
	"format": "",
	"price": "",
	"expClassID": "",
	"expName": "",
	"deptID": "",
	"deptName": "",
	"mnemonicCode": "",
	"creationDate": "",
	"lastUpdateDate": "",
	"recordType": "",
	"recordTypeName": ""
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
  //项目类型
  loadTypeData(15,100,types)
  //费用科目
  loadExpenseData()
});

async function loadTypeData(cid,count,ay){
	let url=`/constantitem/page?ctype=${cid}&count=${count}&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		ay.value=result.data.records 
	}else{
		if(result.errMsg=='未登录'){
			router.push('/login')
		}
	}	
}



async function loadExpenseData(){
	let url=`/expenseclass/page?count=1000&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		expenss.value=result.data.records 
	}else{
		if(result.errMsg=='未登录'){
			router.push('/login')
		}
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
	let url=`/fmeditem/page?count=${ps.value}&pn=${pn}`
	if(kw!='')
		url+=`&keyword=${kw.value}`
	if(ctype!='')
		url+=`&ctype=${ctype.value}`
	if(dept!='')
		url+=`&dept=${dept.value}`
	if(expens!='')
		url+=`&expClassId=${expens.value}`
		
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
	postReq("/fmeditem/update",editform.value).then(resp=>{
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
	postReq("/fmeditem/add",editform.value).then(resp=>{
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
	    '确认是否删除?','Warning',
	    {
	      confirmButtonText: 'OK',
	      cancelButtonText: 'Cancel',
	      type: 'warning',
	    }
	  )
	.then(() => {
		var formData = new FormData();
		formData.append("id", id);
		postReq("/fmeditem/del",formData).then(resp=>{
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