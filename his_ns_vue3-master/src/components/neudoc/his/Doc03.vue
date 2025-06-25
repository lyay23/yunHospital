<template>
  <div class="container" style="padding: 20px;">
    <el-dialog
      title="新增检验申请"
      v-model="dialogVisible"
      width="40%">
      <el-form size="mini">
        <el-form-item>
           <el-input v-model="fmeditemKw" placeholder="按编码/名称/拼音搜索..." class="input-with-select" style="width: 100%">
            <template #prepend>检验名称：</template>
            <template #append><el-button :icon="Search" @click="searchFmeditem(1)"></el-button></template>
            </el-input>
        </el-form-item>
        <el-form-item>
          <el-table border :data="fmeditemData" size="mini" max-height="250px" @selection-change="handleFmeditemSelectionChange">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column property="itemCode" label="编码" width="120"></el-table-column>
            <el-table-column property="itemName" label="项目名称" ></el-table-column>
            <el-table-column property="price" label="单价" width="80"></el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item>
          <el-pagination
                v-if="fmeditemPage.total > 0"
                small
                background
                layout="prev, pager, next"
                :total="fmeditemPage.total"
                :page-size="fmeditemPage.size"
                :current-page="fmeditemPage.current"
                @current-change="handleFmeditemPageChange"
                style="margin-top: 10px; text-align: center;"
            />
        </el-form-item>
        <el-form-item label="执行科室" >
          <el-select  placeholder="请选择" v-model="selectedDeptId">
            <el-option
              v-for="item in deptData"
              :key="item.id"
              :label="item.deptName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否加急：" >
          <el-switch
            v-model="isUrgent"
            active-color="#13ce66">
          </el-switch>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addCheckApply">确 定</el-button>
          <el-button @click="dialogVisible = false">取 消</el-button>
        </el-form-item>
      </el-form>
  </el-dialog>

    <el-row :gutter="20">
      <!-- Left Column -->
      <el-col :span="16">
        <el-space direction="vertical" alignment="stretch" style="width: 100%;">
          <el-card shadow="never">
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <el-space>
                    <el-tag>本项目金额合计:</el-tag>
                    <el-tag type="warning">{{ totalAmount }}元</el-tag>
                </el-space>
                <el-space>
                  <el-button type="primary" :icon="Plus" @click="openAddDialog">新增项目</el-button>
                  <el-button type="danger" :icon="Delete" @click="removeCheckApply">删除项目</el-button>
                  <el-button :icon="Document" @click="saveCheckApply">暂存</el-button>
                  <el-button type="success" :icon="SuccessFilled" @click="openCheckApply">开立</el-button>
                  <el-button :icon="DeleteFilled" @click="cancelCheckApply">作废</el-button>
                  <el-button :icon="Collection" @click="saveAsTemplate">存为套组</el-button>
                </el-space>
              </div>
            </template>
            <el-table :data="checkApplyData" style="width: 100%;" @selection-change="handleCheckApplySelectionChange">
                <el-table-column type="selection"></el-table-column>
                <el-table-column property="itemName" label="项目名称" ></el-table-column>
                <el-table-column property="deptName" label="执行科室" width="100"></el-table-column>
                <el-table-column label="加急" width="60" align="center">
                   <template #default="scope">
                      <el-tag :type="scope.row.isUrgent === 1 ? 'danger' : 'info'" size="small">{{ scope.row.isUrgent === 1 ? '是' : '否' }}</el-tag>
                   </template>
                </el-table-column>
                <el-table-column label="状态" width="80" align="center">
                   <template #default="scope">
                      <span>{{ scope.row.state === 1 ? '暂存' : (scope.row.state === 2 ? '已开立' : '已作废') }}</span>
                   </template>
                </el-table-column>
                <el-table-column property="price" label="单价" width="80"></el-table-column>
                 <template #empty>
                    <el-empty description="暂无检验项目" />
                </template>
            </el-table>
          </el-card>

          <el-card shadow="never">
            <template #header>
              <span>目的要求</span>
            </template>
            <el-input type="textarea" v-model="requirement" :rows="4" placeholder="【检验目的：】【患者主诉：】【现病史：】" resize="none"></el-input>
          </el-card>
        </el-space>
      </el-col>
      
      <!-- Right Column -->
      <el-col :span="8">
        <el-card shadow="never">
            <template #header>
              <span>检验套组</span>
            </template>
             <el-table :data="templateData" style="width: 100%">
              <el-table-column prop="name" label="名称"></el-table-column>
              <el-table-column fixed="right" label="操作" width="180" align="center">
                <template #default="scope">
                  <el-space>
                    <el-button @click.prevent="useTemplate(scope.row)" type="primary" text size="small">使用</el-button>
                    <el-button @click.prevent="showTemplateDetails(scope.row)" type="info" text size="small">详细</el-button>
                    <el-button @click.prevent="deleteTemplate(scope.row)" type="danger" text size="small">删除</el-button>
                  </el-space>
                </template>
              </el-table-column>
              <template #empty>
                  <el-empty description="暂无检验套组" />
              </template>
    </el-table>
        </el-card>
      </el-col>
    </el-row>
    <el-dialog
        title="套组详情"
        v-model="detailsDialogVisible"
        width="30%">
        <el-table :data="templateDetails" border size="mini">
            <el-table-column property="itemName" label="项目名称" />
            <el-table-column property="price" label="单价" />
        </el-table>
        <template #footer>
            <el-button @click="detailsDialogVisible = false">关 闭</el-button>
        </template>
  </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, defineProps } from 'vue';
