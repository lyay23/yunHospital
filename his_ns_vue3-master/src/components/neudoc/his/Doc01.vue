<template>
<!-- 页面正文 -->
<el-main style="width:100%;background:#fff;height:800px;overflow-y: auto">
	<el-form ref="form"  label-width="80px" label-position="left" size="mini" >
		<el-row style="background-color: #EAF1F5;margin-top: -20px">
			<el-col :span="8" style="margin-top: 4px;"></el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-tickets" @click="save" :disabled="isFormDisabled">暂存</el-button>
			</el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-success" @click="submit" :disabled="isFormDisabled">提交</el-button>
			</el-col>
			<el-col :span="4" >
				<el-button type="text" size="small" class="el-icon-printer" @click="clearAll" :disabled="isFormDisabled">清空所有</el-button>
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
				:disabled="isFormDisabled"></el-input>
		</el-form-item>
		<el-form-item label="现病史">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.present" 
				:disabled="isFormDisabled"></el-input>
		</el-form-item>
		<el-form-item label="现病治疗情况">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.presentTreat" 
				:disabled="isFormDisabled"></el-input>
		</el-form-item>
		<el-form-item label="既往史">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.history" 
				:disabled="isFormDisabled"></el-input>
		</el-form-item>
		<el-form-item label="过敏史">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.allergy" 
				:disabled="isFormDisabled"></el-input>
		</el-form-item>
		<el-form-item label="体格检查">
			<el-input type="textarea" :rows="2" v-model="medicalRecord.physique" 
				:disabled="isFormDisabled"></el-input>
		</el-form-item>

		<el-form-item label="评估/诊断">
			<el-input type="textarea" :rows="3" :value="diagnosisSummary" readonly
				:disabled="isFormDisabled"></el-input>
			<el-button type="primary" @click="openAssessmentDialog" :disabled="isFormDisabled">评估/诊断</el-button>
		</el-form-item>

		<el-form-item label="检查建议">
			<el-input type=
			"textarea" :rows="2" v-model="medicalRecord.proposal" 
				:disabled="isFormDisabled"></el-input>
		</el-form-item>

		<el-form-item label="注意事项">
			<el-input type=
			"textarea" :rows="2" v-model="medicalRecord.careful" 
				:disabled="isFormDisabled"></el-input>
		</el-form-item>

		<el-form-item label="检查结果">
			<el-input type=
			"textarea" :rows="2" v-model="medicalRecord.checkResult" 
				:disabled="isFormDisabled"></el-input>
		</el-form-item>

		<el-form-item label="处理意见">
			<el-input type=
			"textarea" :rows="2" v-model="medicalRecord.handling" 
				:disabled="isFormDisabled"></el-input>
		</el-form-item>
		
	</el-form>

	<el-dialog title="选择诊断" v-model="diseaseSelectDialogVisible" width="50%">
		<el-table :data="diseases" @selection-change="handleSelectionChange" max-height="300px" ref="diseaseTable">
			<el-table-column type="selection" width="55"></el-table-column>
			<el-table-column property="diseaseCode" label="ICD编码"></el-table-column>
			<el-table-column property="diseaseName" label="名称"></el-table-column>
		</el-table>
		<div style="display: flex; margin-bottom: 10px; margin-top: 10px;">
			<el-input v-model="searchKeyword" placeholder="按疾病编码或名称搜索" clearable @clear="handleSearch" @keyup.enter="handleSearch" />
			<el-button type="primary" @click="handleSearch" style="margin-left: 10px;">搜索</el-button>
		</div>
		<div style="margin-top: 20px; text-align: right;">
			<el-pagination
				v-model:current-page="currentPage"
				v-model:page-size="pageSize"
				:page-sizes="[10, 20, 50, 100]"
				layout="total, sizes, prev, pager, next, jumper"
				:total="total"
				@size-change="handleSizeChange"
				@current-change="handleCurrentChange"
			/>
		</div>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="diseaseSelectDialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="confirmDiseaseSelection">确 定</el-button>
			</span>
		</template>
	</el-dialog>

	<el-dialog title="评估/诊断" v-model="assessmentDialogVisible" width="60%" @open="onAssessmentDialogOpen">
		<div class="diagnosis-section">
			<div class="diagnosis-category">
				<div class="diagnosis-header">
					<span>西医诊断</span>
					<div>
						<el-button size="mini" @click="addDiagnosis('west')" :disabled="tempChineseDiseases.length > 0">增加</el-button>
						<el-button size="mini" type="danger" @click="removeDiagnosis('west')" :disabled="!isTempWestActive">删除</el-button>
					</div>
				</div>
				<el-table :data="tempWesternDiseases" @selection-change="handleWestSelection" ref="westTable" max-height="200px">
					<el-table-column type="selection" width="55" :disabled="!isTempWestActive"></el-table-column>
					<el-table-column prop="disease.diseaseCode" label="ICD编码"></el-table-column>
					<el-table-column prop="disease.diseaseName" label="名称"></el-table-column>
					<el-table-column label="发病日期">
						<template #default="scope">
							<el-date-picker v-model="scope.row.getSiskDate" type="date" placeholder="选择日期" size="mini" style="width: 100%;"></el-date-picker>
						</template>
					</el-table-column>
				</el-table>
			</div>
			<div class="diagnosis-category">
				<div class="diagnosis-header">
					<span>中医诊断</span>
					<div>
						<el-button size="mini" @click="addDiagnosis('east')" :disabled="tempWesternDiseases.length > 0">增加</el-button>
						<el-button size="mini" type="danger" @click="removeDiagnosis('east')" :disabled="!isTempEastActive">删除</el-button>
					</div>
				</div>
				<el-table :data="tempChineseDiseases" @selection-change="handleEastSelection" ref="eastTable" max-height="200px">
					<el-table-column type="selection" width="55" :disabled="!isTempEastActive"></el-table-column>
					<el-table-column prop="disease.diseaseCode" label="ICD编码"></el-table-column>
					<el-table-column prop="disease.diseaseName" label="名称"></el-table-column>
					<el-table-column label="发病日期">
						<template #default="scope">
							<el-date-picker v-model="scope.row.getSiskDate" type="date" placeholder="选择日期" size="mini" style="width: 100%;"></el-date-picker>
						</template>
					</el-table-column>
				</el-table>
			</div>
		</div>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="assessmentDialogVisible = false">取 消</el-button>
				<el-button type="primary" @click="confirmAssessment">确 定</el-button>
			</span>
		</template>
	</el-dialog>
