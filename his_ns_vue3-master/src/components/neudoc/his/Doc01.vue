<template>
<!-- 页面正文 -->
<el-main style="width:100%;background:#fff;height:800px;overflow-y: auto">
	<el-form ref="form"  label-width="80px" label-position="left" size="mini" >
		<el-row style="background-color: #EAF1F5;margin-top: -20px">
			<el-col :span="8" style="margin-top: 4px;"></el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-tickets" >暂存</el-button>
			</el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-success" >提交</el-button>
			</el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-printer" >清空所有</el-button>
			</el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-circle-plus-outline" >刷新</el-button>
			</el-col>
		</el-row>
		<el-row>
			<el-col :span="24" align="left" >
				<el-tag>病史内容：</el-tag>
			</el-col>
		</el-row>

		<el-form-item label="主诉">
			<el-input type="textarea" :rows="1" v-model="medicalRecord.Readme" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		<el-form-item label="现病史">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.Present" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		<el-form-item label="现病治疗情况">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.PresentTreat" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		<el-form-item label="既往史">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.History" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		<el-form-item label="过敏史">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.Allergy" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		<el-form-item label="体格检查">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.Physique" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		
	</el-form>
</el-main>
</template>

<script setup>
import { ref,onMounted } from 'vue'
import { getReq } from '../../../utils/api'


const loading=ref(false)//是否在加载数据
const isSaved=ref(false)//是否保存
const medicalRecord=ref({})//病历首案

onMounted(async () => {

});

//获取病人病历首页信息信息
async function loadMedicalRecord(rid){
	loading.value=true
	getReq(`/neudoc/getMedicalRecord?rid=${rid}`).then(response=>{
		if(response.data.result&&response.data.data){
			medicalRecord.value=response.data.data
		}
	}).catch(err=>{
		
	})
	
}


</script>

<style>
</style>