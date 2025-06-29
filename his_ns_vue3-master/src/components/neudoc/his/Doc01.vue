<template>
<!-- 页面正文 -->
<el-main style="width:100%;background:#fff;height:800px;overflow-y: auto">
	<el-row :gutter="20">
		<!-- 左侧：病历表单 -->
		<el-col :span="16">
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
					<el-col :span="4">
						<el-button type="primary" size="small" @click="analyzeByAI" :disabled="isFormDisabled">AI分析</el-button>
					</el-col>
				</el-row>
				<el-row>
					<el-col :span="24" align="left" >
						<el-tag>病史内容：</el-tag>
					</el-col>
				</el-row>

				<el-form-item label="主诉">
					<el-input v-model="medicalRecord.readme" placeholder="患者陈述的主要症状、体征及其持续时间" :disabled="isDiagnosed"></el-input>
				</el-form-item>
				<el-form-item label="现病史">
					<el-input v-model="medicalRecord.present" placeholder="围绕主诉，详细描述病情发生、发展、演变、诊疗的过程" :disabled="isDiagnosed"></el-input>
				</el-form-item>
				<el-form-item label="现病治疗情况">
					<el-input v-model="medicalRecord.presentTreat" placeholder="患者在来本院就诊前，针对现病做过的治疗" :disabled="isDiagnosed"></el-input>
				</el-form-item>
				<el-form-item label="既往史">
					<el-input v-model="medicalRecord.history" placeholder="患者既往的健康状况和疾病历史" :disabled="isDiagnosed"></el-input>
				</el-form-item>
				<el-form-item label="过敏史">
					<el-input v-model="medicalRecord.allergy" placeholder="患者的过敏史" :disabled="isDiagnosed"></el-input>
				</el-form-item>
				<el-form-item label="体格检查">
					<el-input v-model="medicalRecord.physique" placeholder="医生的体格检查结果" :disabled="isDiagnosed"></el-input>
				</el-form-item>

				<el-form-item label="评估/诊断">
					<el-input type="textarea" :rows="3" :value="diagnosisSummary" readonly
						:disabled="isFormDisabled"></el-input>
					<el-button type="primary" @click="openAssessmentDialog" :disabled="isFormDisabled">评估/诊断</el-button>
				</el-form-item>

				<el-form-item label="检查建议">
					<el-input v-model="medicalRecord.proposal" placeholder="医生的检查建议" :disabled="isDiagnosed"></el-input>
				</el-form-item>

				<el-form-item label="注意事项">
					<el-input v-model="medicalRecord.careful" placeholder="医生的注意事项" :disabled="isDiagnosed"></el-input>
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
		</el-col>
		<!-- 右侧：AI分析结果 -->
		<el-col :span="8">
			<el-card class="ai-card-beauty">
				<template #header>
					<div class="ai-title-bar">
						<i class="el-icon-s-opportunity ai-title-icon"></i>
						<span class="ai-title-text">AI分析结果</span>
						<el-tag v-if="aiLoading" type="info" size="mini" style="margin-left:10px;">分析中...</el-tag>
					</div>
				</template>
				<el-empty v-if="!aiContent && !aiLoading" description="暂无AI分析结果" />
				<el-scrollbar v-else class="ai-scrollbar-beauty" ref="aiScrollbarRef">
					<transition name="ai-fade-expand" mode="out-in">
						<div v-if="aiParsedContent.analysis || aiParsedContent.comment" key="ai-content" class="ai-typewriter-content-beauty" ref="aiContentRef">
							<div v-if="aiParsedContent.analysis" class="ai-section-block">
								<span class="ai-section-title">病历分析结果：</span>
								<span class="ai-section-text">{{ aiParsedContent.analysis }}</span>
							</div>
							<div v-if="aiParsedContent.comment" class="ai-section-block ai-section-comment">
								<span class="ai-section-title ai-section-title-comment">评价：</span>
								<span class="ai-section-text ai-section-text-comment">{{ aiParsedContent.comment }}</span>
							</div>
						</div>
					</transition>
				</el-scrollbar>
			</el-card>
		</el-col>
	</el-row>
</el-main>
</template>

<script setup>
import {
	ref,
	onMounted,
	watch,
	computed,
	nextTick
} from 'vue'
import { get,postReq } from '../../../utils/api.js'
import {
	ElMessage,
	ElMessageBox
} from 'element-plus'
import { useUserStore } from '../../../store/user.js'
import { EventSourcePolyfill } from 'event-source-polyfill'

