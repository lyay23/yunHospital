<template>
	<!-- 搜索栏 start -->
	<el-row class="search-bar">
		<div class="search-controls">
			<el-select v-model="ctype" filterable 
				clearable @clear="loadData(1)"
				placeholder="科目类型"  @change="loadData(1)" >
				<el-option v-for="item in types"
					:key="item.id"
					:label="item.constantCode+item.constantName"
					:value="item.id"/>
			</el-select>
			<el-select v-model="dept" filterable
				clearable @clear="loadData(1)"
				placeholder="执行科室"  @change="loadData(1)" >
			    <el-option
			      v-for="item in depts"
			      :key="item.id"
			      :label="item.deptCode+item.deptName"
			      :value="item.id"/>
			</el-select>
			<el-select v-model="expens" filterable
				clearable @clear="loadData(1)"
				placeholder="费用科目"  @change="loadData(1)" >
			    <el-option
			      v-for="item in expenss"
			      :key="item.id"
			      :label="item.expCode+item.expName"
			      :value="item.id"/>
			</el-select>
			<el-input v-model.trim="kw" 
				  placeholder="请输入费用编码或名称"/>
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</div>
		<el-button type="primary" @click="addDialogVisible=true">新增非药品收费项目</el-button>
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
			    <el-table-column prop="recordTypeName" label="项目类型"  align="center" />
				<el-table-column prop="itemCode" label="项目编码" align="center"/>
			    <el-table-column prop="itemName" label="项目名称"  align="center" />
				<el-table-column prop="format" label="规格" align="center"/>
				<el-table-column prop="price" label="单价" align="center"/>
				<el-table-column prop="expName" label="费用科目"  align="center" />
				<el-table-column prop="deptName" label="执行科室"  align="center" />
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
			<el-form-item label="项目类型" prop="recordType">
				<el-select v-model="form.recordType" placeholder="项目类型">
					<el-option v-for="item in types"
						:key="item.id"
						:label="item.constantName"
						:value="item.id"/>
				</el-select>
			</el-form-item>	
			<el-form-item label="执行科室" prop="deptID">
				<el-select v-model="form.deptID" filterable placeholder="执行科室">
					<el-option v-for="item in depts"
						:key="item.id"
						:label="item.deptName"
						:value="item.id"/>
				</el-select>
			</el-form-item>
			<el-form-item label="费用科目" prop="expClassID">
				<el-select v-model="form.expClassID" filterable placeholder="费用科目">
					<el-option v-for="item in expenss"
						:key="item.id"
						:label="item.expName"
						:value="item.id"/>
				</el-select>
			</el-form-item>
		
			<el-form-item label="项目编码" prop="itemCode" >
			  <el-input v-model="form.itemCode"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="项目名称"  prop="itemName" >
			  <el-input v-model="form.itemName"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="规格"  prop="format" >
			  <el-input v-model="form.format"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="单价"  prop="price" >
			  <el-input v-model.number="form.price"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="助记码"  prop="mnemonicCode" >
			  <el-input v-model="form.mnemonicCode"  autocomplete="off" />
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
	    	<el-form-item label="项目类型"  prop="recordType">
	    		<el-select v-model="form.recordType" placeholder="项目类型">
	    			<el-option v-for="item in types"
	    				:key="item.id"
	    				:label="item.constantName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>	
	    	<el-form-item label="执行科室"  prop="deptID">
	    		<el-select v-model="form.deptID" filterable placeholder="执行科室">
	    			<el-option v-for="item in depts"
	    				:key="item.id"
	    				:label="item.deptName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>
	    	<el-form-item label="费用科目"  prop="expClassID">
	    		<el-select v-model="form.expClassID" filterable placeholder="费用科目">
	    			<el-option v-for="item in expenss"
	    				:key="item.id"
	    				:label="item.expName"
	    				:value="item.id"/>
	    		</el-select>
	    	</el-form-item>	
	    	<el-form-item label="项目编码" prop="itemCode" >
	    	  <el-input v-model="form.itemCode"  autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="项目名称"  prop="itemName" >
	    	  <el-input v-model="form.itemName"  autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="规格"  prop="format" >
	    	  <el-input v-model="form.format"  autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="单价"  prop="price" >
	    	  <el-input v-model.number="form.price"  autocomplete="off" />
	    	</el-form-item>
			<el-form-item label="助记码"  prop="mnemonicCode" >
			  <el-input v-model="form.mnemonicCode"  autocomplete="off" />
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
import { ref, onMounted } from 'vue'
import { fetchData, postReq } from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '../../router'