</el-main>
</template>

<script setup>
import { ref,onMounted, defineExpose, watch, computed, defineProps } from 'vue'
import { getReq,postReq } from '../../../utils/api'
import { ElMessageBox, ElMessage } from 'element-plus'

const props = defineProps({
    isDiagnosed: {
        type: Boolean,
        default: false
    }
});

const westernDiseases = ref([]);
const chineseDiseases = ref([]);
const tempWesternDiseases = ref([]);
const tempChineseDiseases = ref([]);

const selectedWest = ref([]);
const selectedEast = ref([]);
const isWestActive = ref(true);
const isEastActive = ref(true);
const isTempWestActive = ref(true);
const isTempEastActive = ref(true);

const assessmentDialogVisible = ref(false);
const diseaseSelectDialogVisible = ref(false);
const diseases = ref([]);
const selectedDiseases = ref([]);
const diseaseTable = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const searchKeyword = ref('');
const currentDiagnosisType = ref('west');
const westCategoryId = 176;
const eastCategoryId = 177;

const loading=ref(false)//是否在加载数据
const isSaved=ref(false)//是否保存
const medicalRecord=ref({})//病历首案
const currentRid = ref(null)//病历首案
const eastTable=ref(null)

const isFormDisabled = computed(() => {
  return isSaved.value || props.isDiagnosed;
});

onMounted(async () => {
	// No initial data loading needed here
});

async function loadDiseases(dicaType) {
	if (!dicaType) return;
	try {
		let url = `/disease/page?count=${pageSize.value}&pn=${currentPage.value}&dicaType=${dicaType}`;
		if (searchKeyword.value) {
			url += `&keyword=${searchKeyword.value}`;
		}
		const resp = await getReq(url);
		if (resp.data.result) {
			diseases.value = resp.data.data.records;
			total.value = resp.data.data.total;
		} else {
			diseases.value = [];
			total.value = 0;
		}
	} catch (e) {
		console.error(e)
		diseases.value = [];
		total.value = 0;
		ElMessage.error('加载疾病列表失败')
	}
}

async function handleTabClick(tab) {
	if (diseaseTable.value && diseaseTable.value[0]) {
		diseaseTable.value[0].clearSelection();
	}
	diseases.value = [];
	selectedDiseases.value = [];
	searchKeyword.value = '';
	currentPage.value = 1;
	await loadDiseases(currentDiagnosisType.value === 'west' ? westCategoryId : eastCategoryId);
}

function handleSelectionChange(val) {
	selectedDiseases.value = val;
}

function handleSizeChange(val) {
	pageSize.value = val;
	loadDiseases(currentDiagnosisType.value === 'west' ? westCategoryId : eastCategoryId);
}

function handleCurrentChange(val) {
	currentPage.value = val;
	loadDiseases(currentDiagnosisType.value === 'west' ? westCategoryId : eastCategoryId);
}

function handleSearch() {
	currentPage.value = 1;
	loadDiseases(currentDiagnosisType.value === 'west' ? westCategoryId : eastCategoryId);
}

function openAssessmentDialog() {
	if (!medicalRecord.value.registId) {
		ElMessage.error('请先选择一位患者');
		return;
	}
	assessmentDialogVisible.value = true;
}

function onAssessmentDialogOpen() {
	tempWesternDiseases.value = JSON.parse(JSON.stringify(westernDiseases.value));
	tempChineseDiseases.value = JSON.parse(JSON.stringify(chineseDiseases.value));
}

