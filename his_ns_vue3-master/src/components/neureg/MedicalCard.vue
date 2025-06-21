<template>
	<!-- 搜索栏 start -->
	<el-row class="search-bar">
		<div class="search-controls">
			<el-input v-model.trim="kw"
				  placeholder="请输入姓名或身份证号码"/>
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</div>
		<el-button type="primary" @click="addDialogVisible=true">新增电子就诊卡</el-button>
	</el-row>
	<div style="margin-top: 40px;"></div>
	<!-- 搜索栏 end -->
	<!-- 表格栏 start -->
	<el-row class="m-2">
		<el-col :span="24">
			<el-table :data="data.records" style="width:100%;"
			 v-loading="loading">
				<el-table-column type="expand">
				      <template #default="scope">
				        <div m="4" style="padding-left: 50px;">
				          <p m="t-0 b-2">家庭地址: {{ scope.row.addr }}</p>
				          <p m="t-0 b-2">注册时间: {{ scope.row.createdate }}</p>
						  <p m="t-0 b-2">注册渠道: {{ scope.row.channelName }}</p>
				        </div>
				      </template>
				</el-table-column>
			    <el-table-column type="index" label="序号" align="center" width="70" 
					:index="index => (data.current - 1) * data.size + index + 1" />
			    <el-table-column prop="realname" label="姓名"  align="center" width="100" />
			    <el-table-column prop="gender" label="性别"   width="70" align="center">
					<template #default="scope">
					    {{scope.row.gender==1?"男":"女"}}
					</template>
				</el-table-column>
			    <el-table-column prop="idnumber" label="身份证"  align="center" />
				<el-table-column prop="birthdate" label="出生日期"  align="center" />
			    <el-table-column prop="phone" label="手机号"  align="center" />
				<el-table-column prop="cardtypeName" label="卡类型"  align="center" />
				<el-table-column fixed="right" label="操作"  align="center" >
				  <template #default="scope">
				    <el-button link type="primary" @click="showEdit(scope.row)">编辑</el-button>
					<el-button link type="danger" @click="del(scope.row.id)">注销</el-button>
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
					:value="item" />
			  </el-select>
		</el-col>
	</el-row>
	<!-- page end -->
	
	<!-- 编辑常数据类别对话框 start -->
	<el-dialog v-model="editDialogVisible" title="编辑" @close="handleClose('edit')">
	    <el-form :model="form" :rules="rules" ref="editFormRef" label-width="100px">
			<el-form-item label="卡号" prop="cardNo" >
			  <el-input v-model="form.cardNo"  autocomplete="off" />
			</el-form-item>
			<el-form-item label="姓名" prop="realname">
				<el-input v-model="form.realname" autocomplete="off" />
			</el-form-item>  
			<el-form-item label="身份证号" prop="idnumber">
				<el-input v-model="form.idnumber" autocomplete="off" @blur="getBirthDay" />
			</el-form-item>
			<el-form-item label="出生日期" prop="birthdate">
				<el-input v-model="form.birthdate" autocomplete="off" />
			</el-form-item>	
			<el-form-item label="性别" prop="gender">
				<el-select v-model="form.gender" placeholder="请选择性别">
					<el-option label="男"  :value="71"/>
					<el-option label="女"  :value="72"/>
				</el-select>
			</el-form-item>
			<el-form-item label="手机号码" prop="phone">
				<el-input v-model="form.phone" autocomplete="off" />
			</el-form-item>
			<el-form-item label="家庭住址" prop="addr">
				<el-input v-model="form.addr" autocomplete="off" />
			</el-form-item>
			<el-form-item label="注册渠道" prop="channel">
				<el-select v-model="form.channel" placeholder="请选择注册渠道">
					<el-option v-for="item in channelOptions" :key="item.value" :label="item.label" :value="item.value" />
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
	  <!-- 编辑常数据类别对话框  end-->
	  
	<!-- 新增常数据类别对话框 start -->
	<el-dialog v-model="addDialogVisible" title="新增就诊卡" @close="handleClose('add')">
	    <el-form :model="form" :rules="rules" ref="addFormRef" label-width="100px">
	    	<el-form-item label="姓名" prop="realname">
	    		<el-input v-model="form.realname" autocomplete="off" />
	    	</el-form-item>  
	    	<el-form-item label="身份证号" prop="idnumber" >
	    		<el-input v-model="form.idnumber" autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="出生日期" prop="birthdate">
	    		<el-date-picker
	    		  v-model="form.birthdate"
	    		  type="date"
	    		  placeholder="选择一个日期"
	    		  format="YYYY-MM-DD"
	    		  value-format="YYYY-MM-DD"
	    		/>
	    	</el-form-item>
	    	<el-form-item label="性别" prop="gender" >
	    		<el-select v-model="form.gender" class="m-2" placeholder="请选择性别">
	    			<el-option label="男"  :value="71"/>
	    			<el-option label="女"  :value="72"/>
	    		</el-select>
	    	</el-form-item>
			<el-form-item label="就诊卡类型" prop="cardtype" >
				<el-select v-model="form.cardtype" class="m-2" placeholder="请选择卡类型" >
					<el-option label="普通"  :value="185"/>
					<el-option label="医保卡"  :value="186"/>
				</el-select>
			</el-form-item>
			<el-form-item label="医保卡号" prop="cardNo" v-if="form.cardtype==186">
				<el-input v-model="form.cardNo" autocomplete="off" />
			</el-form-item> 
	    	<el-form-item label="手机号码" prop="phone">
	    		<el-input v-model="form.phone" autocomplete="off" />
	    	</el-form-item>
			<el-form-item label="家庭住址" prop="addr">
				<el-input v-model="form.addr" autocomplete="off" />
			</el-form-item>
			<el-form-item label="注册渠道" prop="channel" >
				<el-select v-model="form.channel" class="m-2" placeholder="请选择注册渠道" >
					<el-option v-for="item in channelOptions" :key="item.value" :label="item.label" :value="item.value" />
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
	 
	<!--常数据项  start  --> 
	<!--常数据项  end  --> 
