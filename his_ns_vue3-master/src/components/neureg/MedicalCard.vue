<template>
	<!-- 搜索栏 start -->
	<el-row>
	    <el-col :span="5">
			<el-input v-model.trim="kw"
				  placeholder="请输入姓名或身份证号码" class="w-100 m-2"/>
		</el-col>
		<el-col :span="1">
		</el-col>
		<el-col :span="4" class="m-2">
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</el-col>
		<el-col :span="10">			
		</el-col>
		<el-col :span="4">
			<el-button type="danger" @click="addDialogVisible=true">新增电子就诊卡</el-button>
		</el-col>
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
				          <p m="t-0 b-2">关联用户: {{ scope.row.customerName }}</p>
				          <p m="t-0 b-2">关&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系: {{ scope.row.relationshipName }}</p>
				          <p m="t-0 b-2">家庭地址: {{ scope.row.addr }}</p>
				          <p m="t-0 b-2">注册时间: {{ scope.row.createdate }}</p>
						  <p m="t-0 b-2">注册渠道: {{ scope.row.channelName }}</p>
				        </div>
				      </template>
				</el-table-column>
			    <el-table-column prop="id" label="序号" align="center" width="70" />
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
				<el-table-column fixed="right" label="Operations"  align="center" >
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
	<el-dialog v-model="editDialogVisible" title="编辑" @close="closeDialog">
	    <el-form :model="editform" :rules="rules" label-width="100px">
			<el-form-item label="编号"  prop="id" >
			  <el-input v-model="editform.id" readonly autocomplete="off" />
			</el-form-item>
			<el-form-item label="卡号"  prop="cardNo" >
			  <el-input v-model="editform.cardNo" readonly autocomplete="off" />
			</el-form-item>
			<el-form-item label="姓名" prop="realname">
				<el-input v-model="editform.realname" autocomplete="off" />
			</el-form-item>  
			<el-form-item label="身份证号" prop="idnumber">
				<el-input v-model="editform.idnumber" autocomplete="off" />
			</el-form-item>
			<el-form-item label="出生日期" prop="birthdate">
				<el-input v-model="editform.birthdate" autocomplete="off" />
			</el-form-item>	
			<el-form-item label="性别" prop="gender">
				<el-select v-model="editform.gender" class="m-2" >
					<el-option label="男"  value="71"/>
					<el-option label="女"  value="72"/>
				</el-select>
			</el-form-item>
			<el-form-item label="手机号码" prop="phone">
				<el-input v-model="editform.phone" autocomplete="off" />
			</el-form-item>
			<el-form-item label="家庭住址" prop="addr">
				<el-input v-model="editform.addr" autocomplete="off" />
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
	    	<el-form-item label="编号"  prop="id" >
	    	  <el-input v-model="editform.id" readonly autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="姓名" prop="realname">
	    		<el-input v-model="editform.realname" autocomplete="off" />
	    	</el-form-item>  
	    	<el-form-item label="身份证号" prop="idnumber" >
	    		<el-input v-model="editform.idnumber" autocomplete="off" @blur="getBirthDay()" />
	    	</el-form-item>
	    	<el-form-item label="出生日期" prop="birthdate">
	    		<el-input v-model="editform.birthdate" autocomplete="off" />
	    	</el-form-item>
	    	<el-form-item label="性别" prop="gender" >
	    		<el-select v-model="editform.gender" class="m-2" placeholder="请选择性别">
	    			<el-option label="男"  value="71"/>
	    			<el-option label="女"  value="72"/>
	    		</el-select>
	    	</el-form-item>
			<el-form-item label="就诊卡类型" prop="cardtype" >
				<el-select v-model="editform.cardtype" class="m-2" placeholder="请选择卡类型" >
					<el-option label="普通"  value="185"/>
					<el-option label="医保卡"  value="186"/>
				</el-select>
			</el-form-item>
			<el-form-item label="医保卡号" prop="cardNo" v-show="editform.cardtype==198">
				<el-input v-model="editform.cardNo" autocomplete="off" />
			</el-form-item> 
	    	<el-form-item label="手机号码" prop="phone">
	    		<el-input v-model="editform.phone" autocomplete="off" />
	    	</el-form-item>
			<el-form-item label="家庭住址" prop="addr">
				<el-input v-model="editform.addr" autocomplete="off" />
			</el-form-item>
			<el-form-item label="注册渠道" prop="channel" v-show="false">
				<el-input v-model="editform.channel"  autocomplete="off" />
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
//搜索框
const kw=ref('')
//每页行数
const ps=ref(10)
const pageSizes=[10,20,30,50]


//编辑框是否显示
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
//表单对象
const editform=ref({
	"id": "",
	"realname": "",
	"gender": "",
	"idnumber": "",
	"birthdate": "",
	"phone": "",
	"addr": "",
	"cardtype": "",
	"cardtypeName": "",
	"cardNo": "",
	"customerId": "",
	"customerName": "",
	"relationship": "",
	"relationshipName": "",
	"createdate": "",
	"channel": "",
	"channelName": ""
})
//表单验证规则
const rules=ref({
	realname: [
		{ required: true, message: '请输入姓名', trigger: 'blur' }
	],
	gender: [
		{ required: true, message: '请选择性别', trigger: 'blur' }
	],
	idnumber: [
		{ pattern: /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, 
			message: '请输入有效身份证号码', trigger: 'blur' },
	],
	phone: [
		{ pattern: /^1[3456789]\d{9}$/, message: '请输入有效手机号', trigger: 'blur' }
	],
	addr: [
		{ required: true, message: '请输入家庭住址', trigger: 'blur' }
	],
	channel: [
		{ required: true, message: '请选择就诊卡类型', trigger: 'blur' }
	]
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
	editDialogVisible.value=true
	editform.value=item
}

//关闭对话框，清空表单
function  closeDialog(){
	editform.value={
		"id": "",
		"realname": "",
		"gender": "",
		"idnumber": "",
		"birthdate": "",
		"phone": "",
		"addr": "",
		"cardtype": "",
		"cardtypeName": "",
		"cardNo": "",
		"customerId": "",
		"customerName": "",
		"relationship": "",
		"relationshipName": "",
		"createdate": "",
		"channel": "",
		"channelName": ""
	}
}

//保存编辑内容
function editSave(){
	postReq("/medicalcard/update",editform.value).then(resp=>{
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
	editform.value.channel=181
	postReq("/medicalcard/add",editform.value).then(resp=>{
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

function getBirthDay(){
	
	if(editform.value.idnumber!=""){
		var idCard=editform.value.idnumber
		// 提取出生日期
		var birthday = idCard.substring(6, 14);
		var year = birthday.substring(0, 4);
		var month = birthday.substring(4, 6);
		var day = birthday.substring(6, 8);
		
		editform.value.birthdate=year+"-"+month+"-"+day
		
		
		// 提取性别
		var genderCode = parseInt(idCard.charAt(16));
		var gender = genderCode % 2 === 0 ? "0" : "1";
		editform.value.gender=gender
		
	}
}

</script>

<style>
</style>