import { getReq, postReq } from '../../../utils/api';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, Delete, Document, SuccessFilled, DeleteFilled, Collection } from '@element-plus/icons-vue';
import { useUserStore } from '../../../store/user';

const userStore = useUserStore();

const props = defineProps({
    patient: {
        type: Object,
        default: null
    }
});

// Dialog state
const dialogVisible = ref(false);
const fmeditemKw = ref('');
const fmeditemData = ref([]);
const fmeditemPage = ref({ total: 0, size: 5, current: 1 });
const selectedFmeditems = ref([]);
const deptData = ref([]);
const selectedDeptId = ref(null);
const isUrgent = ref(false);

// Main content state
const checkApplyData = ref([]);
const selectedCheckApplies = ref([]);
const requirement = ref('');
const templateData = ref([]);
const medicalRecordId = ref(null);
const detailsDialogVisible = ref(false);
const templateDetails = ref([]);

// Computed total amount
const totalAmount = computed(() => {
  return checkApplyData.value.reduce((sum, item) => sum + (item.price || 0), 0).toFixed(2);
});

// Watch for patient changes
watch(() => props.patient, async (newPatient, oldPatient) => {
  if (newPatient && newPatient.id && (!oldPatient || newPatient.id !== oldPatient.id)) {
    // 1. 获取病历ID
    try {
      const medRes = await getReq('/neudoc/medicalrecord/getByRegistId', { registId: newPatient.id });
      if (medRes.data && medRes.data.data && medRes.data.data.id) {
        medicalRecordId.value = medRes.data.data.id;
      } else {
        // 如果没有病历，创建一个，并获取新ID
        const newMedRed = { registId: newPatient.id, caseNumber: newPatient.caseNumber };
        const createRes = await postReq('/neudoc/medicalrecord/save', newMedRed);
        if (createRes.data && createRes.data.data && createRes.data.data.id) {
            medicalRecordId.value = createRes.data.data.id;
        } else {
            ElMessage.error("创建新病历失败，无法继续操作");
            return;
        }
      }
      
      // 成功获取或创建病历ID后，加载检验申请
      loadCheckApplies(newPatient.id);
      loadTemplates(); // Also load templates for the doctor

    } catch (error) {
      console.error("获取或创建病历失败", error);
      ElMessage.error("准备患者病历信息时出错，请重试");
        }
  } else if (!newPatient) {
    // 清理数据
    checkApplyData.value = [];
    requirement.value = '';
    medicalRecordId.value = null;
    templateData.value = []; // Also clear templates
  }
}, { immediate: true, deep: true });

async function loadCheckApplies(registId) {
    const url = `/checkapply/page?registId=${registId}&recordType=2&count=100`;
    try {
        const result = await getReq(url);
        checkApplyData.value = result.data.data.records.map(item => ({
            ...item,
        }));
        
        if (checkApplyData.value.length > 0) {
             if(checkApplyData.value[0].objective)
                requirement.value = checkApplyData.value[0].objective;
             if(checkApplyData.value[0].medicalId)
                medicalRecordId.value = checkApplyData.value[0].medicalId;
        } else {
            requirement.value = '';
        }
    } catch (error) {
        console.error("加载检验申请失败:", error);
        checkApplyData.value = [];
        requirement.value = '';
        medicalRecordId.value = null; // Reset if no items
  }
}

async function loadTemplates() {
    const url = `/checktemplate/page?recordType=2&doctorId=${userStore.userInfo.id}&count=100`;
     try {
        const result = await getReq(url);
        templateData.value = result.data.data.records;
    } catch (error) {
        console.error("加载检验模板失败:", error);
}
}

async function loadDepts() {
    // 调用分页接口，但一次性加载所有检验科室
    const url = `/department/tech`;
    try {
        const result = await getReq(url);
        deptData.value = result.data.data;
    } catch (error) {
        console.error("加载执行科室失败:", error);
    }
}

