<template>
	<!-- 搜索栏 start -->
	<el-row>
		<el-col :span="5">
			<div class="grid-content ep-bg-purple-dark" >
				<el-select v-model="dept" class="m-2" filterable 
					clearable @clear="loadData(1)"
					placeholder="所属科室"  @change="loadData(1)" >
				    <el-option
				      v-for="item in depts"
				      :key="item.id"
				      :label="item.deptCode+item.deptName"
				      :value="item.id"/>
				</el-select>
			</div>
		</el-col>
	    <el-col :span="5">
			<div class="grid-content ep-bg-purple-dark" >	
				<el-input v-model.trim="kw"
				  placeholder="请输入姓名或排班名" class="w-100 m-2"/>
			</div>
		</el-col>
		<el-col :span="1">
		</el-col>
		<el-col :span="4" class="m-2">
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</el-col>
		<el-col :span="5">			
		</el-col>
		<el-col :span="4">
			<el-button type="danger" @click="addDialogVisible=true">新增排班规则</el-button>
		</el-col>
	</el-row>
	<div style="margin-top: 40px;"></div>
	<!-- 搜索栏 end -->
	<!-- 表格栏 start -->
	<el-row class="m-2">
		<el-col :span="24">
			<el-table :data="data.records" style="width:100%;"
			 v-loading="loading">
			    <el-table-column fixed prop="id" label="序号" width="80"  align="center" />
			    <el-table-column prop="ruleName" label="规则名称"  align="center" />
				<el-table-column prop="deptName" label="所属科室" align="center"/>
				<el-table-column prop="realName" label="姓名" align="center"/>
				<el-table-column prop="week" label="规则"  align="center" />
				<el-table-column prop="num" label="可预约人数"  align="center" />
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
	    <el-form :model="editform" :rules="rules" label-width="100">
			<el-form-item label="编号"  prop="id" >
			  <el-input v-model="editform.id" readonly autocomplete="off" />
			</el-form-item>
			<el-form-item label="所属科室"  prop="constantTypeCode">
				<el-select v-model="editform.deptID" class="m-2" filterable 
					placeholder="所属科室"  >
				    <el-option
				      v-for="item in depts"
				     :key="item.id"
				     :label="item.deptCode+item.deptName"
				     :value="item.id"/>
				</el-select>		
			</el-form-item> 
			<el-form-item label="医生"  prop="constantTypeCode">
				<el-select v-model="editform.userID" class="m-2" filterable 
					placeholder="医生" @change="loadUserData"  >
				    <el-option
				      v-for="item in users"
				     :key="item.id"
				     :label="item.realName"
				     :value="item.id"/>
				</el-select>		
			</el-form-item> 
			<el-form-item label="规则名称"  prop="ruleName">
				<el-input v-model="editform.ruleName" autocomplete="off" />
			</el-form-item>  
			<el-form-item label="可预约人数"  prop="num">
				<el-input v-model="editform.num" autocomplete="off" />
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
				<el-text class="mx-1" style="margin-right:60px;"></el-text>
				<template v-for="(item,index) in morning" :key="index">
				     <el-checkbox v-model="item.isShow"
					   :checked="editform.week[index*2]==1?true:false"
					   style="margin-right:50px;"></el-checkbox>
				</template>
			</el-form-item>
			<el-form-item label="下午">
				<el-text class="mx-1" style="margin-right:60px;"></el-text>
				<template v-for="(item,index) in afternoon" :key="index">
				     <el-checkbox v-model="item.isShow" 
						 :checked="editform.week[index*2+1]==1?true:false"
						style="margin-right:50px;">
					 </el-checkbox>
				</template>
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
	   <el-form :model="editform" :rules="rules" label-width="100">
	   	<el-form-item label="编号"  prop="id" >
	   	  <el-input v-model="editform.id" readonly autocomplete="off" />
	   	</el-form-item>
	   	<el-form-item label="所属科室"  prop="constantTypeCode">
	   		<el-select v-model="editform.deptID" class="m-2" filterable 
	   			placeholder="所属科室"  @change="loadUserData()" >
	   		    <el-option
	   		      v-for="item in depts"
	   		     :key="item.id"
	   		     :label="item.deptCode+item.deptName"
	   		     :value="item.id"/>
	   		</el-select>		
	   	</el-form-item> 
	   	<el-form-item label="医生"  prop="constantTypeCode">
	   		<el-select v-model="editform.userID" class="m-2" filterable 
	   			placeholder="医生"  >
	   		    <el-option
	   		      v-for="item in users"
	   		     :key="item.id"
	   		     :label="item.realName"
	   		     :value="item.id"/>
	   		</el-select>		
	   	</el-form-item> 
	   	<el-form-item label="规则名称"  prop="ruleName">
	   		<el-input v-model="editform.ruleName" autocomplete="off" />
	   	</el-form-item>  
	   	<el-form-item label="可预约人数"  prop="num">
	   		<el-input v-model="editform.num" autocomplete="off" />
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
			<el-text class="mx-1" style="margin-right:80px;"></el-text>
			<template v-for="(item,index) in morning" :key="index">
			     <el-checkbox v-model="item.isShow"
				   :checked="editform.week[index*2]==1?true:false"
				   style="margin-right:50px;"></el-checkbox>
			</template>
	   	</el-form-item>
		<el-form-item label="下午">
			<el-text class="mx-1" style="margin-right:80px;"></el-text>
			<template v-for="(item,index) in afternoon" :key="index">
			     <el-checkbox v-model="item.isShow" 
					 :checked="editform.week[index*2+1]==1?true:false"
					style="margin-right:50px;" >
				 </el-checkbox>
			</template>
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
import { ref,onMounted,watchEffect} from 'vue'
import { fetchData,postReq } from '../../utils/api'
import { ElMessageBox } from 'element-plus'

