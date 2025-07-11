<template>
<!-- 页面正文 -->
<el-main class="medical-record-main">
	<el-row :gutter="40">
		<!-- 左侧：病历表单 -->
		<el-col :span="16">
			<div class="medical-form-container">
				<div class="action-bar">
					<div class="action-group">
						<el-button :icon="Tickets" type="default" plain @click="save" :disabled="isFormDisabled">
							暂存
						</el-button>
						<el-button type="success" :icon="CircleCheck" @click="submit" :disabled="isFormDisabled">
							提交
						</el-button>
						<el-button type="danger" plain :icon="Delete" @click="clearAll" :disabled="isFormDisabled">
							清空
						</el-button>
						<el-button :icon="Refresh" @click="refresh">
							刷新
						</el-button>
						<el-button type="primary" class="ai-btn-highlight" @click="analyzeByAI" :disabled="isFormDisabled">
							AI分析 <el-icon class="el-icon--right"><MagicStick /></el-icon>
						</el-button>
					</div>
				</div>

				<el-form ref="form" label-width="100px" label-position="left" class="medical-form">
					<div class="form-section">
						<div class="section-header">
							<el-icon><Document /></el-icon>
							<span>病史内容</span>
						</div>

						<el-form-item label="主诉">
							<el-input v-model="medicalRecord.readme" 
								:placeholder="isDiagnosed ? '暂无内容' : '患者陈述的主要症状、体征及其持续时间'" 
								:disabled="isDiagnosed"
								class="custom-input"></el-input>
						</el-form-item>
						<el-form-item label="现病史">
							<el-input type="textarea" v-model="medicalRecord.present" 
								:placeholder="isDiagnosed ? '暂无内容' : '围绕主诉，详细描述病情发生、发展、演变、诊疗的过程'"
								:disabled="isDiagnosed"
								:rows="3"
								class="custom-textarea"></el-input>
						</el-form-item>
						<el-form-item label="现病治疗情况">
							<el-input type="textarea" v-model="medicalRecord.presentTreat" 
								:placeholder="isDiagnosed ? '暂无内容' : '患者在来本院就诊前，针对现病做过的治疗'"
								:disabled="isDiagnosed"
								:rows="2"
								class="custom-textarea"></el-input>
						</el-form-item>
						<el-form-item label="既往史">
							<el-input v-model="medicalRecord.history" 
								:placeholder="isDiagnosed ? '暂无内容' : '患者既往的健康状况和疾病历史'"
								:disabled="isDiagnosed"
								class="custom-input"></el-input>
						</el-form-item>
						<el-form-item label="过敏史">
							<el-input v-model="medicalRecord.allergy" 
								:placeholder="isDiagnosed ? '暂无内容' : '患者的过敏史'"
								:disabled="isDiagnosed"
								class="custom-input"></el-input>
						</el-form-item>
						<el-form-item label="体格检查">
							<el-input type="textarea" v-model="medicalRecord.physique" 
								:placeholder="isDiagnosed ? '暂无内容' : '医生的体格检查结果'"
								:disabled="isDiagnosed"
								:rows="2"
								class="custom-textarea"></el-input>
						</el-form-item>
					</div>

					<div class="form-section">
						<div class="section-header">
							<el-icon><List /></el-icon>
							<span>诊断信息</span>
						</div>

						<el-form-item label="评估/诊断">
							<div class="diagnosis-wrapper">
								<el-input type="textarea" :rows="3" :value="diagnosisSummary" readonly
									:disabled="isFormDisabled" 
									:placeholder="isDiagnosed ? '暂无内容' : '点击右侧按钮进行诊断'"
									class="custom-textarea"></el-input>
								<el-button type="primary" @click="openAssessmentDialog" 
									:disabled="isFormDisabled" 
									class="diagnosis-btn">
									评估/诊断
								</el-button>
							</div>
						</el-form-item>

						<el-form-item label="检查建议">
							<el-input type="textarea" v-model="medicalRecord.proposal" 
								:placeholder="isDiagnosed ? '暂无内容' : '医生的检查建议'"
								:disabled="isDiagnosed"
								:rows="2"
								class="custom-textarea"></el-input>
						</el-form-item>

						<el-form-item label="注意事项">
							<el-input type="textarea" v-model="medicalRecord.careful" 
								:placeholder="isDiagnosed ? '暂无内容' : '医生的注意事项'"
								:disabled="isDiagnosed"
								:rows="2"
								class="custom-textarea"></el-input>
						</el-form-item>
					</div>

					<div class="form-section">
						<div class="section-header">
							<el-icon><DocumentChecked /></el-icon>
							<span>检查结果</span>
						</div>

						<el-form-item label="检查结果">
							<el-input type="textarea" :rows="3" v-model="medicalRecord.checkResult" 
								:disabled="isFormDisabled"
								:placeholder="isDiagnosed ? '暂无内容' : '请输入检查结果'"
								class="custom-textarea"></el-input>
						</el-form-item>

						<el-form-item label="处理意见">
							<el-input type="textarea" :rows="3" v-model="medicalRecord.handling" 
								:disabled="isFormDisabled"
								:placeholder="isDiagnosed ? '暂无内容' : '请输入处理意见'"
								class="custom-textarea"></el-input>
						</el-form-item>
					</div>
				</el-form>
			</div>

			<!-- Dialogs -->
			<el-dialog title="选择诊断" v-model="diseaseSelectDialogVisible" width="50%" class="custom-dialog">
				<div class="dialog-search">
					<el-input v-model="searchKeyword" placeholder="按疾病编码或名称搜索" 
						clearable @clear="handleSearch" 
						@keyup.enter="handleSearch">
						<template #append>
							<el-button :icon="Search" @click="handleSearch"></el-button>
						</template>
					</el-input>
				</div>

				<el-table :data="diseases" 
					@selection-change="handleSelectionChange" 
					max-height="300px" 
					ref="diseaseTable"
					class="custom-table">
					<el-table-column type="selection" width="55"></el-table-column>
					<el-table-column property="diseaseCode" label="ICD编码"></el-table-column>
					<el-table-column property="diseaseName" label="名称"></el-table-column>
				</el-table>

				<div class="dialog-pagination">
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

			<el-dialog title="评估/诊断" 
				v-model="assessmentDialogVisible" 
				width="60%" 
				@open="onAssessmentDialogOpen"
				class="custom-dialog">
				<div class="diagnosis-section">
					<div class="diagnosis-category">
						<div class="diagnosis-header">
							<div class="header-title">
								<el-icon><FirstAidKit /></el-icon>
								<span>西医诊断</span>
							</div>
							<div class="header-actions">
								<el-button type="primary" plain @click="addDiagnosis('west')" 
									:disabled="tempChineseDiseases.length > 0">
									增加
								</el-button>
								<el-button type="danger" plain @click="removeDiagnosis('west')" 
									:disabled="!isTempWestActive">
									删除
								</el-button>
							</div>
						</div>
						<el-table :data="tempWesternDiseases" 
							@selection-change="handleWestSelection" 
							ref="westTable" 
							max-height="200px"
							class="custom-table">
							<el-table-column type="selection" width="55" :disabled="!isTempWestActive"></el-table-column>
							<el-table-column prop="disease.diseaseCode" label="ICD编码"></el-table-column>
							<el-table-column prop="disease.diseaseName" label="名称"></el-table-column>
							<el-table-column label="发病日期" width="180">
								<template #default="scope">
									<el-date-picker v-model="scope.row.getSiskDate" 
										type="date" 
										placeholder="选择日期"
										style="width: 100%;"
										class="custom-date-picker"></el-date-picker>
								</template>
							</el-table-column>
						</el-table>
					</div>

					<div class="diagnosis-category">
						<div class="diagnosis-header">
							<div class="header-title">
								<el-icon><Coin /></el-icon>
								<span>中医诊断</span>
							</div>
							<div class="header-actions">
								<el-button type="primary" plain @click="addDiagnosis('east')" 
									:disabled="tempWesternDiseases.length > 0">
									增加
								</el-button>
								<el-button type="danger" plain @click="removeDiagnosis('east')" 
									:disabled="!isTempEastActive">
									删除
								</el-button>
							</div>
						</div>
						<el-table :data="tempChineseDiseases" 
							@selection-change="handleEastSelection" 
							ref="eastTable" 
							max-height="200px"
							class="custom-table">
							<el-table-column type="selection" width="55" :disabled="!isTempEastActive"></el-table-column>
							<el-table-column prop="disease.diseaseCode" label="ICD编码"></el-table-column>
							<el-table-column prop="disease.diseaseName" label="名称"></el-table-column>
							<el-table-column label="发病日期" width="180">
								<template #default="scope">
									<el-date-picker v-model="scope.row.getSiskDate" 
										type="date" 
										placeholder="选择日期"
										style="width: 100%;"
										class="custom-date-picker"></el-date-picker>
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
						<el-icon class="ai-title-icon"><MagicStick /></el-icon>
						<span class="ai-title-text">AI分析结果</span>
						<el-tag v-if="aiLoading" type="info" effect="dark" class="ai-status-tag">分析中...</el-tag>
					</div>
				</template>
				<el-empty v-if="!aiContent && !aiLoading" description="暂无AI分析结果" />
				<div v-else class="ai-content-wrapper">
					<transition name="ai-fade-expand" mode="out-in">
						<div v-if="aiParsedContent.analysis || aiParsedContent.comment" 
							key="ai-content" 
							class="ai-typewriter-content-beauty" 
							ref="aiContentRef">
							<div v-if="aiParsedContent.analysis" class="ai-section-block">
								<div class="ai-section-title">
									<el-icon><DataAnalysis /></el-icon>
									<span>病历分析结果：</span>
								</div>
								<div class="ai-section-text">{{ aiParsedContent.analysis }}</div>
							</div>
							<div v-if="aiParsedContent.comment" class="ai-section-block ai-section-comment">
								<div class="ai-section-title ai-section-title-comment">
									<el-icon><ChatLineRound /></el-icon>
									<span>评价：</span>
								</div>
								<div class="ai-section-text ai-section-text-comment">{{ aiParsedContent.comment }}</div>
							</div>
						</div>
					</transition>
				</div>
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
import { 
	Tickets, 
	CircleCheck, 
	Delete, 
	Refresh, 
	MagicStick,
	Document,
	DocumentChecked,
	FirstAidKit,
	Coin,
	Search,
	DataAnalysis,
	ChatLineRound,
	List
} from '@element-plus/icons-vue'

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
/* 主容器样式 */
.medical-record-main {
	width: 100%;
	background: #f8fafc;
	min-height: 100vh;
	padding: 24px;
	overflow-x: hidden;
	max-width: 1600px;
	margin: 0 auto;
}