async function searchFmeditem(pn = 1) {
    const url = `/fmeditem/page?expClassId=73&pn=${pn}&count=${fmeditemPage.value.size}&keyword=${fmeditemKw.value}`;
    try {
        const result = await getReq(url);
        fmeditemData.value = result.data.data.records;
        fmeditemPage.value.total = result.data.data.total;
        fmeditemPage.value.current = result.data.data.current;
    } catch (error) {
        console.error("加载检验项目失败:", error);
    }
}

function handleFmeditemPageChange(pn) {
    searchFmeditem(pn);
}

function openAddDialog() {
  if (!props.patient) {
    ElMessage.warning('请先选择一位患者');
        return;
    }
  dialogVisible.value = true;
  fmeditemKw.value = '';
  fmeditemData.value = [];
  isUrgent.value = false;
  selectedDeptId.value = null;
  loadDepts();
  searchFmeditem(1);
}

function handleFmeditemSelectionChange(selection) {
  selectedFmeditems.value = selection;
}

function handleCheckApplySelectionChange(selection) {
  selectedCheckApplies.value = selection;
}

function addCheckApply() {
  if (selectedFmeditems.value.length === 0) {
    ElMessage.warning('请至少选择一个检验项目');
        return;
    }
  if (!selectedDeptId.value) {
    ElMessage.warning('请选择执行科室');
        return;
    }

  const selectedDept = deptData.value.find(d => d.id === selectedDeptId.value);

  selectedFmeditems.value.forEach(item => {
    checkApplyData.value.push({
      registId: props.patient.id,
      itemId: item.id,
      itemName: item.itemName,
      price: item.price,
      deptId: selectedDeptId.value,
      deptName: selectedDept.deptName,
      isUrgent: isUrgent.value ? 1 : 0,
        state: 1, // 1 for '暂存'
    });
  });

  dialogVisible.value = false;
}

async function removeCheckApply() {
  if (selectedCheckApplies.value.length === 0) {
        ElMessage.warning('请选择要删除的项目');
        return;
    }

  // Separate items into those that need DB deletion and those that are just in UI
  const idsToDelete = selectedCheckApplies.value
    .filter(item => item.id)
    .map(item => item.id);
  
  const clientOnlyItems = selectedCheckApplies.value.filter(item => !item.id);

  // Handle DB deletion
  if (idsToDelete.length > 0) {
    try {
      const result = await postReq('/checkapply/del', idsToDelete);
      if (!result.data.result) {
        ElMessage.error(result.data.errMsg || '删除失败');
        return; // Stop if DB deletion fails
    }
    } catch (error) {
      ElMessage.error('网络错误，删除失败');
      return;
    }
}

  // If DB deletion was successful (or not needed), update the UI
  checkApplyData.value = checkApplyData.value.filter(item => 
    !selectedCheckApplies.value.some(selected => selected === item)
  );
  ElMessage.success('删除成功');
  selectedCheckApplies.value = [];
}

async function saveCheckApply() {
  if (!props.patient) {
    ElMessage.warning('请先选择一位患者');
    return;
  }
  
  if (checkApplyData.value.length === 0) {
    ElMessage.warning('没有可暂存的项目');
        return;
    }
  
  // If medicalRecordId is not available, fetch it first
  if (!medicalRecordId.value) {
      try {
          const medRes = await getReq('/neudoc/medicalrecord/getByRegistId', { registId: props.patient.id });
          if (medRes.data && medRes.data.data && medRes.data.data.id) {
              medicalRecordId.value = medRes.data.data.id;
          } else {
              // 此处不应再创建，因为 watcher 里已经处理过了
              ElMessage.error('无法获取病历信息，暂存失败');
              return;
          }
      } catch (error) {
          ElMessage.error('获取病历信息时发生网络错误');
          return;
    }
  }

  // Use saveOrUpdateBatch to simplify logic
  const payload = checkApplyData.value.map(item => ({
      ...item,
      medicalId: medicalRecordId.value,
      registId: props.patient.id,
      objective: requirement.value,
      recordType: 2, // 检验
      state: 1, // 暂存
      id: item.id || null,
      doctorId: userStore.userInfo.id,
  }));

  try {
    const result = await postReq('/checkapply/saveOrUpdateBatch', payload);
    ElMessage.success(`暂存成功`);
    // The controller now returns the updated items. We can update the local state.
    if (result.data && result.data.result) {
        checkApplyData.value = result.data.data;
    } else {
        // Fallback to reloading if the response is not as expected
        loadCheckApplies(props.patient.id);
    }
  } catch (error) {
     ElMessage.error('网络错误，操作失败');
  }
}

