<template>
	<!-- 搜索栏 start -->
	<el-row class="search-bar">
		<div class="search-controls">
			<el-select v-model="ctype" filterable 
				clearable @clear="loadData(1)"
				placeholder="常数据类别"  @change="loadData(1)" >
				<el-option
				  v-for="item in types"
				  :key="item.id"
				  :label="item.constantTypeCode+item.constantTypeName"
				  :value="item.id"/>
			</el-select>
			<el-input v-model.trim="kw"
			  clearable @clear="loadData(1)"
			  placeholder="请输入常数项编码或名称"/>
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</div>
		<el-button type="primary" @click="addDialogVisible=true">新增数据项</el-button>
	</el-row>
	<div style="margin-top: 40px;"></div>
	<!-- 搜索栏 end -->
	<!-- 表格栏 start -->
	<el-row class="m-2">
		<el-col :span="24">
			<el-table :data="data.records" style="width:100%;"
				v-loading="loading">
				<el-table-column fixed type="index" label="序号" align="center" width="80" 
					:index="index => (data.current - 1) * data.size + index + 1" />
			    <el-table-column prop="constantTypeName" label="类别"  align="center" />
				<el-table-column prop="constantCode" label="编码" align="center"/>
			    <el-table-column prop="constantName" label="名称"  align="center" />
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
			v-model:current-page="currentPage"
			@current-change="handleCurrentChange"/>
		</el-col>
		<el-col :span="4">
			<el-select v-model="ps" class="w-20 m-2" placeholder="每页行数" @change="loadData(1)" >
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
	<el-dialog v-model="editDialogVisible" title="编辑" @close="handleClose('edit')">
	    <el-form :model="form" :rules="rules" ref="editFormRef" label-width="100px">
			<el-form-item label="类别" prop="constantTypeID">
				<el-select v-model="form.constantTypeID" filterable placeholder="常数据类别">
				    <el-option
				      v-for="item in types"
				      :key="item.id"
				      :label="item.constantTypeName"
				      :value="item.id"/>
				</el-select>		
			</el-form-item> 
			<el-form-item label="项编码" prop="constantCode">
				<el-input v-model="form.constantCode" autocomplete="off" />
			</el-form-item>  
			<el-form-item label="项名称" prop="constantName">
				<el-input v-model="form.constantName" autocomplete="off" />
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
	<el-dialog v-model="addDialogVisible" title="新增" @close="handleClose('add')">
	    <el-form :model="form" :rules="rules" ref="addFormRef" label-width="100px">
			<el-form-item label="类别" prop="constantTypeID">
				<el-select v-model="form.constantTypeID" filterable placeholder="常数据类别">
				    <el-option
				      v-for="item in types"
				      :key="item.id"
				      :label="item.constantTypeName"
				      :value="item.id"/>
				</el-select>		
			</el-form-item> 
			<el-form-item label="项编码" prop="constantCode">
				<el-input v-model="form.constantCode" autocomplete="off" />
			</el-form-item>  
			<el-form-item label="项名称" prop="constantName">
				<el-input v-model="form.constantName" autocomplete="off" />
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
import { ElMessage, ElMessageBox } from 'element-plus'

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

// 表单ref
const addFormRef = ref(null)
const editFormRef = ref(null)

//表单对象
const form=ref({
	"id": 0,
	"constantTypeID": '',
	"constantCode": "",
	"constantName": ""
})
//表单验证
const rules=ref({
	constantTypeID: [
		{ required: true, message: '请选择类别', trigger: 'change' }
	],
	constantCode: [
		{ required: true, message: '请输入项编码', trigger: 'blur' }
	],
	constantName: [
		{ required: true, message: '请输入项名称', trigger: 'blur' }
	]
})

const currentPage = ref(1)

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
	currentPage.value = pn
	
	let params = new URLSearchParams()
	params.append('pn', pn)
	params.append('count', ps.value)
	
	if(kw.value) params.append('keyword', kw.value)
	if(ctype.value) params.append('ctype', ctype.value)
	
	const result = await fetchData(`/constantitem/page?${params.toString()}`,null);
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
	editDialogVisible.value = true
	form.value = { ...item }
}

function handleClose(type) {
  if (type === 'edit' && editFormRef.value) {
    editFormRef.value.resetFields()
  } else if (type === 'add' && addFormRef.value) {
    addFormRef.value.resetFields()
  }
  form.value = { id: 0, constantTypeID: '', constantCode: '', constantName: '' }
}

//保存编辑内容
async function editSave() {
  if (!editFormRef.value) return
  await editFormRef.value.validate((valid) => {
    if (valid) {
      postReq("/constantitem/update", form.value).then(resp => {
        if (resp.data.result) {
          editDialogVisible.value = false
          loadData(data.value.current)
          ElMessage.success('修改成功')
        } else {
          ElMessage.error(resp.data.errMsg || '修改失败')
        }
      })
    }
  })
}

//保存新增内容
async function save() {
  if (!addFormRef.value) return
  await addFormRef.value.validate((valid) => {
    if (valid) {
      postReq("/constantitem/add", form.value).then(resp => {
        if (resp.data.result) {
          addDialogVisible.value = false;
          currentPage.value = 1;
          loadData(1);
          ElMessage.success('新增成功');
        } else {
          ElMessage.error(resp.data.errMsg || '新增失败')
        }
      })
    }
  })
}

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

<style scoped>
.search-bar {
	display: flex;
	justify-content: space-between; /* 两端对齐 */
	align-items: center;
	margin-bottom: 20px;
}

.search-controls {
	display: flex;
	align-items: center;
	gap: 15px; /* 使用 gap 设置控件之间的间距 */
}
</style>