/* 医疗表单容器 */
.medical-form-container {
	background: #fff;
	border-radius: 16px;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
	padding: 24px;
	transition: all 0.3s ease;
	height: 97%;
	width: 100%;
}

/* 操作栏样式 */
.action-bar {
	display: flex;
	flex-direction: row;
	align-items: center;
	padding: 0 0 24px 0;
	margin-bottom: 24px;
	border-bottom: 1px solid #e9ecef;
}

.action-group {
	display: flex;
	flex-direction: row;
	flex-wrap: nowrap;
	gap: 6px;
	align-items: center;
}

.action-group .el-button {
	min-width: 72px;
	height: 34px;
	font-size: 14px;
	border-radius: 7px;
	font-weight: 500;
	letter-spacing: 0.1px;
	padding: 0 14px;
	transition: all 0.2s;
}

.action-group .el-button[type="success"] {
	font-weight: 600;
}



.ai-btn-highlight {
	font-weight: 600;
	box-shadow: 0 2px 8px rgba(64,158,255,0.10);
	border-radius: 7px;
	letter-spacing: 0.3px;
}

.ai-btn-highlight:active {
	box-shadow: 0 1px 4px rgba(64,158,255,0.10);
}

@media (max-width: 1400px) {
	.action-group .el-button {
		min-width: 60px;
		font-size: 13px;
		padding: 0 10px;
	}
}