async function openCheckApply() {
  if (selectedCheckApplies.value.length === 0) {
        ElMessage.warning('请选择要开立的项目');
        return;
    }
    
  // 筛选出已暂存（有ID）的项目
  const idsToOpen = selectedCheckApplies.value
    .filter(item => item.id) 
    .map(item => item.id);
    
  if (selectedCheckApplies.value.some(item => !item.id)) {
     ElMessage.warning('有新项目尚未暂存，请先点击"暂存"按钮。');
  }

  if (idsToOpen.length === 0) {
      return;
  }

  const payload = {
    ids: idsToOpen,
    state: 2, // 开立
  };

  try {
    const result = await postReq('/checkapply/updateState', payload);
    if (result.data.result) {
        ElMessage.success('开立成功');
      loadCheckApplies(props.patient.id);
      selectedCheckApplies.value = [];
    } else {
        ElMessage.error(result.data.errMsg || '开立失败');
    }
  } catch (error) {
    ElMessage.error('网络错误，开立失败');
    }
}

async function cancelCheckApply() {
   if (selectedCheckApplies.value.length === 0) {
        ElMessage.warning('请选择要作废的项目');
        return;
    }

  const idsToCancel = selectedCheckApplies.value
    .filter(item => item.id) 
    .map(item => item.id);

  if (selectedCheckApplies.value.some(item => !item.id)) {
     ElMessage.warning('新项目无法被作废，请先暂存或删除。');
  }

  if (idsToCancel.length === 0) {
      return;
  }

  const payload = {
    ids: idsToCancel,
    state: 0, // 作废
  };

  try {
    const result = await postReq('/checkapply/updateState', payload);
    if (result.data.result) {
        ElMessage.success('作废成功');
      loadCheckApplies(props.patient.id);
      selectedCheckApplies.value = [];
    } else {
        ElMessage.error(result.data.errMsg || '作废失败');
    }
    } catch (error) {
    ElMessage.error('网络错误，作废失败');
    }
}

async function useTemplate(template) {
    try {
        const result = await getReq(`/checkrelation/items?templateId=${template.id}`);
        if (result.data.result && result.data.data) {
            const items = result.data.data;
            if (items.length > 0) {
                 // 默认取第一个项目的执行科室，或提供选择
                const defaultDeptId = items[0].deptId; 
                const defaultDeptName = items[0].deptName;

                items.forEach(item => {
                    // 检查是否已存在相同的itemId
                    if (!checkApplyData.value.some(existing => existing.itemId === item.id)) {
                        checkApplyData.value.push({
                            registId: props.patient.id,
        itemId: item.id,
        itemName: item.itemName,
        price: item.price,
                            deptId: defaultDeptId, 
                            deptName: defaultDeptName,
        isUrgent: 0,
                            state: 1, // 默认为暂存
                        });
                    }
                });
                ElMessage.success(`套组"${template.name}"已添加`);
            }
    } else {
            ElMessage.error('获取套组项目失败');
        }
    } catch (error) {
        console.error('使用模板失败:', error);
        ElMessage.error('网络错误，使用模板失败');
    }
}

async function showTemplateDetails(template) {
  try {
    const result = await getReq(`/checkrelation/items?templateId=${template.id}`);
    if (result.data.result && result.data.data) {
      templateDetails.value = result.data.data;
      detailsDialogVisible.value = true;
    } else {
      ElMessage.error('获取套组详情失败');
    }
  } catch (error) {
    console.error('获取套组详情失败:', error);
    ElMessage.error('网络错误，获取套组详情失败');
    }
}

async function deleteTemplate(template) {
  ElMessageBox.confirm(
    `确定要删除套组 " ${template.name} " 吗？`,
    '确认删除',
    {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
    }
  ).then(async () => {
    try {
        const result = await postReq('/checktemplate/del', template);
        if (result.data.result) {
            ElMessage.success('删除成功');
        loadTemplates(); 
        } else {
            ElMessage.error(result.data.errMsg || '删除失败');
        }
    } catch (error) {
      ElMessage.error('网络错误，删除失败');
    }
  }).catch(() => {
    // User cancelled
  });
        }

async function saveAsTemplate() {
    if (checkApplyData.value.length === 0) {
        ElMessage.warning('没有可存为套组的项目');
        return;
    }

    ElMessageBox.prompt('请输入套组名称', '存为套组', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '套组名称不能为空',
    }).then(async ({ value }) => {
        const itemIds = checkApplyData.value.map(item => item.itemId);
        const payload = {
            name: value,
            scope: '3', // 3-个人
            recordType: '2', // 2-检验
            userId: userStore.userInfo.id,
            itemIds: itemIds,
        };

        try {
            const result = await postReq('/checktemplate/add', payload);
            if (result.data.result) {
                ElMessage.success('套组保存成功');
                loadTemplates(); // Refresh the template list
            } else {
                ElMessage.error(result.data.errMsg || '保存失败');
    }
        } catch (error) {
            ElMessage.error('网络错误，保存失败');
        }
    }).catch(() => {
        // User cancelled
    });
}
</script>

<style scoped>
/* Add any specific styles if needed */
</style>