// 加载状态
const loading = ref(false)

// 分页和表格数据
const data = ref({})
const ps = ref(10)
const pageSizes = [10, 20, 30, 50]
const currentPage = ref(1)

// 搜索栏 v-model
const kw = ref('')
const ctype = ref('')
const dept = ref('')
const expens = ref('')

// 搜索栏下拉框数据源
const types = ref([])
const depts = ref([])
const expenss = ref([])

// 对话框可见性
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)

// 表单 ref
const addFormRef = ref(null)
const editFormRef = ref(null)

// 表单对象
const form = ref({})
const defaultForm = {
	id: '',
	itemCode: '',
	itemName: '',
	format: '',
	price: null,
	expClassID: '',
	deptID: '',
	mnemonicCode: '',
	recordType: ''
}

// 表单验证规则
const rules = ref({
	itemCode: [{ required: true, message: '请输入项目编码', trigger: 'blur' }],
	itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
	price: [
		{ required: true, message: '请输入单价', trigger: 'blur' },
		{ type: 'number', message: '单价必须为数字' }
	],
	expClassID: [{ required: true, message: '请选择费用科目', trigger: 'change' }],
	deptID: [{ required: true, message: '请选择执行科室', trigger: 'change' }],
	recordType: [{ required: true, message: '请选择项目类型', trigger: 'change' }]
})

// 加载表格数据
function loadData(pageNo = 1) {
	loading.value = true
	currentPage.value = pageNo

	const params = new URLSearchParams()
	params.append('pn', pageNo)
	params.append('count', ps.value)
	
	if (kw.value) params.append('keyword', kw.value)
	if (ctype.value) params.append('ctype', ctype.value)
	if (dept.value) params.append('dept', dept.value)
	if (expens.value) params.append('expClassId', expens.value)
	
	fetchData(`/fmeditem/page?${params.toString()}`)
		.then(res => {
			if (res.result) {
				data.value = res.data
			} else if (res.errMsg === '未登录') {
				router.push('/login')
			} else {
				ElMessage.error(res.errMsg || '数据加载失败')
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

// 加载下拉框数据
const loadDepts = async () => {
    const res = await fetchData('/department/page?count=1000')
	if(res.result) depts.value = res.data.records
}
const loadTypes = async () => {
	const res = await fetchData('/constantitem/page?ctype=15&count=100&pn=1')
	if(res.result) types.value = res.data.records
}
const loadExpenss = async () => {
	const res = await fetchData('/expenseclass/page?count=1000')
	if(res.result) expenss.value = res.data.records
}

// 组件挂载
onMounted(() => {
	loadData()
	loadDepts()
	loadTypes()
	loadExpenss()
})

// 分页改变
function handleCurrentChange(pageNo) {
	loadData(pageNo)
}

// 对话框关闭
const handleClose = (type) => {
	if(type === 'add' && addFormRef.value) {
		addFormRef.value.resetFields()
	}
	if(type === 'edit' && editFormRef.value) {
		editFormRef.value.resetFields()
	}
}

// 新增
const save = () => {
	addFormRef.value.validate(valid => {
		if (valid) {
			postReq('/fmeditem/add', form.value).then(res => {
				if (res) {
					ElMessage.success('新增成功')
					addDialogVisible.value = false
					loadData(1)
				} else {
					ElMessage.error('新增失败')
				}
			})
		}
	})
}

// 显示编辑
const showEdit = (row) => {
	form.value = { ...row }
	editDialogVisible.value = true
}

// 编辑保存
const editSave = () => {
	editFormRef.value.validate(valid => {
		if(valid) {
			postReq('/fmeditem/update', form.value).then(res => {
				if (res) {
					ElMessage.success('修改成功')
					editDialogVisible.value = false
					loadData(currentPage.value)
				} else {
					ElMessage.error('修改失败')
				}
			})
		}
	})
}

// 删除
const del = (id) => {
	ElMessageBox.confirm('确认删除该项目吗?', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	}).then(() => {
		postReq(`/fmeditem/del?id=${id}`).then(res => {
			if (!res.errMsg) {
				ElMessage.success('删除成功')
				if (data.value.records.length === 1 && currentPage.value > 1) {
					currentPage.value--
				}
				loadData(currentPage.value)
			} else {
				ElMessage.error(res.errMsg)
			}
		})
	}).catch(() => { /* 取消操作 */ })
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