/* 表单区域样式 */
.medical-form {
	padding: 0 24px;
}

.form-section {
	margin-bottom: 20px;
	background: #fff;
	border-radius: 12px;
	padding: 16px;
	box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
	transition: all 0.3s ease;
}

.section-header {
	display: flex;
	align-items: center;
	margin-bottom: 16px;
	padding-bottom: 8px;
	border-bottom: 2px solid #e9ecef;
	color: #2c3e50;
	font-size: 16px;
	font-weight: 600;
}

.section-header .el-icon {
	margin-right: 8px;
	font-size: 18px;
	color: #409EFF;
}

/* 输入框样式 */
.custom-input.el-input .el-input__wrapper {
	box-shadow: 0 0 0 1px #e9ecef inset;
	padding: 8px 12px;
	border-radius: 6px;
	transition: all 0.3s ease;
}

.custom-textarea.el-textarea .el-textarea__inner {
	padding: 8px 12px;
	border-color: #e9ecef;
	border-radius: 6px;
	transition: all 0.3s ease;
	line-height: 1.5;
	min-height: 60px !important;
}

:deep(.el-form-item) {
	margin-bottom: 16px;
}

:deep(.el-form-item__label) {
	font-size: 16px;
	font-weight: 600;
	color: #2c3e50;
	padding-right: 12px;
}

.custom-input.el-input .el-input__wrapper,
.custom-textarea.el-textarea .el-textarea__inner {
  font-size: 15px;
  line-height: 1.7;
}
.custom-textarea.el-textarea .el-textarea__inner::placeholder,
.custom-input.el-input .el-input__wrapper input::placeholder {
  font-size: 15px;
  color: #b0b3b8;
}

