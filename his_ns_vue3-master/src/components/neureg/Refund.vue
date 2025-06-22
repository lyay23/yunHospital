<template>
	<!-- 搜索栏 start -->
	<el-row>
	    <el-col :span="5">
			<el-input v-model.trim="kw"
				  placeholder="请输入姓名或手机或身份证号码" class="w-100 m-2"/>
		</el-col>
		<el-col :span="1">
		</el-col>
		<el-col :span="4" class="m-2">
			<el-button type="primary" @click="loadData(1)" >查询</el-button>
		</el-col>
		<el-col :span="10">			
		</el-col>
		
	</el-row>
	<div style="margin-top: 40px;"></div>
	<!-- 搜索栏 end -->
	<!-- 表格栏 start -->
	<el-row class="m-2">
		<el-col :span="24">
			<el-table :data="data.records" style="width:100%;"
			 v-loading="loading">
				
			    <el-table-column prop="caseNumber" label="病历号" align="center" width="90" />
			    <el-table-column prop="realName" label="姓名"  align="center" width="100" />
			    <el-table-column prop="gender" label="性别"   width="70" align="center">
					<template #default="scope">
					    {{scope.row.gender==71?"男":"女"}}
					</template>
				</el-table-column>
			    <el-table-column prop="idNumber" label="身份证"  align="center" width="180" />
				<el-table-column prop="visitDate" label="挂号日期"  align="center" />
			    <el-table-column prop="noon" label="午别"  align="center" />
				<el-table-column prop="deptName" label="科室"  align="center" />
				<el-table-column prop="doctorName" label="医生"  align="center" />
				<el-table-column prop="registName" label="号别"  align="center" />
				<el-table-column fixed="right" label="操作"  align="center" >
				  <template #default="scope">
					<el-button link type="danger" @click="refund(scope.row)">退号</el-button>
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
	
	
</template>

<script setup>
import { ref,onMounted } from 'vue'
import { fetchData,postReq } from '../../utils/api'
import { ElMessageBox, ElMessage } from 'element-plus'
import router from '../../router';
import { formatDate } from '../../utils/utils.js'
import { useUserStore } from '../../store/user.js'
//获取用户仓库对象
const userStore=useUserStore()
//data start
const loading=ref(false)
const data = ref({})
//搜索框
const kw=ref('')
const deptId=ref('')
//每页行数
const ps=ref(10)
const pageSizes=[10,20,30,50]
const currentPage = ref(1)





onMounted(async () => {
  loadData(1)
});

//加载数据
async function loadData(pn){
	loading.value = true
	try {
		currentPage.value = pn
		var date=formatDate(new Date())
		
		const params = new URLSearchParams()
		params.append('pn', pn)
		params.append('count', ps.value)
		params.append('state', 1)
		params.append('regDate', date)
		params.append('_', new Date().getTime())
		
		if(kw.value) params.append('keyword', kw.value)
		if(deptId.value) params.append('deptId', deptId.value)
		
		const result = await fetchData(`/register/page?${params.toString()}`,null);
		if(result.result){
			data.value = result.data
		}else{
			if(result.errMsg=='未登录')
				router.push('/login')		
		}	
	} finally {
		loading.value = false
	}
}



//分页
function handleCurrentChange (number)  {
	loadData(number)
}

//退号
function refund(item){
	ElMessageBox.confirm(
	    '确认是否退号?',
	    '提醒',
	    {
	      confirmButtonText: '确认',
	      cancelButtonText: '取消',
	      type: 'warning',
	    }
	)
	.then(() => {
		var param={
			"registerVo":item,
			"userVo":userStore.getUserInfo.value
		}
		postReq("/register/backOff",param).then(resp=>{
			if(resp.data.result){
				ElMessage.success('退号成功')
				// 如果删除的是当前页的最后一条数据，则加载前一页
				if (data.value.records.length === 1 && data.value.current > 1) {
					loadData(data.value.current - 1)
				} else {
					loadData(data.value.current)
				}
			}else{
				ElMessage.error(resp.data.errMsg || '退号失败')
			}
			
		})
	})
}


</script>

<style>
</style>