function confirmAssessment() {
	westernDiseases.value = tempWesternDiseases.value;
	chineseDiseases.value = tempChineseDiseases.value;
	assessmentDialogVisible.value = false;
}

async function addDiagnosis(type) {
	searchKeyword.value = '';
	currentDiagnosisType.value = type;
	currentPage.value = 1;
	diseases.value = [];
	selectedDiseases.value = [];

	const dicaType = type === 'west' ? westCategoryId : eastCategoryId;
	await loadDiseases(dicaType);

	diseaseSelectDialogVisible.value = true;
}

function removeDiagnosis(type) {
	if (type === 'west') {
		tempWesternDiseases.value = tempWesternDiseases.value.filter(d => !selectedWest.value.includes(d));
	} else {
		tempChineseDiseases.value = tempChineseDiseases.value.filter(d => !selectedEast.value.includes(d));
	}
}

function handleWestSelection(val) {
	selectedWest.value = val;
}

function handleEastSelection(val) {
	selectedEast.value = val;
}

function confirmDiseaseSelection() {
	const newDiagnoses = selectedDiseases.value.map(disease => {
		const diagnoseType = currentDiagnosisType.value === 'west' ? 1 : 2;
		return {
			registId: currentRid.value,
			diseaseID: disease.id,
			diagnoseType: diagnoseType,
			getSiskDate: new Date(),
			diagnoseCate: 1, // 1 for 初诊
			disease: disease 
		}
	});

	if (currentDiagnosisType.value === 'west') {
		tempWesternDiseases.value.push(...newDiagnoses);
	} else {
		tempChineseDiseases.value.push(...newDiagnoses);
	}
	diseaseSelectDialogVisible.value = false;
}

//获取病人病历首页信息信息
async function loadMedicalRecord(rid){
	// First, clear the state of the previous patient.
	isSaved.value = false; // Reset the save state for the new patient
	medicalRecord.value = { registId: rid };
	westernDiseases.value = [];
	chineseDiseases.value = [];

	currentRid.value = rid;
	loading.value=true;

	getReq('/neudoc/getMedicalRecord', { registId: rid }).then(response=>{
		if(response.data.result && response.data.data){
			medicalRecord.value = response.data.data;
			// The arrays are already empty, just populate them
			if (medicalRecord.value.medicalDiseases) {
				medicalRecord.value.medicalDiseases.forEach(d => {
					if (d.diagnoseType === 1) { // 1 for 西医
						westernDiseases.value.push(d);
					} else if (d.diagnoseType === 2) { // 2 for 中医
						chineseDiseases.value.push(d);
					}
				});
			}
		}
		// If there is no record, medicalRecord.value remains { registId: rid }
		// and the diagnosis lists remain empty, which is the correct state.
	}).catch(err=>{
		ElMessage.error('加载病历失败');
	})
	
}

function save(callback){
	if (!medicalRecord.value || !medicalRecord.value.registId) {
	    ElMessage.error('请先选择一位患者');
	    return;
	}
	medicalRecord.value.medicalDiseases = [...westernDiseases.value, ...chineseDiseases.value];
	postReq('/neudoc/save',medicalRecord.value).then(resp=>{
		if(resp.data.result){
			if (callback && typeof callback === 'function') {
				callback();
			} else {
			ElMessage.success('暂存成功')
			}
		}else{
			ElMessage.error(resp.data.errMsg || '操作失败')
		}
	}).catch(err => {
	    ElMessage.error('网络错误，操作失败')
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
		medicalRecord.value.caseState=2; //2-已提交
		save(() => {
			ElMessage.success('提交成功');
			isSaved.value = true;
		});
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
	westernDiseases.value = [];
	chineseDiseases.value = [];
}

const diagnosisSummary = computed(() => {
    const westNames = westernDiseases.value.map(d => d.disease.diseaseName).join(', ');
    const eastNames = chineseDiseases.value.map(d => d.disease.diseaseName).join(', ');
    let summary = '';
    if (westNames) summary += `西医诊断: ${westNames}`;
    if (eastNames) {
        if (summary) summary += '\n';
        summary += `中医诊断: ${eastNames}`;
    }
    return summary || '无';
});

watch([westernDiseases, chineseDiseases], () => {
    isWestActive.value = chineseDiseases.value.length === 0;
    isEastActive.value = westernDiseases.value.length === 0;
}, { deep: true });

watch([tempWesternDiseases, tempChineseDiseases], () => {
    isTempWestActive.value = tempChineseDiseases.value.length === 0;
    isTempEastActive.value = tempWesternDiseases.value.length === 0;
}, { deep: true });

defineExpose({
    loadMedicalRecord,
    clearForm
})

</script>

<style>
.diagnosis-section {
	border: 1px solid #eee;
	padding: 10px;
	margin-bottom: 20px;
}

.diagnosis-category {
	margin-top: 10px;
}

.diagnosis-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 10px;
}
</style>