//加载页码
const loading=ref(false)
//分页信息
const data = ref({})
//table 数据
const list=ref([])
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


//新增对话框是否显示
const addDialogVisible = ref(false)
//编辑对话框是否显示
const editDialogVisible = ref(false)
//表单对象
const editform=ref({
	"id": "",
	"ruleName": "",
	"deptID": "",
	"deptName": "",
	"userID": "",
	"userName": "",
	"week": "00000000000000",
	"num": ""
})
const afternoon=ref([{isShow:false},
		{isShow:false},
		{isShow:false},
		{isShow:false},
		{isShow:false},
		{isShow:false},
		{isShow:false}])
let morning=ref([{isShow:false},
		{isShow:false},
		{isShow:false},
		{isShow:false},
		{isShow:false},
		{isShow:false},
		{isShow:false}])


//表单验证
const rules=ref({
	constantCode: [
		{ required: true, message: '请输入项编码', trigger: 'blur' }
	],
	constantName: [
		{ required: true, message: '请输入项名称', trigger: 'blur' }
	]
})

//组件挂载后事件
onMounted(async() => {
  loadData(1)
  loadDeptData()
});


async function loadDeptData(){
	let url=`/department/page?&count=200&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		depts.value=result.data.records 
	}else{
		if(result.errMsg=='未登录'){
			router.push('/login')
		}
	}	
}

async function loadUserData(){
	let url=`/user/page?&dept=${editform.value.deptID}&count=200&pn=1`
	const result = await fetchData(url,null);
	if(result.result){
		users.value=result.data.records 
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
	if(kw!='')
		url+=`&keyword=${kw.value}`
	
	if(dept!='')
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
	loadUserData()
	editDialogVisible.value=true
	editform.value=item
	for (let i = 0; i < 7; i++) {
		morning.value[i].isShow=(item.week[i*2]=="1"?true:false)
		afternoon.value[i].isShow=(item.week[i*2+1]=="1"?true:false)
	}
}
function  closeDialog(){
	morning.value.forEach((item,index)=>{
		item.isShow=false
		afternoon.value[index].isShow=false
	})
	editform.value={
		"id": "",
		"ruleName": "",
		"deptID": "",
		"deptName": "",
		"userID": "",
		"userName": "",
		"week": "00000000000000",
		"num": ""
	}
	
}

//保存编辑内容
function editSave(){
	getWeek()
	postReq("/rule/update",editform.value).then(resp=>{
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
	getWeek()
	postReq("/rule/add",editform.value).then(resp=>{
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
	    '确认是否删除?','Warning',
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
				ElMessageBox.alert('删除成功', '提示',{})
				
			}else{
				if(result.errMsg=='未登录')
					router.push('/login')
				ElMessageBox.alert(resp.data.errMsg, '提示',{})
			}
		})
	})
}

function getWeek(){
	let week=""
	morning.value.forEach((item,index)=>{
		if(item.isShow)
			week+="1"
		else
			week+="0"
		
		if(afternoon.value[index].isShow)
			week+="1"
		else
			week+="0"
	})
	editform.value.week=week;
}

</script>

<style>
</style>