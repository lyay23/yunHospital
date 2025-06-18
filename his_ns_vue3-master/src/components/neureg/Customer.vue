<template>
	<!-- 搜索栏 start -->
	<el-row>
		<el-col :span="5" style="padding-right: 20px;">
				<el-input v-model.trim="kw"
				  placeholder="请输入姓名或身份证号" class="w-100 m-2"/>
		</el-col>
	    <el-col :span="5">
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</el-col>
		<el-col :span="14">
		</el-col>
	</el-row>
	<div style="margin-top: 40px;"></div>
	<!-- 搜索栏 end -->
	<!-- 表格栏 start -->
	<el-row class="m-2">
		<el-col :span="24">
			<el-table :data="data.records" style="width:100%;"
				v-loading="loading">
			    <el-table-column fixed prop="id" label="序号" width="70"  align="center" />
			    <el-table-column prop="realName" label="姓名" width="100" align="center" />
				<el-table-column prop="gender" label="性别" width="70"  align="center">
					<template #default="scope">
					    {{scope.row.gender==1?"男":"女"}}
					</template>
				</el-table-column>
				<el-table-column prop="idnumber" label="身份证号"  align="center" />
			    <el-table-column prop="birthdate" label="出生日期"  align="center" />
				<el-table-column prop="phone" label="手机"  align="center" />
			    <el-table-column prop="createdate" label="注册日期"  align="center" />
			     <el-table-column prop="channelName" label="注册渠道"  align="center" />
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


//组件挂载后事件
onMounted(async() => {
  loadData(1)
});


//加载数据
async function loadData(pn){
	loading.value=true
	let url=`/customer/page?count=${ps.value}&pn=${pn}`
	if(kw!='')
	{
		url+=`&keyword=${kw.value}`
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






</script>

<style>
</style>