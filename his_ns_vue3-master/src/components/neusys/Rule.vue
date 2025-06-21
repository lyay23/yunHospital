<template>
	<!-- 搜索栏 start -->
	<el-row class="search-bar">
		<div class="search-controls">
			<el-select v-model="dept" filterable 
				clearable @clear="loadData(1)"
				placeholder="所属科室"  @change="loadData(1)" >
				<el-option
				  v-for="item in depts"
				  :key="item.id"
				  :label="item.deptCode+item.deptName"
				  :value="item.id"/>
			</el-select>
			<el-input v-model.trim="kw"
			  placeholder="请输入姓名或排班名"/>
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</div>
		<el-button type="primary" @click="addDialogVisible=true">新增排班规则</el-button>
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
			    <el-table-column prop="ruleName" label="规则名称"  align="center" />
				<el-table-column prop="deptName" label="所属科室" align="center"/>
				<el-table-column prop="realName" label="姓名" align="center"/>
				<el-table-column prop="week" label="规则"  align="center" />
				<el-table-column prop="num" label="可预约人数"  align="center" />
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
			<el-form-item label="所属科室" prop="deptID">
				<el-select v-model="form.deptID" filterable placeholder="所属科室" @change="loadUserData(form.deptID)">
				    <el-option
				      v-for="item in depts"
				     :key="item.id"
				     :label="item.deptName"
				     :value="item.id"/>
				</el-select>		
			</el-form-item> 
			<el-form-item label="医生" prop="userID">
				<el-select v-model="form.userID" filterable placeholder="医生">
				    <el-option
				      v-for="item in users"
				     :key="item.id"
				     :label="item.realName"
				     :value="item.id"/>
				</el-select>		
			</el-form-item> 
			<el-form-item label="规则名称" prop="ruleName">
				<el-input v-model="form.ruleName" autocomplete="off" />
			</el-form-item>  
			<el-form-item label="可预约人数" prop="num">
				<el-input v-model.number="form.num" autocomplete="off" />
			</el-form-item>
			<el-form-item label="排班规则">
				<el-text class="mx-1" style="margin-right:80px;"></el-text>
				<el-text class="mx-1" style="margin-right: 20px;">星期一</el-text>
				<el-text class="mx-1" style="margin-right: 20px;">星期二</el-text>
				<el-text class="mx-1" style="margin-right: 20px;">星期三</el-text>
				<el-text class="mx-1" style="margin-right: 20px;">星期四</el-text>
				<el-text class="mx-1" style="margin-right: 20px;">星期五</el-text>
				<el-text class="mx-1" style="margin-right: 20px;">星期六</el-text>
				<el-text class="mx-1" style="margin-right: 20px;">星期天</el-text>
			</el-form-item>		
			<el-form-item label="上午">
				<el-checkbox-group v-model="weekSelection" @change="handleWeekChange">
					<el-checkbox v-for="i in 7" :key="'am-'+i" :label="`am-${i}`" />
				</el-checkbox-group>
			</el-form-item>
			<el-form-item label="下午">
				<el-checkbox-group v-model="weekSelection" @change="handleWeekChange">
					<el-checkbox v-for="i in 7" :key="'pm-'+i" :label="`pm-${i}`" />
				</el-checkbox-group>
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
	   	<el-form-item label="所属科室" prop="deptID">
	   		<el-select v-model="form.deptID" filterable placeholder="所属科室" @change="loadUserData(form.deptID)">
	   		    <el-option
	   		      v-for="item in depts"
	   		     :key="item.id"
	   		     :label="item.deptName"
	   		     :value="item.id"/>
	   		</el-select>		
	   	</el-form-item> 
	   	<el-form-item label="医生" prop="userID">
	   		<el-select v-model="form.userID" filterable placeholder="医生">
	   		    <el-option
	   		      v-for="item in users"
	   		     :key="item.id"
	   		     :label="item.realName"
	   		     :value="item.id"/>
	   		</el-select>		
	   	</el-form-item> 
	   	<el-form-item label="规则名称" prop="ruleName">
	   		<el-input v-model="form.ruleName" autocomplete="off" />
	   	</el-form-item>  
	   	<el-form-item label="可预约人数" prop="num">
	   		<el-input v-model.number="form.num" autocomplete="off" />
	   	</el-form-item>
	   	<el-form-item label="排班规则">
	   		<el-text class="mx-1" style="margin-right:80px;"></el-text>
	   		<el-text class="mx-1" style="margin-right: 20px;">星期一</el-text>
	   		<el-text class="mx-1" style="margin-right: 20px;">星期二</el-text>
	   		<el-text class="mx-1" style="margin-right: 20px;">星期三</el-text>
	   		<el-text class="mx-1" style="margin-right: 20px;">星期四</el-text>
	   		<el-text class="mx-1" style="margin-right: 20px;">星期五</el-text>
	   		<el-text class="mx-1" style="margin-right: 20px;">星期六</el-text>
	   		<el-text class="mx-1" style="margin-right: 20px;">星期天</el-text>
	   	</el-form-item>		
	   	<el-form-item label="上午">
				<el-checkbox-group v-model="weekSelection" @change="handleWeekChange">
					<el-checkbox v-for="i in 7" :key="'am-'+i" :label="`am-${i}`" />
				</el-checkbox-group>
	   	</el-form-item>
		<el-form-item label="下午">
				<el-checkbox-group v-model="weekSelection" @change="handleWeekChange">
					<el-checkbox v-for="i in 7" :key="'pm-'+i" :label="`pm-${i}`" />
				</el-checkbox-group>
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
import { ref,onMounted,watch } from 'vue'
import { fetchData,postReq } from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '../../router';