const props = defineProps({
    patient: {
        type: Object,
        default: null
    },
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

const aiContent = ref(''); // 整体AI内容
const aiLoading = ref(false);
let eventSource = null;
let typewriterTimer = null;
let contentBuffer = '';
const aiScrollbarRef = ref(null);
const aiContentRef = ref(null);

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
		const resp = await get(url);
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
	if (!props.patient) {
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
const loadMedicalRecord = async (rid) => {
    if (!rid) {
        console.error("加载病历的rid无效");
        return;
    }
    try {
        const response = await get('/neudoc/medicalrecord/getByRegistId', { registId: rid });
            if (response.data.data) {
                Object.assign(medicalRecord.value, response.data.data);

			if (medicalRecord.value.medicalDiseases) {
                    const west = medicalRecord.value.medicalDiseases.filter(d => d.diagnoseType === 1);
                    const east = medicalRecord.value.medicalDiseases.filter(d => d.diagnoseType === 2);
                // 后端已经返回了正确的嵌套结构，直接赋值即可
                westernDiseases.value = west;
                chineseDiseases.value = east;
                }
            } else {
                // 如果没有病历记录，为新诊创建一个
                const newRecord = { registId: props.patient.id, caseNumber: props.patient.caseNumber };
                clearForm(); // 先清空旧数据
                Object.assign(medicalRecord.value, newRecord); // 再合并新数据
        }
    } catch (error) {
        console.error('加载病历失败:', error);
		ElMessage.error('加载病历失败');
}
};

const save = (callback) => {
	return new Promise((resolve, reject) => {
	if (!props.patient) {
	    ElMessage.error('请先选择一位患者');
			return reject(new Error('No patient selected'));
	}
		isSaved.value = false;
	medicalRecord.value.medicalDiseases = [...westernDiseases.value, ...chineseDiseases.value];
		postReq('/neudoc/medicalrecord/save', medicalRecord.value).then(resp => {
			if (resp.data.result) {
			if (callback && typeof callback === 'function') {
				callback();
				} else {
					ElMessage.success('暂存成功');
				}
				// 使用 Object.assign 更新，而不是替换整个对象
				Object.assign(medicalRecord.value, resp.data.data);
				resolve(true); // Promise 成功
			} else {
				ElMessage.error(resp.data.errMsg || '操作失败');
				reject(new Error(resp.data.errMsg || '操作失败')); // Promise 失败
		}
	}).catch(err => {
			ElMessage.error('网络错误，操作失败');
			reject(err); // Promise 失败
		});
	});
}
const submit=()=>{
	if (!props.patient) {
        ElMessage.error('请先选择一位患者');
        return;
    }
	save(()=>{
		isSaved.value=true
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
	medicalRecord.value = {}
	westernDiseases.value = []
	chineseDiseases.value = []
	isSaved.value = false
	aiContent.value = ''
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

watch(() => props.patient, (newPatient, oldPatient) => {
    if (newPatient && newPatient.id) {
        if (!oldPatient || newPatient.id !== oldPatient.id) {
            loadMedicalRecord(newPatient.id);
        }
    } else {
        clearForm();
    }
}, { deep: true, immediate: true });

watch([westernDiseases, chineseDiseases], () => {
    isWestActive.value = chineseDiseases.value.length === 0;
    isEastActive.value = westernDiseases.value.length === 0;
}, { deep: true });

watch([tempWesternDiseases, tempChineseDiseases], () => {
    isTempWestActive.value = tempChineseDiseases.value.length === 0;
    isTempEastActive.value = tempWesternDiseases.value.length === 0;
}, { deep: true });

function resetAIResult() {
  aiContent.value = '';
  if (typewriterTimer) clearInterval(typewriterTimer);
  if (eventSource) {
    eventSource.close();
    eventSource = null;
  }
  contentBuffer = '';
}

// 过滤AI内容中的**包裹内容和多余星号
function filterAIContent(raw) {
  // 移除**包裹的内容和所有**
  return raw.replace(/\*\*[^*]+\*\*/g, '').replace(/\*\*/g, '').trim();
}

// 打字机流式输出（带过滤）
function streamTypewriter(fullText) {
  if (typewriterTimer) clearInterval(typewriterTimer);
  let i = 0;
  const prev = aiContent.value;
  // 过滤掉**内容
  const filteredText = filterAIContent(fullText);
  typewriterTimer = setInterval(() => {
    aiContent.value = prev + filteredText.slice(prev.length, prev.length + i + 1);
    i++;
    if (prev.length + i >= filteredText.length) {
      clearInterval(typewriterTimer);
    }
  }, 18);
}

// 自动滚动到底部（双保险：wrapRef和scrollIntoView）
watch(aiContent, async () => {
  await nextTick();
  // 方式1：Element Plus官方推荐
  if (aiScrollbarRef.value && aiScrollbarRef.value.wrapRef) {
    aiScrollbarRef.value.wrapRef.scrollTop = aiScrollbarRef.value.wrapRef.scrollHeight;
  }
  // 方式2：DOM scrollIntoView
  if (aiContentRef.value && aiContentRef.value.scrollIntoView) {
    aiContentRef.value.scrollIntoView({ behavior: 'smooth', block: 'end' });
  }
});

async function analyzeByAI() {
  if (!props.patient) {
    ElMessage.error('请先选择一位患者');
    return;
  }
  resetAIResult();
  aiLoading.value = true;
  const message = encodeURIComponent(JSON.stringify(medicalRecord.value));
  const userStore = useUserStore();
  const token = userStore.userInfo.token || localStorage.getItem('token');
  // 使用GET方式拼接参数
  eventSource = new EventSourcePolyfill(`/ai/stream?message=${message}`, {
    headers: { token }
  });
  eventSource.onmessage = function(event) {
    let raw = event.data.trim();
    if (raw.startsWith('data:')) {
      raw = raw.substring(5).trim();
    }
    if(raw === "" || raw.startsWith(':')){
      return;
    }
    try {
      const data = JSON.parse(raw);
      //  解析阿里云百炼服务返回的JSON结构
      if (data.output && data.output.choices && data.output.choices.length > 0) {
        const message = data.output.choices[0].message;
        if (message && message.content) {
          // 百炼API每次返回的是完整的句子，而不是增量，所以直接赋值
          contentBuffer = message.content;
          streamTypewriter(contentBuffer);
        }
      }
    } catch (e) {
      console.warn('无法解析SSE中的JSON片段:', event.data, e);
    }
  };
  eventSource.onerror = function() {
    aiLoading.value = false;
    if (eventSource) eventSource.close();
  };
}

// 主题分明的分块渲染AI内容
function parseAIContent(raw) {
  // 过滤**内容
  const filtered = filterAIContent(raw);
  // 按"评价："分块
  const result = { analysis: '', comment: '' };
  const match = filtered.match(/([\s\S]*?)(评价[：:][\s\S]*)/);
  if (match) {
    result.analysis = match[1].trim();
    result.comment = match[2].replace(/^评价[：:]/, '').trim();
  } else {
    result.analysis = filtered;
  }
  return result;
}

const aiParsedContent = computed(() => {
  return parseAIContent(aiContent.value);
});

defineExpose({
    loadMedicalRecord,
    clearForm,
    save
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

.ai-card-beauty {
  height: 780px;
  overflow: hidden;
  border-radius: 22px;
  box-shadow: 0 6px 32px 0 rgba(80,120,255,0.13), 0 2px 8px 0 rgba(80,120,255,0.07);
  background: linear-gradient(135deg, #fafdff 60%, #e6f0ff 100%);
  border: none;
  transition: box-shadow 0.3s;
}
.ai-card-beauty:hover {
  box-shadow: 0 10px 40px 0 rgba(80,120,255,0.18), 0 4px 16px 0 rgba(80,120,255,0.10);
}
.ai-title-bar {
  display: flex;
  align-items: center;
  background: linear-gradient(90deg, #e0eaff 0%, #fafdff 100%);
  border-radius: 16px 16px 0 0;
  padding: 14px 24px 14px 16px;
  font-weight: bold;
  font-size: 22px;
  color: #2563eb;
  letter-spacing: 1.2px;
  box-shadow: 0 2px 8px 0 rgba(80,120,255,0.04);
}
.ai-title-icon {
  font-size: 26px;
  color: #4b8cff;
  margin-right: 10px;
}
.ai-title-text {
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}
.ai-scrollbar-beauty {
  max-height: 700px;
  min-height: 220px;
  overflow: auto;
  background: #f6faff;
  border-radius: 16px;
  padding: 22px 26px 22px 26px;
  transition: max-height 0.4s cubic-bezier(.4,0,.2,1);
}
.ai-typewriter-content-beauty {
  font-size: 20px;
  color: #1a237e;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  background: #f4f8ff;
  border-radius: 12px;
  padding: 22px 28px;
  line-height: 2.2;
  margin-top: 6px;
  word-break: break-all;
  box-shadow: 0 2px 8px rgba(75,140,255,0.08);
  min-height: 80px;
  transition: background 0.2s, min-height 0.4s cubic-bezier(.4,0,.2,1);
  text-indent: 2em;
  letter-spacing: 0.7px;
}
/* 平滑展开动画 */
.ai-fade-expand-enter-active, .ai-fade-expand-leave-active {
  transition: all 0.4s cubic-bezier(.4,0,.2,1);
}
.ai-fade-expand-enter-from, .ai-fade-expand-leave-to {
  opacity: 0;
  transform: scaleY(0.98);
}
/* 美化滚动条 */
.ai-scrollbar-beauty ::-webkit-scrollbar {
  width: 8px;
  background: #eaf2ff;
  border-radius: 8px;
}
.ai-scrollbar-beauty ::-webkit-scrollbar-thumb {
  background: #c6d8ff;
  border-radius: 8px;
}
.ai-section-block {
  margin-bottom: 18px;
  padding: 0 0 0 0;
}
.ai-section-title {
  display: inline-block;
  font-weight: bold;
  color: #2563eb;
  font-size: 18px;
  margin-bottom: 6px;
}
.ai-section-text {
  display: inline;
  color: #1a237e;
  font-size: 18px;
  margin-left: 8px;
}
.ai-section-comment {
  background: linear-gradient(90deg, #e3f0ff 0%, #fafdff 100%);
  border-radius: 10px;
  box-shadow: 0 2px 8px 0 rgba(80,120,255,0.08);
  padding: 16px 18px;
  margin-top: 10px;
  margin-bottom: 0;
  border-left: 5px solid #2563eb;
}
.ai-section-title-comment {
  color: #e65100;
  font-size: 19px;
}
.ai-section-text-comment {
  color: #e65100;
  font-size: 18px;
  font-weight: 500;
  margin-left: 8px;
}
</style>