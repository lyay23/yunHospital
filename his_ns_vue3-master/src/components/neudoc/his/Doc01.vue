<template>
<!-- 页面正文 -->
<el-main style="width:100%;background:#fff;height:800px;overflow-y: auto">
	<el-form ref="form"  label-width="80px" label-position="left" size="mini" >
		<el-row style="background-color: #EAF1F5;margin-top: -20px">
			<el-col :span="8" style="margin-top: 4px;"></el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-tickets" @click="save">暂存</el-button>
			</el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-success" @click="submit">提交</el-button>
			</el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-printer" @click="clearAll">清空所有</el-button>
			</el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-circle-plus-outline" @click="refresh">刷新</el-button>
			</el-col>
		</el-row>
		<el-row>
			<el-col :span="24" align="left" >
				<el-tag>病史内容：</el-tag>
			</el-col>
		</el-row>

		<el-form-item label="主诉">
			<el-input type="textarea" :rows="1" v-model="medicalRecord.readme" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		<el-form-item label="现病史">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.present" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		<el-form-item label="现病治疗情况">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.presentTreat" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		<el-form-item label="既往史">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.history" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		<el-form-item label="过敏史">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.allergy" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		<el-form-item label="体格检查">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.physique" 
				:disabled="isSaved"></el-input>
		</el-form-item>

		<el-form-item label="检查建议">
			<el-input type=
			"textarea" :rows="2" v-model="medicalRecord.proposal" 
				:disabled="isSaved"></el-input>
		</el-form-item>

		<el-form-item label="注意事项">
			<el-input type=
			"textarea" :rows="2" v-model="medicalRecord.careful" 
				:disabled="isSaved"></el-input>
		</el-form-item>

		<el-form-item label="检查结果">
			<el-input type=
			"textarea" :rows="2" v-model="medicalRecord.checkResult" 
				:disabled="isSaved"></el-input>
		</el-form-item>

		<el-form-item label="诊断结果">
			<el-input type=
			"textarea" :rows="2" v-model="medicalRecord.diagnosis" 
				:disabled="isSaved"></el-input>
		</el-form-item>

		<el-form-item label="处理意见">
			<el-input type=
			"textarea" :rows="2" v-model="medicalRecord.handling" 
				:disabled="isSaved"></el-input>
		</el-form-item>
		
	</el-form>
</el-main>
</template>

<script setup>
import { ref,onMounted, defineExpose } from 'vue'
import { getReq,postReq } from '../../../utils/api'
import { ElMessageBox, ElMessage } from 'element-plus'


const loading=ref(false)//是否在加载数据
const isSaved=ref(false)//是否保存
const medicalRecord=ref({})//病历首案
const currentRid = ref(null)//病历首案

onMounted(async () => {

});

//获取病人病历首页信息信息
async function loadMedicalRecord(rid){
	currentRid.value = rid;
	loading.value=true
	getReq(`/neudoc/getMedicalRecord?rid=${rid}`).then(response=>{
		if(response.data.result&&response.data.data){
			medicalRecord.value=response.data.data
		}else{
			medicalRecord.value = { registID: rid } 
		}
	}).catch(err=>{
		ElMessage.error('加载病历失败');
	})
	
}

function save(){
	if (!medicalRecord.value || !medicalRecord.value.registID) {
	    ElMessage.error('请先选择一位患者');
	    return;
	}
	postReq('/neudoc/save',medicalRecord.value).then(resp=>{
		if(resp.data.result){
			ElMessage.success('暂存成功')
		}else{
			ElMessage.error(resp.data.errMsg || '暂存失败')
		}
	}).catch(err => {
	    ElMessage.error('网络错误，暂存失败')
	})
}
function submit(){
	ElMessageBox.confirm(
	    '确认是否提交?提交后无法修改',
	    '提醒',
	    {
	      confirmButtonText: '确认',
	      cancelButtonText: '取消',
	      type: 'warning',
	    }
	)
	.then(() => {
		medicalRecord.value.state=2
		save()
	})
}

function clearAll(){
	ElMessageBox.confirm(
	    '确认是否清空?',
	    '提醒',
	    {
	      confirmButtonText: '确认',
	      cancelButtonText: '取消',
	      type: 'warning',
	    }
	)
	.then(() => {
		medicalRecord.value={}
	})
}

function refresh() {
    if (currentRid.value) {
        loadMedicalRecord(currentRid.value);
        ElMessage.success('刷新成功');
    } else {
        ElMessage.warning('请先选择一位患者');
    }
}

function clearForm() {
    medicalRecord.value = {};
    currentRid.value = null;
}

defineExpose({
    loadMedicalRecord,
    clearForm
})

</script>

<style>
</style>