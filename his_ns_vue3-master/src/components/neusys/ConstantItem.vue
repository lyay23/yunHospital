<template>
	<!-- 搜索栏 start -->
	<el-row>
		<el-col :span="5">
			<div class="grid-content ep-bg-purple-dark" >
				<el-select v-model="ctype" class="m-2" filterable 
					clearable @clear="loadData(1)"
					placeholder="常数据类别"  @change="loadData(1)" >
				    <el-option
				      v-for="item in types"
				      :key="item.id"
				      :label="item.constantTypeCode+item.constantTypeName"
				      :value="item.id"/>
				</el-select>
			</div>
		</el-col>
	    <el-col :span="5">
			<div class="grid-content ep-bg-purple-dark" >	
				<el-input v-model.trim="kw"
				  placeholder="请输入常数项编码或名称" class="w-100 m-2"/>
			</div>
		</el-col>
		<el-col :span="1">
		</el-col>
		<el-col :span="4" class="m-2">
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</el-col>
		<el-col :span="5">			
		</el-col>
		<el-col :span="4">
			<el-button type="danger" @click="addDialogVisible=true">新增数据项</el-button>
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
			    <el-table-column prop="constantTypeName" label="类别名称"  align="center" />
				<el-table-column prop="constantCode" label="常数据项编码" align="center"/>
				<el-table-column prop="constantName" label="常数据项名称"  align="center" />
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
	    <el-form :model="editform" :rules="rules">
			<el-form-item label="编号" :label-width="formLabelWidth" prop="id" >
			  <el-input v-model="editform.id" readonly autocomplete="off" />
			</el-form-item>
			<el-form-item label="类别编码" :label-width="formLabelWidth" prop="constantTypeCode">
				<el-select v-model="editform.constantTypeID" class="m-2" filterable 
					placeholder="常数据类别"  >
				    <el-option
				      v-for="item in types"
				      :key="item.id"
				      :label="item.constantTypeName"
				      :value="item.id"/>
				</el-select>		
			</el-form-item> 
			<el-form-item label="类别编码" :label-width="formLabelWidth" prop="constantTypeCode">
				<el-input v-model="editform.constantCode" autocomplete="off" />
			</el-form-item>  
			<el-form-item label="类别名称" :label-width="formLabelWidth" prop="constantTypeName">
				<el-input v-model="editform.constantName" autocomplete="off" />
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
	    <el-form :model="editform" :rules="rules">
			<el-form-item label="编号" v-show="false" :label-width="formLabelWidth" prop="id" >
			  <el-input v-model="editform.id" readonly autocomplete="off" />
			</el-form-item>
			<el-form-item label="类别编码" :label-width="formLabelWidth" prop="constantTypeCode">
				<el-select v-model="editform.constantTypeID" class="m-2"  filterable
					placeholder="常数据类别"  >
				    <el-option
				      v-for="item in types"
				      :key="item.id"
				      :label="item.constantTypeName"
				      :value="item.id"/>
				</el-select>		
			</el-form-item> 
			<el-form-item label="类别编码" :label-width="formLabelWidth" prop="constantTypeCode">
				<el-input v-model="editform.constantCode" autocomplete="off" />
			</el-form-item>  
			<el-form-item label="类别名称" :label-width="formLabelWidth" prop="constantTypeName">
				<el-input v-model="editform.constantName" autocomplete="off" />
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
const types=ref([])
//搜索栏 文本框
const kw=ref('')
//搜索栏 下拉框选中值
const ctype=ref('')
//每页行数
const ps=ref(10)
//行数集
const pageSizes=[10,20,30,50]
//url
const typeUrl='/constant/page'


//新增对话框是否显示
const addDialogVisible = ref(false)
//编辑对话框是否显示
const editDialogVisible = ref(false)
//表单对象
const editform=ref({
	"id": 0,
	"constantTypeID": '',
	"constantCode": "",
	"constantName": ""
})
//表单验证
const rules=ref({
	constantCode: [
		{ required: true, message: '请输入项编码', trigger: 'blur' }
	],
	constantName: [
		{ required: true, message: '请输入项名称', trigger: 'blur' }
	]
})

//组件挂载后事件
onMounted(async() => {
  loadData(1)
  loadTypeData()
});

//获取常数据类别
async function loadTypeData(){
	let url=`/constant/page?count=50&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		types.value=result.data.records 
	}else{
		if(result.errMsg=='未登录')
			router.push('/login')	
	}	
}
//加载数据
async function loadData(pn){
	loading.value=true
	let url=`/constantitem/page?count=${ps.value}&pn=${pn}`
	if(kw!='')
	{
		url+=`&keyword=${kw.value}`
	}
	if(ctype!='')
	{
		url+=`&ctype=${ctype.value}`
	}
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
		"id": 0,
		"constantTypeID": '',
		"constantCode": "",
		"constantName": ""
	}
}

//保存编辑内容
function editSave(){
	postReq("/constantitem/update",editform.value).then(resp=>{
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
	postReq("/constantitem/add",editform.value).then(resp=>{
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
		postReq("/constantitem/del",formData).then(resp=>{
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