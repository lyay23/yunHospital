<template>
	<!-- 搜索栏 start -->
	<el-row class="search-bar">
		<div class="search-controls">
			<el-select v-model="userType" filterable 
				clearable @clear="loadData(1)"
				placeholder="用户类型"  @change="loadData(1)" >
				<el-option v-for="item in userTypes"
					:key="item.id"
					:label="item.constantCode+item.constantName"
					:value="item.id"/>
			</el-select>
			<el-select v-model="dept" filterable
				clearable @clear="loadData(1)"
				placeholder="所在科室"  @change="loadData(1)" >
			    <el-option
			      v-for="item in depts"
			      :key="item.id"
			      :label="item.deptCode+item.deptName"
			      :value="item.id"/>
			</el-select>
			<el-select v-model="docType" filterable
				clearable @clear="loadData(1)"
				placeholder="医生职称"  @change="loadData(1)" >
			    <el-option
			      v-for="item in docTypes"
			      :key="item.id"
			      :label="item.constantCode+item.constantName"
			      :value="item.id"/>
			</el-select>
			<el-input v-model.trim="kw"
				  clearable @clear="loadData(1)"
				  placeholder="请输入用户名或姓名"/>
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</div>
		<el-button type="primary" @click="addDialogVisible=true">新增用户</el-button>
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
			    <el-table-column prop="userName" label="用户名"  align="center" />
				<el-table-column prop="realName" label="姓名" align="center"/>
				<el-table-column prop="useTypeName" label="用户类型"  align="center" />
			    <el-table-column prop="docTitle" label="医生职称"  align="center" />
				<el-table-column prop="dept" label="所在科室"  align="center" />
				<el-table-column prop="isScheduling" label="排班"  align="center" >
					<template #default="scope">
						{{ scope.row.isScheduling === 1 ? '是' : '否' }}
					</template>
				</el-table-column>
				<el-table-column prop="registLe" label="挂号级别"  align="center" />
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
			<el-form-item label="用户名" prop="userName" >
			  <el-input v-model="form.userName"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="姓名"  prop="realName" >
			  <el-input v-model="form.realName"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="用户类型" prop="useType">
				<el-select v-model="form.useType" placeholder="用户类型">
					<el-option v-for="item in userTypes"
						:key="item.id"
						:label="item.constantName"
						:value="item.id"/>
				</el-select>
			</el-form-item>	
			<el-form-item label="所在科室" prop="deptID">
				<el-select v-model="form.deptID" filterable placeholder="所在科室">
					<el-option v-for="item in depts"
						:key="item.id"
						:label="item.deptName"
						:value="item.id"/>
				</el-select>
			</el-form-item>
			<el-form-item label="医生职称" prop="docTitleID">
				<el-select v-model="form.docTitleID" filterable placeholder="医生职称">
					<el-option v-for="item in docTypes"
						:key="item.id"
						:label="item.constantName"
						:value="item.id"/>
				</el-select>
			</el-form-item>
			<el-form-item label="挂号级别" prop="registLeID">
				<el-select v-model="form.registLeID" filterable placeholder="挂号级别">
					<el-option v-for="item in registLes"
						:key="item.id"
						:label="item.registName"
						:value="item.id"/>
				</el-select>
			</el-form-item>
			<el-form-item label="是否排班" prop="isScheduling" >
				 <el-radio-group v-model="form.isScheduling">
				    <el-radio :value="1">是</el-radio>
				    <el-radio :value="0">否</el-radio>
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
	<el-dialog v-model="addDialogVisible" title="新增" @close="handleClose('add')">
	    <el-form :model="form" :rules="rules" ref="addFormRef" label-width="100px">
	    	<el-form-item label="用户名" prop="userName" >
	    	  <el-input v-model="form.userName"  autocomplete="off" />
	    	</el-form-item>
			<el-form-item label="密码" prop="password">
				<el-input v-model="form.password" type="password" show-password autocomplete="off" />
			</el-form-item>
	    	<el-form-item label="姓名"  prop="realName" >
	    	  <el-input v-model="form.realName"  autocomplete="off" />
	    	</el-form-item>
			<el-form-item label="用户类型"  prop="useType">
	    		<el-select v-model="form.useType" filterable placeholder="用户类型">
	    			<el-option v-for="item in userTypes"
	    				:key="item.id"
	    				:label="item.constantName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>	
	    	<el-form-item label="所在科室"  prop="deptID">
	    		<el-select v-model="form.deptID" filterable placeholder="所在科室">
	    			<el-option v-for="item in depts"
	    				:key="item.id"
	    				:label="item.deptName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>
	    	<el-form-item label="医生职称"  prop="docTitleID">
	    		<el-select v-model="form.docTitleID" filterable placeholder="医生职称">
	    			<el-option v-for="item in docTypes"
	    				:key="item.id"
	    				:label="item.constantName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>
	    	<el-form-item label="挂号级别"  prop="registLeID">
	    		<el-select v-model="form.registLeID" filterable placeholder="挂号级别">
	    			<el-option v-for="item in registLes"
	    				:key="item.id"
	    				:label="item.registName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>
	    	<el-form-item label="是否排班"  prop="isScheduling" >
	    		 <el-radio-group v-model="form.isScheduling">
	    		    <el-radio :value="1">是</el-radio>
	    		    <el-radio :value="0">否</el-radio>
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
import { ElMessage, ElMessageBox } from 'element-plus'

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
const currentPage = ref(1)

