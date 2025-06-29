<template>
  <el-container direction="vertical" style="height: 100%; padding: 10px;">
    <!-- Header -->
    <el-row :gutter="10" style="margin-bottom: 10px;">
      <el-col :span="24">
        <el-card shadow="never">
          <el-row justify="space-between" align="middle">
            <el-col :span="12">
              <el-tag>当前诊断:</el-tag>
              <el-tag type="info" style="margin-left: 10px;">{{ patientDiagnosis || '暂无' }}</el-tag>
            </el-col>
            <el-col :span="12" style="text-align: right;">
              <el-button-group>
                <el-button type="primary" :icon="CirclePlus" @click="openAddPrescriptionDialog" :disabled="isDiagnosed">增方</el-button>
                <el-button type="danger" :icon="Delete" @click="deletePrescription" :disabled="isDiagnosed">删方</el-button>
                <el-button type="success" :icon="SuccessFilled" @click="issuePrescription" :disabled="isDiagnosed">开立</el-button>
                <el-button type="warning" :icon="DeleteFilled" @click="cancelPrescription" :disabled="isDiagnosed">作废</el-button>
                <el-button :icon="Refresh" @click="refreshAll" :disabled="isDiagnosed">刷新</el-button>
              </el-button-group>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- Main Content -->
    <el-row :gutter="10" style="height:calc(100% - 120px);">
      <!-- Left: Prescription List -->
      <el-col :span="6">
        <el-card shadow="never" style="height: 100%; display: flex; flex-direction: column;">
          <template #header><span>处方列表</span></template>
          <el-table :data="prescriptions" highlight-current-row @row-click="handlePrescriptionSelect" height="100%" style="flex: 1;" :row-style="{ cursor: isDiagnosed ? 'default' : 'pointer' }">
            <el-table-column prop="prescriptionName" label="名称" />
            <el-table-column prop="prescriptionState" label="状态" width="70">
              <template #default="scope">{{ formatState(scope.row.prescriptionState) }}</template>
            </el-table-column>
            <template #empty><el-empty description="暂无处方" /></template>
          </el-table>
        </el-card>
      </el-col>

      <!-- Right: Prescription Details -->
      <el-col :span="18">
        <el-card shadow="never" style="height: 100%; display: flex; flex-direction: column;">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>{{ selectedPrescription ? selectedPrescription.prescriptionName : '处方明细' }}</span>
              <el-button-group>
                <el-button type="primary" :icon="CirclePlus" size="small" @click="openAddDrugDialog" :disabled="!selectedPrescription || isDiagnosed">增药</el-button>
                <el-button type="danger" :icon="Delete" size="small" @click="deleteDrugs" :disabled="selectedDetails.length === 0 || isDiagnosed">删药</el-button>
                <el-button type="info" size="small" @click="analyzePrescription" :disabled="!selectedPrescription || prescriptionDetails.length === 0">分析药方</el-button>
              </el-button-group>
            </div>
          </template>
          <el-table :data="prescriptionDetails" @selection-change="handleDetailSelectionChange" height="100%" style="flex: 1;" :row-style="{ cursor: isDiagnosed ? 'default' : 'pointer' }">
            <el-table-column type="selection" width="55" :selectable="() => !isDiagnosed"/>
            <el-table-column prop="drugsName" label="药品名称" />
            <el-table-column prop="drugsFormat" label="规格" width="120" />
            <el-table-column prop="drugsPrice" label="单价" width="80" />
            <el-table-column prop="drugsUsage" label="用法" width="100" />
            <el-table-column prop="dosage" label="用量" width="80" />
            <el-table-column prop="frequency" label="频次" width="80" />
            <el-table-column prop="amount" label="数量" width="80" />
            <template #empty><el-empty description="请先在左侧选择处方" /></template>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- Footer: Templates -->
    <el-row :gutter="10" style="margin-top: 10px;">
      <el-col :span="24">
        <el-tabs type="border-card">
          <el-tab-pane label="处方模板">
            <el-container>
              <el-aside width="400px" style="padding-right: 10px;">
                <el-input v-model="templateKw" placeholder="搜索模板" @input="loadTemplates" clearable/>
                <el-table :data="templates" highlight-current-row @row-click="handleTemplateSelect" size="small" style="margin-top: 10px;" :row-style="{ cursor: isDiagnosed ? 'default' : 'pointer' }">
                  <el-table-column prop="name" label="模板名称" />
                  <el-table-column prop="scope" label="范围" width="80">
                    <template #default="scope">{{ formatScope(scope.row.scope) }}</template>
                  </el-table-column>
                </el-table>
              </el-aside>
              <el-main style="padding:0;">
                <el-card shadow="never">
                  <template #header>
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                      <span>模板明细</span>
                      <el-button type="primary" @click="useTemplate" :disabled="!selectedTemplate || !selectedPrescription || isDiagnosed">使用该模板</el-button>
                    </div>
                  </template>
                  <el-table :data="templateDetails" size="small">
                    <el-table-column prop="drugsName" label="药品" />
                    <el-table-column prop="drugsFormat" label="规格" />
                    <el-table-column prop="drugsUsage" label="用法" />
                    <el-table-column prop="dosage" label="用量" />
                  </el-table>
                </el-card>
              </el-main>
            </el-container>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>

    <!-- Dialogs -->
    <el-dialog title="新增处方" v-model="addPrescriptionDialogVisible" width="30%">
      <el-input v-model="newPrescriptionName" placeholder="默认为'新增处方+时间戳'" />
      <template #footer>
        <el-button @click="addPrescriptionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addPrescription">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="添加药品" v-model="addDrugDialogVisible" width="60%">
      <el-autocomplete v-model="drugKw" :fetch-suggestions="loadDrugs" placeholder="搜索药品" style="width: 100%; margin-bottom: 10px;" @select="selectDrug" value-key="drugsName"/>
      <template #footer>
        <el-button @click="addDrugDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog title="编辑药品详情" v-model="editDrugDialogVisible" width="30%">
      <el-form :model="currentDrug" label-width="80px">
        <el-form-item label="药品">{{ currentDrug.drugsName }}</el-form-item>
        <el-form-item label="用法"><el-input v-model="currentDrug.drugsUsage" /></el-form-item>
        <el-form-item label="用量"><el-input v-model="currentDrug.dosage" /></el-form-item>
        <el-form-item label="频次"><el-input v-model="currentDrug.frequency" /></el-form-item>
        <el-form-item label="数量"><el-input-number v-model="currentDrug.amount" :min="1" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDrugDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddDrug">确定</el-button>
      </template>
    </el-dialog>

    <!-- AI分析结果弹窗 -->
    <el-dialog v-model="showAiCard" width="540px" :close-on-click-modal="false" :close-on-press-escape="true" class="ai-dialog-beauty">
      <template #title>
        <div class="ai-dialog-title">
          <i class="el-icon-s-opportunity ai-title-icon"></i>
          <span class="ai-title-gradient">AI药方分析</span>
          <el-tag v-if="aiLoading" type="info" size="mini" style="margin-left:10px;">分析中...</el-tag>
        </div>
      </template>
      <div class="ai-dialog-content">
        <el-empty v-if="!aiContent && !aiLoading" description="暂无AI分析结果" />
        <el-scrollbar v-else class="ai-scrollbar-beauty" ref="aiScrollbarRef">
          <transition name="ai-fade-expand" mode="out-in">
            <div v-if="aiParsedContent.analysis || aiParsedContent.comment" key="ai-content" class="ai-typewriter-content-beauty ai-dialog-content-inner" ref="aiContentRef">
              <div v-if="aiParsedContent.analysis" class="ai-section-block">
                <span class="ai-section-title">药方分析结果</span>
                <div class="ai-section-text" v-html="aiParsedContent.analysis"></div>
              </div>
              <div v-if="aiParsedContent.comment" class="ai-section-block ai-section-comment">
                <div class="ai-section-divider"></div>
                <span class="ai-section-title ai-section-title-comment">AI综合评价</span>
                <div class="ai-section-text ai-section-text-comment" v-html="aiParsedContent.comment"></div>
              </div>
            </div>
          </transition>
        </el-scrollbar>
      </div>
      <template #footer>
        <el-button @click="showAiCard = false">关闭</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, computed, watch, defineProps, nextTick } from 'vue';