</template>

<script setup>
import { ref,onMounted } from 'vue'
import { fetchData,postReq } from '../../utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from '../../router';

//data start
const loading=ref(false)
const data = ref({})
//搜索框
const kw=ref('')
//每页行数
const ps=ref(10)
const pageSizes=[10,20,30,50]
const currentPage = ref(1)


//编辑框是否显示
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)

// 表单ref
const addFormRef = ref(null)
const editFormRef = ref(null)

const channelOptions = ref([
  { value: 181, label: '窗口' },
  { value: 182, label: '微信' },
  { value: 183, label: 'AndroidApp' },
  { value: 184, label: '苹果App' }
])

//表单对象
const defaultForm = {
	"id": "",
	"realname": "",
	"gender": "",
	"idnumber": "",
	"birthdate": "",
	"phone": "",
	"addr": "",
	"cardtype": "",
	"cardNo": "",
	"channel": 181,
}
const form=ref(JSON.parse(JSON.stringify(defaultForm)))

//表单验证规则
const rules=ref({
	realname: [ { required: true, message: '请输入姓名', trigger: 'blur' } ],
	gender: [ { required: true, message: '请选择性别', trigger: 'change' } ],
	idnumber: [
		{ required: true, message: '请输入有效身份证号码', trigger: 'blur' },
		{ pattern: /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, 
			message: '请输入有效身份证号码', trigger: 'blur' },
	],
	phone: [
		{ required: true, message: '请输入手机号', trigger: 'blur' },
		{ pattern: /^1[3456789]\d{9}$/, message: '请输入有效手机号', trigger: 'blur' }
	],
	addr: [ { required: true, message: '请输入家庭住址', trigger: 'blur' } ],
	cardtype: [ { required: true, message: '请选择就诊卡类型', trigger: 'change' } ],
	cardNo: [ { required: true, message: '请输入医保卡号', trigger: 'blur' } ],
	birthdate: [ { required: true, message: '请选择出生日期', trigger: 'change' } ],
	channel: [ { required: true, message: '请选择注册渠道', trigger: 'change' } ]
})
//data end


onMounted(async () => {
  loadData(1)
});

//加载数据
async function loadData(pn){
	loading.value=true
	let url=`/medicalcard/page?count=${ps.value}&pn=${pn}`
	if(kw!='')
		url+=`&keyword=${kw.value}`
	
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
	// gender 在表格中是字符串"男"/"女"，在表单中是数字 71/72，需要转换
	const genderValue = typeof item.gender === 'string' ? (item.gender === '男' ? 71 : 72) : item.gender;
	form.value = { ...item, gender: genderValue }
}

function handleClose(type) {
  const formRef = type === 'edit' ? editFormRef.value : addFormRef.value;
  if (formRef) {
    formRef.resetFields();
  }
  form.value = JSON.parse(JSON.stringify(defaultForm));
}

//保存编辑内容
async function editSave(){
  if (!editFormRef.value) return;
  await editFormRef.value.validate((valid) => {
    if (valid) {
      postReq("/medicalcard/update", form.value).then(resp => {
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
      postReq("/medicalcard/add", form.value).then(resp => {
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
		postReq("/medicalcard/del",formData).then(resp=>{
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