//新增对话框是否显示
const addDialogVisible = ref(false)
//编辑对话框是否显示
const editDialogVisible = ref(false)

// 表单ref
const addFormRef = ref(null)
const editFormRef = ref(null)

//表单对象
const form=ref({})
const defaultForm = {
	id: null,
	userName: '',
	password: '',
	realName: '',
	useType: '',
	deptID: '',
	docTitleID: '',
	isScheduling: 0,
	registLeID: ''
}

//表单验证
const rules=ref({
	userName: [ { required: true, message: '请输入用户名', trigger: 'blur' } ],
	password: [ { required: true, message: '请输入密码', trigger: 'blur' } ],
	realName: [ { required: true, message: '请输入姓名', trigger: 'blur' } ],
	useType: [ { required: true, message: '请选择用户类型', trigger: 'change' } ],
	deptID: [ { required: true, message: '请选择所在科室', trigger: 'change' } ],
})

//组件挂载后事件
onMounted(async() => {
  loadData(1)
  loadDepts()
  loadConstant(13,userTypes) //用户类型
  loadConstant(8,docTypes) //医生职称
  loadRegistLe()
});

//加载挂号级别
async function loadRegistLe(){
	let url=`/registlevel/page?count=1000&pn=1`
	const result = await fetchData(url,null);
	if(result.result)
		registLes.value=result.data.records 
}
//获取常数据类别
async function loadConstant(cid,ay){
	let url=`/constantitem/page?ctype=${cid}&count=1000&pn=1`
	const result = await fetchData(url,null);
	if(result.result)
		ay.value=result.data.records 
}
//加载科室
async function loadDepts(){
	let url=`/department/page?count=1000&pn=1`
	const result = await fetchData(url,null);
	if(result.result)
		depts.value=result.data.records 
}

//加载数据
async function loadData(pn){
	loading.value=true
	currentPage.value = pn
	
	let params = new URLSearchParams()
	params.append('pn', pn)
	params.append('count', ps.value)
	
	if(kw.value) params.append('keyword', kw.value)
	if(userType.value) params.append('userType', userType.value)
	if(docType.value) params.append('docType', docType.value)
	if(dept.value) params.append('dept', dept.value)
	
	let url=`/user/page?${params.toString()}`
	
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

function del(id){
	ElMessageBox.confirm(
	    '您确定要删除该条记录吗?',
	    '提示',
	    {
	      confirmButtonText: '确定',
	      cancelButtonText: '取消',
	      type: 'warning',
	    }
	  )
	    .then(() => {
			postReq(`/user/del?id=${id}`,null).then(resp=>{
				if(resp.data.result){
					ElMessage({
						type: 'success',
						message: '删除成功',
					})
					loadData(data.value.current);
				}else{
					ElMessage.error(resp.data.errMsg)
				}
			})
	    })
	    .catch(() => {
	      ElMessage({
	        type: 'info',
	        message: '删除已取消',
	      })
	    })
}

//显示编辑框
function showEdit(item){
	form.value={ ...item }
	editDialogVisible.value=true
}

function handleClose(type) {
  const formRef = type === 'add' ? addFormRef.value : editFormRef.value;
  if (formRef) {
    formRef.resetFields();
  }
}

//保存编辑内容
async function editSave(){
	editFormRef.value.validate((valid) => {
		if (valid) {
			postReq("/user/update",form.value).then(resp=>{
				if(resp.data.result){
					editDialogVisible.value=false;
					loadData(data.value.current)
					ElMessage.success('修改成功')
				}else{
					ElMessage.error(resp.data.errMsg)
				}
			})
		}
	})
}
//显示新增框
const showAdd = () => {
	form.value = { ...defaultForm }
	addDialogVisible.value = true
}
//新增
async function save(){
	addFormRef.value.validate((valid) => {
		if (valid) {
			postReq("/user/add",form.value).then(resp=>{
				if(resp.data.result){
					addDialogVisible.value=false;
					currentPage.value = 1
					loadData(1);
					ElMessage.success('新增成功')
				}else{
					ElMessage.error(resp.data.errMsg)
				}
			})
		}
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