/* 诊断按钮样式 */
.diagnosis-wrapper {
  position: relative;
  width: 100%;
  display: flex;
  align-items: flex-start;
}
.diagnosis-wrapper .custom-textarea {
  flex: 1;
}
.diagnosis-btn {
  margin-left: 8px;
  height: 100%;
  min-width: 100px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 10px;
  box-shadow: none;
  background: #f4f8ff;
  color: #409EFF;
  border: 1px solid #b3d8ff;
  transition: background 0.2s, color 0.2s, border 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}
.diagnosis-btn:hover {
  background: #e6f0ff;
  color: #337ecc;
  border-color: #409EFF;
}

/* 表格样式 */
.custom-table {
	border-radius: 8px;
}

.custom-table th {
	padding: 8px 0;
	font-size: 14px;
}

.custom-table td {
	padding: 6px 0;
	font-size: 14px;
}

/* AI分析卡片样式优化 */
.ai-card-beauty {
	min-height: calc(150vh);
	border-radius: 16px;
	background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
	border: none;
	box-shadow: 0 4px 24px rgba(31, 45, 61, 0.08);
	transition: all 0.3s ease;
	position: sticky;
	margin-left: 20px;
	display: flex;
	flex-direction: column;
}

.ai-content-wrapper {
	flex: 1;
	padding: 24px;
	overflow: hidden;
	display: flex;
	flex-direction: column;
	gap: 20px;
}

.ai-typewriter-content-beauty {
	display: flex;
	flex-direction: column;
	gap: 20px;
}

.ai-section-block {
	padding: 16px;
	margin-bottom: 16px;
}

.ai-section-title {
	margin-bottom: 8px;
}

.ai-section-text {
	font-size: 20px;
	line-height: 1.8;
}

.ai-section-comment {
	background: #fff9f0;
	border-left: 4px solid #e6a23c;
}

.ai-section-title-comment {
	color: #b88230;
}

.ai-section-text-comment {
	color: #8c6428;
	font-size: 18px;
	line-height: 1.8;
}

/* 动画效果 */
.ai-fade-expand-enter-active,
.ai-fade-expand-leave-active {
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.ai-fade-expand-enter-from,
.ai-fade-expand-leave-to {
	opacity: 0;
	transform: translateY(-8px);
}

/* 日期选择器美化 */
.custom-date-picker {
	width: 100%;
}

.custom-date-picker .el-input__wrapper {
	border-radius: 8px;
	transition: all 0.3s ease;
}

.custom-date-picker .el-input__wrapper:hover {
	box-shadow: 0 0 0 1px #409EFF inset;
}

/* Element Plus 组件全局样式覆盖 */
:deep(.el-button) {
	border-radius: 8px;
	font-weight: 500;
	letter-spacing: 0.3px;
}

:deep(.el-dialog) {
	border-radius: 16px;
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.el-dialog__header) {
	padding: 20px 24px;
	margin: 0;
	border-bottom: 1px solid #e9ecef;
}

:deep(.el-dialog__body) {
	padding: 16px;
}

:deep(.el-dialog__footer) {
	padding: 12px 16px;
	border-top: 1px solid #e9ecef;
}

:deep(.el-form-item__label) {
	font-weight: 500;
	color: #2c3e50;
}

:deep(.el-table) {
	--el-table-border-color: #e9ecef;
	--el-table-border: 1px solid #e9ecef;
}

:deep(.el-table th) {
	font-weight: 600;
	background: #f8fafc !important;
}

:deep(.el-pagination) {
	--el-pagination-button-bg-color: #fff;
	--el-pagination-hover-color: #409EFF;
}

:deep(.el-tag) {
	border-radius: 6px;
}

/* 诊断对话框样式 */
.diagnosis-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.header-title {
  display: flex;
  align-items: center;
  font-size: 15px;
  font-weight: 600;
  color: #409EFF;
  gap: 6px;
}
.header-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-bottom: 0;
}
.header-actions .el-button {
  font-size: 13px;
  border-radius: 8px;
  padding: 0 14px;
  height: 28px;
  min-width: 56px;
  font-weight: 500;
  box-shadow: none;
}
.header-actions .el-button--primary {
  background: #f4f8ff;
  color: #409EFF;
  border: 1px solid #b3d8ff;
}
.header-actions .el-button--primary:hover {
  background: #e6f0ff;
  color: #337ecc;
  border-color: #409EFF;
}
.header-actions .el-button--danger {
  background: #fff6f6;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}
.header-actions .el-button--danger:hover {
  background: #ffeaea;
  color: #c45656;
  border-color: #f56c6c;
}
.diagnosis-category {
  margin-bottom: 24px;
}

</style>