import { getReq, postReq } from '../../../utils/api';
import { ElMessage, ElMessageBox } from 'element-plus';
import { CirclePlus, Delete, SuccessFilled, DeleteFilled, Refresh } from '@element-plus/icons-vue';
import { useUserStore } from '../../../store/user';
import { EventSourcePolyfill } from 'event-source-polyfill';

const userStore = useUserStore();
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

// Reactive State
const patientDiagnosis = ref('');
const medicalRecordId = ref(null);
const prescriptions = ref([]);
const selectedPrescription = ref(null);
const prescriptionDetails = ref([]);
const selectedDetails = ref([]);
const templates = ref([]);
const templateKw = ref('');
const selectedTemplate = ref(null);
const templateDetails = ref([]);

// Dialogs
const addPrescriptionDialogVisible = ref(false);
const addDrugDialogVisible = ref(false);
const editDrugDialogVisible = ref(false);

// Form Data
const newPrescriptionName = ref('');
const drugKw = ref('');
const currentDrug = ref({});

const aiContent = ref('');
const aiLoading = ref(false);
const showAiCard = ref(false);
let eventSource = null;
let typewriterTimer = null;
let contentBuffer = '';
const aiScrollbarRef = ref(null);
const aiContentRef = ref(null);

// Watcher
watch(() => props.patient, (newPatient) => {
  if (newPatient && newPatient.id) {
    refreshAll();
  } else {
    resetState();
  }
}, { immediate: true, deep: true });

