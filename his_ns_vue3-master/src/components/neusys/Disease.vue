<template>
	<!-- 搜索栏 start -->
	<el-row class="search-bar">
		<div class="search-controls">
			<el-select v-model="dica" filterable 
				clearable @clear="loadData(1)"
				placeholder="疾病分类"  @change="loadData(1)" >
				<el-option
				  v-for="item in dicas"
				  :key="item.id"
				  :label="item.dicaCode+item.dicaName"
				  :value="item.id"/>
			</el-select>
			<el-input v-model.trim="kw" 
				  placeholder="请输入疾病编码或名称"/>
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</div>
		<el-button type="primary" @click="showAdd">新增诊断疾病</el-button>
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
			    <el-table-column prop="diseaseCode" label="疾病编码"  align="center" />
				<el-table-column prop="diseaseName" label="疾病名称" align="center"/>
			    <el-table-column prop="diseaseICD" label="国际ICD编码"  align="center" />
				<el-table-column prop="dicaName" label="疾病分类" align="center"/>
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
	    <el-form :model="form" :rules="rules" ref="editFormRef" label-width="120px">
			<el-form-item label="疾病编码" prop="diseaseCode" >
			  <el-input v-model="form.diseaseCode"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="疾病名称"  prop="diseaseName" >
			  <el-input v-model="form.diseaseName"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="国际ICD编码"  prop="diseaseICD" >
			  <el-input v-model="form.diseaseICD"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="疾病分类"  prop="diseCategoryID">
				<el-select v-model="form.diseCategoryID" filterable placeholder="疾病分类" >
				    <el-option
				      v-for="item in dicas"
				      :key="item.id"
				      :label="item.dicaName"
				      :value="item.id"/>
				</el-select>		
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
	    <el-form :model="form" :rules="rules" ref="addFormRef" label-width="120px">
			<el-form-item label="疾病编码" prop="diseaseCode" >
			  <el-input v-model="form.diseaseCode"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="疾病名称"  prop="diseaseName" >
			  <el-input v-model="form.diseaseName"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="国际ICD编码"  prop="diseaseICD" >
			  <el-input v-model="form.diseaseICD"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="疾病分类"  prop="diseCategoryID">
				<el-select v-model="form.diseCategoryID" filterable placeholder="疾病分类" >
				    <el-option
				      v-for="item in dicas"
				      :key="item.id"
				      :label="item.dicaName"
				      :value="item.id"/>
				</el-select>		
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
const dica = ref('') // 疾病分类 (Disease Category)

// 搜索栏下拉框数据源
const dicas = ref([])

// 对话框可见性
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)

// 表单 ref
const addFormRef = ref(null)
const editFormRef = ref(null)

// 表单对象
const form = ref({})
const defaultForm = {
	id: null,
	diseaseCode: '',
	diseaseName: '',
	diseaseICD: '',
	diseCategoryID: ''
}

// 表单验证规则
const rules = ref({
	diseaseCode: [{ required: true, message: '请输入疾病编码', trigger: 'blur' }],
	diseaseName: [{ required: true, message: '请输入疾病名称', trigger: 'blur' }],
	diseaseICD: [{ required: true, message: '请输入国际ICD码', trigger: 'blur' }],
	diseCategoryID: [{ required: true, message: '请选择疾病分类', trigger: 'change' }]
})

// 加载表格数据
function loadData(pageNo = 1) {
	if (loading.value) return
	loading.value = true
	currentPage.value = pageNo

	const params = new URLSearchParams()
	params.append('pn', pageNo)
	params.append('count', ps.value)
	
	if (kw.value) params.append('keyword', kw.value)
	if (dica.value) params.append('ctype', dica.value)
	
	fetchData(`/disease/page?${params.toString()}`)
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

// 加载疾病分类下拉框数据
const loadDicas = async () => {
    const res = await fetchData('/disecategory/page?count=1000')
	if(res.result) dicas.value = res.data.records
}

// 组件挂载
onMounted(() => {
	loadData()
	loadDicas()
})

// 分页改变
function handleCurrentChange(pageNo) {
	loadData(pageNo)
}

// 对话框关闭
const handleClose = (type) => {
	const formRef = type === 'add' ? addFormRef.value : editFormRef.value
	if (formRef) {
		formRef.resetFields()
	}
}

// 显示新增
const showAdd = () => {
	form.value = { ...defaultForm }
	addDialogVisible.value = true
}

// 新增保存
const save = () => {
	addFormRef.value.validate(valid => {
		if (valid) {
			postReq('/disease/add', form.value).then(res => {
				if (res.result) {
					ElMessage.success('新增成功')
					addDialogVisible.value = false
					loadData(1)
				} else {
					ElMessage.error(res.errMsg || '新增失败')
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
			postReq('/disease/update', form.value).then(res => {
				if (res.result) {
					ElMessage.success('修改成功')
					editDialogVisible.value = false
					loadData(currentPage.value)
				} else {
					ElMessage.error(res.errMsg || '修改失败')
				}
			})
		}
	})
}

// 删除
const del = (id) => {
	ElMessageBox.confirm('确认删除该疾病吗?', '提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning'
	}).then(() => {
		postReq(`/disease/del?id=${id}`).then(res => {
			if (res.result) {
				ElMessage.success('删除成功')
				if (data.value.records.length === 1 && currentPage.value > 1) {
					currentPage.value--
				}
				loadData(currentPage.value)
			} else {
				ElMessage.error(res.errMsg || '删除失败')
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