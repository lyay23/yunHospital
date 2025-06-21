<template>
	<!-- 搜索栏 start -->
	<el-row class="search-bar">
		<div class="search-controls">
			<el-select v-model="ctype" filterable 
				clearable @clear="loadData(1)"
				placeholder="疾病分类"  @change="loadData(1)" >
				<el-option
				  v-for="item in types"
				  :key="item.id"
				  :label="item.dicaCode+item.dicaName"
				  :value="item.id"/>
			</el-select>
			<el-input v-model.trim="kw"
			  placeholder="请输入疾病编码或名称"/>
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</div>
		<el-button type="primary" @click="addDialogVisible=true">新增诊断疾病</el-button>
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
				<el-table-column prop="diseaseName" label="疾病名称"  align="center" />
				<el-table-column prop="diseaseICD" label="国际ICD编码" align="center"/>
				<el-table-column prop="diseCategoryName" label="疾病分类"  align="center" />
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
			<el-form-item label="疾病分类" prop="diseCategoryID">
				<el-select v-model="form.diseCategoryID" filterable placeholder="疾病分类" >
				    <el-option
				      v-for="item in types"
				      :key="item.id"
				      :label="item.dicaName"
				      :value="item.id"/>
				</el-select>		
			</el-form-item> 
			<el-form-item label="疾病编码" prop="diseaseCode">
				<el-input v-model="form.diseaseCode" autocomplete="off" />
			</el-form-item>  
			<el-form-item label="疾病名称" prop="diseaseName">
				<el-input v-model="form.diseaseName" autocomplete="off" />
			</el-form-item>
			<el-form-item label="国际ICD编码" prop="diseaseICD">
				<el-input v-model="form.diseaseICD" autocomplete="off" />
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
			<el-form-item label="疾病分类" prop="diseCategoryID">
				<el-select v-model="form.diseCategoryID" filterable placeholder="疾病分类" >
				    <el-option
				      v-for="item in types"
				      :key="item.id"
				      :label="item.dicaName"
				      :value="item.id"/>
				</el-select>		
			</el-form-item> 
			<el-form-item label="疾病编码" prop="diseaseCode">
				<el-input v-model="form.diseaseCode" autocomplete="off" />
			</el-form-item>  
			<el-form-item label="疾病名称" prop="diseaseName">
				<el-input v-model="form.diseaseName" autocomplete="off" />
			</el-form-item>
			<el-form-item label="国际ICD编码" prop="diseaseICD">
				<el-input v-model="form.diseaseICD" autocomplete="off" />
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
const currentPage = ref(1)

//新增对话框是否显示
const addDialogVisible = ref(false)
//编辑对话框是否显示
const editDialogVisible = ref(false)

// 表单ref
const addFormRef = ref(null)
const editFormRef = ref(null)

//表单对象
const form=ref({
	"id": "",
	"diseaseCode": "",
	"diseaseName": "",
	"diseaseICD": "",
	"diseCategoryID": ""
})

//表单验证
const rules=ref({
	diseCategoryID: [ { required: true, message: '请选择疾病分类', trigger: 'change' } ],
	diseaseCode: [ { required: true, message: '请输入疾病编码', trigger: 'blur' } ],
	diseaseName: [ { required: true, message: '请输入疾病名称', trigger: 'blur' } ],
	diseaseICD: [ { required: true, message: '请输入国际ICD编码', trigger: 'blur' } ]
})

//组件挂载后事件
onMounted(async() => {
  loadData(1)
  loadTypeData()
});

//获取常数据类别
async function loadTypeData(){
	let url=`/disecategory/page?count=1000&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		types.value=result.data.records 
	}else{
		if(result.errMsg=='未登录'){
			router.push('/login')
		}
	}	
}
//加载数据
async function loadData(pn){
	loading.value=true
	let url=`/disease/page?count=${ps.value}&pn=${pn}`
	if(kw!='')
		url+=`&keyword=${kw.value}`
	
	if(ctype!='')
		url+=`&ctype=${ctype.value}`
	
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
	editDialogVisible.value = true
	form.value = { ...item }
}

function handleClose(type) {
  const formRef = type === 'edit' ? editFormRef.value : addFormRef.value;
  if (formRef) {
    formRef.resetFields();
  }
  form.value = { id: '', diseaseCode: '', diseaseName: '', diseaseICD: '', diseCategoryID: '' };
}

//保存编辑内容
async function editSave(){
  if (!editFormRef.value) return;
  await editFormRef.value.validate((valid) => {
    if (valid) {
      postReq("/disease/update", form.value).then(resp => {
        if (resp.data.result) {
          editDialogVisible.value = false;
          loadData(data.value.current);
          ElMessage.success('修改成功');
        } else {
          ElMessage.error(resp.data.errMsg || '修改失败');
        }
      });
    }
  });
}

//保存新增内容
async function save(){
  if (!addFormRef.value) return;
  await addFormRef.value.validate((valid) => {
    if (valid) {
      postReq("/disease/add", form.value).then(resp => {
        if (resp.data.result) {
          addDialogVisible.value = false;
          currentPage.value = 1;
          loadData(1);
          ElMessage.success('新增成功');
        } else {
          ElMessage.error(resp.data.errMsg || '新增失败');
        }
      });
    }
  });
}

function del(id){
	ElMessageBox.confirm(
	    '确认是否删除?',
	    '提醒',
	    {
	      confirmButtonText: '确认',
	      cancelButtonText: '取消',
	      type: 'warning',
	    }
	  )
	.then(() => {
		var formData = new FormData();
		formData.append("id", id);
		postReq("/disease/del",formData).then(resp=>{
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