function resetState() {
  patientDiagnosis.value = '';
  medicalRecordId.value = null;
  prescriptions.value = [];
  selectedPrescription.value = null;
  prescriptionDetails.value = [];
}

async function refreshAll() {
  if (!props.patient?.id) return;
  try {
    const res = await getReq(`/neudoc/medicalrecord/getByRegistId?registId=${props.patient.id}`);
    if (res && res.data && res.data.data) {
      medicalRecordId.value = res.data.data.id;
      patientDiagnosis.value = res.data.data.diagnosis;
    } else {
       ElMessage.warning("未找到患者病历信息");
       resetState();
    }
    await loadPrescriptions();
    await loadTemplates();
  } catch (e) {
    console.error(e);
    ElMessage.error("加载患者信息失败");
  }
}

// Prescription Logic
async function loadPrescriptions() {
  if (!props.patient?.id) return;
  try {
    const res = await getReq('/prescription/page', { registId: props.patient.id, count: 100 });
    if (res && res.data && res.data.data && res.data.data.records) {
      prescriptions.value = res.data.data.records;
      if (selectedPrescription.value && !prescriptions.value.find(p => p.id === selectedPrescription.value.id)) {
        selectedPrescription.value = null;
        prescriptionDetails.value = [];
      }
    } else {
      prescriptions.value = [];
    }
  } catch (e) {
    console.error("加载处方列表失败", e);
    ElMessage.error("加载处方列表失败");
  }
}

function openAddPrescriptionDialog() {
  if (!medicalRecordId.value) {
    ElMessage.warning("请先确认患者的病历信息！");
    return;
  }
  newPrescriptionName.value = '';
  addPrescriptionDialogVisible.value = true;
}

async function addPrescription() {
  const payload = {
    medicalId: medicalRecordId.value,
    registId: props.patient.id,
    userId: userStore.userInfo.id,
    prescriptionName: newPrescriptionName.value || `新增处方_${Date.now()}`
  };
  try {
    await postReq('/prescription/add', payload);
    ElMessage.success("处方已添加");
    addPrescriptionDialogVisible.value = false;
    await loadPrescriptions();
  } catch (e) {
    ElMessage.error("添加处方失败");
  }
}

async function deletePrescription() {
  if (!selectedPrescription.value) {
    ElMessage.warning("请选择要删除的处方");
    return;
  }
  const state = selectedPrescription.value.prescriptionState;
  if (state !== 1 && state !== 3) {
    ElMessage.error("只能删除暂存或作废状态的处方！");
    return;
  }
  await ElMessageBox.confirm(`确认删除处方 "${selectedPrescription.value.prescriptionName}"?`, '警告', { type: 'warning' });
  try {
    await postReq('/prescription/del', [selectedPrescription.value.id]);
    ElMessage.success("处方已删除");
    selectedPrescription.value = null;
    prescriptionDetails.value = [];
    await loadPrescriptions();
  } catch(e) {
    ElMessage.error("删除处方失败");
  }
}