//加载页码
const loading=ref(false)
//分页信息
const data = ref({})
//搜索栏 下拉框数据集
const depts=ref([])
const users=ref([])
//搜索栏 文本框
const kw=ref('')
//搜索栏 下拉框选中值
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
const form=ref({
	"id": "",
	"ruleName": "",
	"userID": "",
	"num": null,
	"deptID": "",
	"week": "00000000000000"
})

// 用于绑定el-checkbox-group
const weekSelection = ref([])

// 监听 form.week 的变化，更新 weekSelection
watch(() => form.value.week, (newWeek) => {
  const selection = [];
  if (newWeek && newWeek.length === 14) {
    for (let i = 0; i < 7; i++) {
      if (newWeek[i * 2] === '1') selection.push(`am-${i + 1}`);
      if (newWeek[i * 2 + 1] === '1') selection.push(`pm-${i + 1}`);
    }
  }
  weekSelection.value = selection;
});

// 监听 checkbox group 的变化，更新 form.week
const handleWeekChange = (value) => {
  const weekArray = Array(14).fill('0');
  value.forEach(item => {
    const [period, day] = item.split('-');
    const index = (parseInt(day) - 1) * 2 + (period === 'pm' ? 1 : 0);
    weekArray[index] = '1';
  });
  form.value.week = weekArray.join('');
};


//表单验证
const rules=ref({
	ruleName: [ { required: true, message: '请输入规则名称', trigger: 'blur' } ],
	userID: [ { required: true, message: '请选择医生', trigger: 'change' } ],
	num: [
		{ required: true, message: '请输入可预约人数', trigger: 'blur' },
		{ type: 'number', message: '可预约人数必须为数字'}
	],
	deptID: [ { required: true, message: '请选择科室', trigger: 'change' } ]
})

//组件挂载后事件
onMounted(async() => {
  loadData(1)
  loadDeptData()
});

//获取科室
async function loadDeptData(){
	let url=`/department/page?count=1000&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		depts.value=result.data.records 
	}else{
		if(result.errMsg=='未登录'){
			router.push('/login')
		}
	}	
}

//获取医生
async function loadUserData(deptId){
	if (!deptId) {
		users.value = []
		if (form.value) form.value.userID = ''
		return
	}
	let url=`/user/page?count=1000&pn=1&utype=172&deptid=${deptId}`
	const result = await fetchData(url,null);
	if(result.result){
		users.value=result.data.records 
		// 如果切换科室后，之前的医生不在新科室里，则清空医生选项
		if (form.value && form.value.userID && !users.value.find(u => u.id === form.value.userID)) {
			form.value.userID = ''
		}
	}else{
		if(result.errMsg=='未登录'){
			router.push('/login')
		}
	}	
}
//加载数据
async function loadData(pn){
	loading.value=true
	let url=`/rule/page?count=${ps.value}&pn=${pn}`
	if(kw.value!=='')
		url+=`&keyword=${kw.value}`
	
	if(dept.value!=='')
		url+=`&deptId=${dept.value}`
	
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
	form.value = { ...item }
	loadUserData(item.deptID)
	editDialogVisible.value=true
}

function handleClose(type) {
  const formRef = type === 'edit' ? editFormRef.value : addFormRef.value;
  if (formRef) {
    formRef.resetFields();
  }
  form.value = {
    id: '', ruleName: '', userID: '', num: null, deptID: '', week: '00000000000000'
  };
  users.value = []
}

//保存编辑内容
async function editSave(){
  if (!editFormRef.value) return;
  await editFormRef.value.validate((valid) => {
    if (valid) {
      postReq("/rule/update", form.value).then(resp => {
        if (resp.data.result) {
          editDialogVisible.value = false;
          loadData(data.value.current);
          ElMessage.success('修改成功');
        } else {
           if(resp.data.errMsg=='未登录') router.push('/login')
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
      postReq("/rule/add", form.value).then(resp => {
        if (resp.data.result) {
          addDialogVisible.value = false;
          currentPage.value = 1;
          loadData(1);
          ElMessage.success('新增成功');
        } else {
           if(resp.data.errMsg=='未登录') router.push('/login')
          ElMessage.error(resp.data.errMsg || '新增失败');
        }
      });
    }
  });
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
		postReq("/rule/del",formData).then(resp=>{
			if(resp.data.result){
				loadData(data.value.current)
				ElMessage.success('删除成功')
			}else{
				if(resp.data.errMsg=='未登录') router.push('/login')
				ElMessage.error(resp.data.errMsg || '删除失败')
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