<template>
	<!-- 搜索栏 start -->
	<el-row>
	    <el-col :span="5">
			<div class="grid-content ep-bg-purple-dark" >
				 <el-input v-model.trim="kw"
				  placeholder="请输入费用科目编码或名称" class="w-100 m-2"/>
			</div>
		</el-col>
		<el-col :span="1">
		</el-col>
		<el-col :span="4" class="m-2">
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</el-col>
		<el-col :span="10">			
		</el-col>
		<el-col :span="4">
			<el-button type="danger" @click="addDialogVisible=true">新增常数类别</el-button>
		</el-col>
	</el-row>
	<div style="margin-top: 40px;"></div>
	<!-- 搜索栏 end -->
	<!-- 表格栏 start -->
	<el-row class="m-2">
		<el-col :span="24">
			<el-table :data="data.records" style="width:100%;"
			 v-loading="loading">
			    <el-table-column fixed prop="id" label="序号" align="center" width="80" />
			    <el-table-column prop="expCode" label="科目编号"  align="center" />
			    <el-table-column prop="expName" label="科目名称"  align="center" />
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
			<el-select v-model="ps" class="w-20 m-2" placeholder="每页行数" @change="loadData(1)" >
			    <el-option
			      v-for="item in pageSizes"
			      :key="item"
			      :label="item"
			      :value="item"
			    />
			  </el-select>
		</el-col>
	</el-row>
	<!-- page end -->
	
	<!-- 编辑常数据类别对话框 start -->
	<el-dialog v-model="editDialogVisible" title="编辑" @close="closeDialog">
	    <el-form :model="editform" :rules="rules" label-width="100px">
			<el-form-item label="编号"  prop="id" >
			  <el-input v-model="editform.id" readonly autocomplete="off" />
			</el-form-item>
	      <el-form-item label="科目编码" prop="expCode">
	        <el-input v-model="editform.expCode" autocomplete="off" />
	      </el-form-item>  
		  <el-form-item label="科目名称" prop="expName">
		    <el-input v-model="editform.expName" autocomplete="off" />
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
	  <!-- 编辑常数据类别对话框  end-->
	  
	<!-- 新增常数据类别对话框 start -->
	<el-dialog v-model="addDialogVisible" title="新增" @close="closeDialog">
	    <el-form :model="editform" :rules="rules" label-width="100px">
			<el-form-item label="编号" v-show="false"  prop="id" >
			  <el-input v-model="editform.id" readonly autocomplete="off" />
			</el-form-item>
	      <el-form-item label="科目编码"  prop="expCode">
	        <el-input v-model="editform.expCode" autocomplete="off" />
	      </el-form-item>  
		  <el-form-item label="科目名称"  prop="expName">
		    <el-input v-model="editform.expName" autocomplete="off" />
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
	 
	<!--常数据项  start  --> 
	<!--常数据项  end  --> 
</template>

<script setup>
import { ref,onMounted } from 'vue'
import { fetchData,postReq } from '../../utils/api'
import { ElMessageBox } from 'element-plus'
import router from '../../router';

//data start
const loading=ref(false)
const data = ref({})
const kw=ref('')
const ps=ref(10)
const pageSizes=[10,20,30,50]



const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editform=ref({
	"id": "",
	"expCode": "",
	"expName": ""
})

const rules=ref({
	expCode: [
		{ required: true, message: '请输入科目编码', trigger: 'blur' }
	],
	expName: [
		{ required: true, message: '请输入科目名称', trigger: 'blur' }
	]
})
//data end


onMounted(async () => {
  loadData(1)
});

//加载数据
async function loadData(pn){
	loading.value=true
	let url=`/expenseclass/page?count=${ps.value}&pn=${pn}`
	if(kw!='')
		url+=`&keyword=${kw.value}`

	const result = await fetchData(url,null);
	if(result.result){
		data.value = result.data
		loading.value=false
	}else{
		if(result.errMsg=='未登录'){
			router.push('/login')
		}
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
		"id": "",
		"expCode": "",
		"expName": ""
	}
}

//保存编辑内容
function editSave(){
	postReq("/expenseclass/update",editform.value).then(resp=>{
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
	postReq("/expenseclass/add",editform.value).then(resp=>{
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
		postReq("/expenseclass/del",formData).then(resp=>{
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