async function issuePrescription() {
    if (!selectedPrescription.value) {
        ElMessage.warning("请选择要开立的处方");
        return;
    }
    await updatePrescriptionState(2, '开立');
}

async function cancelPrescription() {
    if (!selectedPrescription.value) {
        ElMessage.warning("请选择要作废的处方");
        return;
    }
    await updatePrescriptionState(3, '作废');
}

async function updatePrescriptionState(state, actionName) {
    try {
        await ElMessageBox.confirm(`确认要${actionName}选中的处方吗?`, '提示');
        await postReq('/prescription/updateState', { ids: [selectedPrescription.value.id], prescriptionState: state });
        ElMessage.success(`处方已${actionName}`);
        await loadPrescriptions();
    } catch(e) {
        if(e !== 'cancel') ElMessage.error(`${actionName}操作失败`);
    }
}


function handlePrescriptionSelect(row) {
  selectedPrescription.value = row;
  loadPrescriptionDetails(row.id);
}

// Drug Logic
async function loadPrescriptionDetails(prescriptionId) {
  try {
    const res = await getReq('/prescriptiondetailed/page', { prescriptionId, count: 500 });
    if (res && res.data && res.data.data && res.data.data.records) {
      prescriptionDetails.value = res.data.data.records;
    } else {
      prescriptionDetails.value = [];
    }
  } catch (e) {
    ElMessage.error("加载处方明细失败");
    prescriptionDetails.value = [];
  }
}

function openAddDrugDialog() {
  if (!selectedPrescription.value) {
    ElMessage.warning("请先选择一个处方");
    return;
  }
  if (selectedPrescription.value.prescriptionState !== 1) {
    ElMessage.error("只能为暂存状态的处方增删药品！");
    return;
  }
  drugKw.value = '';
  addDrugDialogVisible.value = true;
}

async function loadDrugs(query, cb) {
  if (query) {
    try {
      const resp = await getReq('/drugs/page', { keyword: query, count: 20 });
      console.log("Response from /drugs/page:", resp);
      if (resp && resp.data && resp.data.data && resp.data.data.records) {
        cb(resp.data.data.records);
      } else {
        cb([]);
      }
    } catch (error) {
      console.error("加载药品失败:", error);
      cb([]);
    }
  } else {
    cb([]);
  }
}

function selectDrug(drug) {
  currentDrug.value = {
    prescriptionId: selectedPrescription.value.id,
    drugsId: drug.id,
    drugsPrice: drug.drugsPrice,
    amount: 1,
    drugsName: drug.drugsName,
    drugsUsage: '遵医嘱',
    dosage: '遵医嘱',
    frequency: '每日一次'
  };
  editDrugDialogVisible.value = true;
}

async function confirmAddDrug() {
  try {
    await postReq('/prescriptiondetailed/add', currentDrug.value);
    ElMessage.success("药品已添加");
    editDrugDialogVisible.value = false;
    addDrugDialogVisible.value = false;
    await loadPrescriptionDetails(selectedPrescription.value.id);
  } catch (e) {
    ElMessage.error("添加药品失败");
  }
}

