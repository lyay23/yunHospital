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
                <el-button type="primary" :icon="CirclePlus" @click="openAddPrescriptionDialog">增方</el-button>
                <el-button type="danger" :icon="Delete" @click="deletePrescription">删方</el-button>
                <el-button type="success" :icon="SuccessFilled" @click="issuePrescription">开立</el-button>
                <el-button type="warning" :icon="DeleteFilled" @click="cancelPrescription">作废</el-button>
                <el-button :icon="Refresh" @click="refreshAll">刷新</el-button>
              </el-button-group>
          </el-col>
        </el-row>
        </el-card>
          </el-col>
        </el-row>

    <!-- Main Content -->
    <el-row :gutter="10" style="flex: 1; min-height: 0;">
      <!-- Left: Prescription List -->
      <el-col :span="6">
        <el-card shadow="never" style="height: 100%; display: flex; flex-direction: column;">
          <template #header><span>处方列表</span></template>
          <el-table :data="prescriptions" highlight-current-row @row-click="handlePrescriptionSelect" height="100%" style="flex: 1;">
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
                <el-button type="primary" :icon="CirclePlus" size="small" @click="openAddDrugDialog" :disabled="!selectedPrescription">增药</el-button>
                <el-button type="danger" :icon="Delete" size="small" @click="deleteDrugs" :disabled="selectedDetails.length === 0">删药</el-button>
              </el-button-group>
            </div>
          </template>
          <el-table :data="prescriptionDetails" @selection-change="handleDetailSelectionChange" height="100%" style="flex: 1;">
            <el-table-column type="selection" width="55" />
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
                <el-table :data="templates" highlight-current-row @row-click="handleTemplateSelect" size="small" style="margin-top: 10px;">
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
                      <el-button type="primary" @click="useTemplate" :disabled="!selectedTemplate || !selectedPrescription">使用该模板</el-button>
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
	</el-container>
</template>

<script setup>
import { ref, computed, watch, defineProps } from 'vue';
import { getReq, postReq } from '../../../utils/api';
import { ElMessage, ElMessageBox } from 'element-plus';
import { CirclePlus, Delete, SuccessFilled, DeleteFilled, Refresh } from '@element-plus/icons-vue';
import { useUserStore } from '../../../store/user';

const userStore = useUserStore();
const props = defineProps({
  patient: {
    type: Object,
    default: null
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
</style>