async function deleteDrugs() {
  if (selectedDetails.value.length === 0) return;
    if (selectedPrescription.value.prescriptionState !== 1) {
    ElMessage.error("只能为暂存状态的处方增删药品！");
    return;
  }
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedDetails.value.length} 项药品吗?`, '警告', { type: 'warning' });
    const ids = selectedDetails.value.map(d => d.id);
    await postReq('/prescriptiondetailed/del', ids);
    ElMessage.success("药品已删除");
    await loadPrescriptionDetails(selectedPrescription.value.id);
  } catch(e) {
     if(e !== 'cancel') ElMessage.error("删除药品失败");
  }
}

function handleDetailSelectionChange(selection) {
  selectedDetails.value = selection;
}

// Template Logic
async function loadTemplates() {
    try {
        const res = await getReq('/drugstemplate/page', { keyword: templateKw.value, scope: '3', count: 100, userId:userStore.userInfo.id });
        if (res && res.data && res.data.data && res.data.data.records) {
            templates.value = res.data.data.records;
        } else {
            templates.value = [];
        }
    } catch(e) {
        ElMessage.error("加载模板失败");
    }
}

async function handleTemplateSelect(template) {
    selectedTemplate.value = template;
    try {
        const res = await getReq('/drugstemplate/details', { templateId: template.id });
        if (res && res.data && res.data.data) {
            templateDetails.value = res.data.data;
        } else {
            templateDetails.value = [];
        }
    } catch(e) {
        ElMessage.error("加载模板详情失败");
    }
}

async function useTemplate() {
    if (!selectedTemplate.value || !selectedPrescription.value) return;
     if (selectedPrescription.value.prescriptionState !== 1) {
      ElMessage.error("只能为暂存状态的处方使用模板！");
      return;
    }
    try {
        await postReq('/prescription/addByTemplate', {
            prescriptionId: selectedPrescription.value.id,
            templateId: selectedTemplate.value.id
        });
        ElMessage.success("模板已应用");
        await loadPrescriptionDetails(selectedPrescription.value.id);
    } catch (e) {
        ElMessage.error("应用模板失败");
    }
}

// Formatters
function formatState(state) {
  const map = { 1: '暂存', 2: '已开立', 3: '作废' };
  return map[state] || '未知';
}

function formatScope(scope) {
  const map = { '1': '个人', '2': '科室', '3': '全院' };
  return map[scope] || '未知';
}

function filterAIContent(raw) {
  return raw.replace(/\*\*[^*]+\*\*/g, '').replace(/\*\*/g, '').trim();
}
function parseAIContent(raw) {
  const filtered = filterAIContent(raw);
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

function highlightUnreasonable(text) {
  // 扩大关键词范围
  const keywords = [
    '存在问题', '不合理', '建议调整', '警惕', '注意', '需改进', '风险', '警告', '需关注', '需注意', '需警惕',
    '不建议', '错误', '禁忌', '冲突', '重复', '剂量过大', '剂量过小', '超量', '漏用', '遗漏', '慎用',
    '警示', '警报', '异常', '疑似', '需优化', '需调整', '需完善', '需补充', '需修正', '需排查', '需规避',
    '需防范', '需警觉', '需警醒', '需重视', '需避免', '需防止', '需纠正', '需补救', '需修复', '需补全',
    '需补强', '需补偿', '需补足', '需补齐'
  ];
  // 构造正则，匹配关键词及其后面一句（到句号、分号、换行或结尾）
  const pattern = new RegExp(`(${keywords.join('|')})([\s\S]*?)([。；;\n]|$)`, 'g');
  return text.replace(pattern, (match, kw, content, end) => {
    return `<span class='ai-unreasonable-highlight'>${kw}${content}${end}</span>`;
  });
}

const aiParsedContent = computed(() => {
  const parsed = parseAIContent(aiContent.value);
  // 对分析和评价部分都做高亮
  return {
    analysis: parsed.analysis ? highlightUnreasonable(parsed.analysis) : '',
    comment: parsed.comment ? highlightUnreasonable(parsed.comment) : ''
  };
});

function resetAIResult() {
  aiContent.value = '';
  if (typewriterTimer) clearInterval(typewriterTimer);
  if (eventSource) {
    eventSource.close();
    eventSource = null;
  }
  contentBuffer = '';
}
function streamTypewriter(fullText) {
  if (typewriterTimer) clearInterval(typewriterTimer);
  let i = 0;
  const prev = aiContent.value;
  const filteredText = filterAIContent(fullText);
  typewriterTimer = setInterval(() => {
    aiContent.value = prev + filteredText.slice(prev.length, prev.length + i + 1);
    i++;
    if (prev.length + i >= filteredText.length) {
      clearInterval(typewriterTimer);
    }
  }, 18);
}
watch(aiContent, async () => {
  await nextTick();
  if (aiScrollbarRef.value && aiScrollbarRef.value.wrapRef) {
    aiScrollbarRef.value.wrapRef.scrollTop = aiScrollbarRef.value.wrapRef.scrollHeight;
  }
  if (aiContentRef.value && aiContentRef.value.scrollIntoView) {
    aiContentRef.value.scrollIntoView({ behavior: 'smooth', block: 'end' });
  }
});

async function analyzePrescription() {
  if (!selectedPrescription.value || prescriptionDetails.value.length === 0) {
    ElMessage.warning('请选择处方并确保有药品');
    return;
  }
  resetAIResult();
  aiLoading.value = true;
  showAiCard.value = true;
  // 组装诊断和药品信息
  const data = {
    diagnosis: patientDiagnosis.value,
    drugs: prescriptionDetails.value.map(d => ({
      name: d.drugsName,
      usage: d.drugsUsage,
      dosage: d.dosage,
      frequency: d.frequency,
      amount: d.amount
    }))
  };
  const message = encodeURIComponent(JSON.stringify(data));
  const token = userStore.userInfo.token || localStorage.getItem('token');
  // 修改为后端新接口 /ai/ana
  eventSource = new EventSourcePolyfill(`/ai/ana?message=${message}`, {
    headers: { token }
  });
  eventSource.onmessage = function(event) {
    let raw = event.data.trim();
    if (raw.startsWith('data:')) {
      raw = raw.substring(5).trim();
    }
    if(raw === '' || raw.startsWith(':')){
      return;
    }
    try {
      const data = JSON.parse(raw);
      if (data.output && data.output.choices && data.output.choices.length > 0) {
        const message = data.output.choices[0].message;
        if (message && message.content) {
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
</script>

<style scoped>
.el-container {
  height: 100%;
}
.el-card {
  height: 100%;
}
.el-table {
  min-height: 200px; /* Adjust as needed */
}
.ai-card-beauty {
  height: auto;
  min-height: 320px;
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
  max-height: 400px;
  min-height: 120px;
  overflow: auto;
  background: #f6faff;
  border-radius: 16px;
  padding: 22px 26px 22px 26px;
  transition: max-height 0.4s cubic-bezier(.4,0,.2,1);
}
.ai-typewriter-content-beauty {
  font-size: 18px;
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
.ai-unreasonable-highlight {
  background: #fff3e0;
  color: #e65100;
  border-radius: 5px;
  padding: 0 3px;
  font-weight: 500;
  box-shadow: 0 1px 4px rgba(230,81,0,0.06);
  transition: background 0.2s;
}
.ai-dialog-beauty {
  border-radius: 18px;
  box-shadow: 0 8px 40px 0 rgba(80,120,255,0.13), 0 2px 8px 0 rgba(80,120,255,0.07);
  background: linear-gradient(135deg, #fafdff 60%, #e6f0ff 100%);
}
.ai-dialog-title {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 1.2px;
  padding: 8px 0 2px 0;
  background: none;
}
.ai-title-gradient {
  background: linear-gradient(90deg, #2563eb 10%, #4b8cff 90%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 700;
  font-size: 26px;
  margin-left: 8px;
  margin-right: 2px;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}
.ai-dialog-content {
  padding: 0 6px 0 6px;
}
.ai-dialog-content-inner {
  padding: 18px 8px 8px 8px;
  border-radius: 12px;
  background: #fafdff;
  min-height: 120px;
}
.ai-section-divider {
  height: 1.5px;
  background: linear-gradient(90deg, #e0eaff 0%, #fafdff 100%);
  margin: 18px 0 12px 0;
  border-radius: 2px;
}
.ai-section-comment {
  background: linear-gradient(90deg, #f7faff 0%, #fafdff 100%);
  border-radius: 10px;
  box-shadow: 0 2px 8px 0 rgba(80,120,255,0.06);
  padding: 18px 18px 10px 18px;
  margin-top: 10px;
  margin-bottom: 0;
  border-left: 5px solid #2563eb;
}
.ai-section-title-comment {
  color: #e65100;
  font-size: 20px;
  margin-bottom: 8px;
}
.ai-section-text-comment {
  color: #e65100;
  font-size: 18px;
  font-weight: 500;
  margin-left: 0;
  line-height: 2.1;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
}
.ai-unreasonable-highlight {
  background: #fff3e0;
  color: #e65100;
  border-radius: 6px;
  padding: 0 4px;
  font-weight: 600;
  box-shadow: 0 1px 4px rgba(230,81,0,0.08);
  border: 1px solid #ffe0b2;
  transition: background 0.2s;
}
</style>