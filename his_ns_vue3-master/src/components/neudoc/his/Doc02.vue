<template>
  <el-container>
    <el-main>
      <el-row>
        <el-col :span="14">
          <div class="toolbar">
            <el-button type="primary" size="small" @click="handleAddItem">新增项目</el-button>
            <el-button type="danger" size="small" @click="handleDeleteItem">删除项目</el-button>
            <el-button type="success" size="small" @click="handleCommitItem">开立项目</el-button>
            <el-button type="warning" size="small" @click="handleCancelItem">作废项目</el-button>
            <el-button size="small" @click="handleSaveAsTemplate">存为组套</el-button>
            <el-button :icon="Refresh" size="small" @click="handleRefresh">刷新</el-button>
          </div>
          <div class="total-amount">
            <el-tag>本项目金额合计：{{ totalAmount }}元</el-tag>
          </div>
          <el-table :data="checkApplyList" style="width: 100%" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="30" align="center"/>
            <el-table-column prop="name" label="申请名称" width="120" align="center"/>
            <el-table-column prop="itemName" label="项目名称" width="180" align="center"/>
            <el-table-column prop="execDept" label="执行科室" align="center"/>
            <el-table-column prop="execState" label="执行状态" align="center"/>
            <el-table-column prop="price" label="单价" align="center"/>
            <el-table-column label="检查结果" align="center">
              <template #default="scope">
                <el-link type="primary" v-if="scope.row.state === 5" @click="viewResult(scope.row)">查看结果</el-link>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
                v-if="checkApplyList.length > 0"
                background
                layout="prev, pager, next"
                :total="checkApplyPage.total"
                :page-size="checkApplyPage.size"
                @current-change="handleCheckApplyPageChange"
                style="margin-top: 10px;"
            />
        </el-col>
        <el-col :span="10">
          <div class="template-section">
            <el-card class="box-card">
              <template #header>
                <div class="clearfix">
                  <span>常用模板</span>
                </div>
              </template>
              <el-table :data="templateList" style="width: 100%">
                <el-table-column prop="name" label="名称" align="center" />
                <el-table-column label="操作" align="center" >
                  <template #default="scope">
                    <div style="display: flex; flex-direction: row; align-items: center;">
                        <el-button type="text" size="small" @click="useTemplate(scope.row)">使用</el-button>
                        <el-button type="text" size="small" @click="viewTemplate(scope.row)" style="margin-left: 8px;">详细</el-button>
                        <el-button v-if="parseInt(scope.row.scope) === 3" type="text" size="small" @click="handleDeleteTemplate(scope.row)" style="margin-left: 8px; color: #F56C6C;">删除</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </el-main>
  </el-container>

  <!-- 新增项目 Dialog -->
  <el-dialog title="新增检查项目" v-model="addItemDialogVisible" width="60%">
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <!-- 选择模板 Tab -->
      <el-tab-pane label="选择模板" name="template">
        <el-row>
          <el-col :span="12">
            <el-input v-model="templateKeyword" placeholder="按模板名称搜索" class="input-with-select">
              <template #append><el-button :icon="Search" @click="searchTemplates" /></template>
            </el-input>
          </el-col>
        </el-row>
        <el-table :data="templates.records" style="width: 100%; margin-top: 10px;" @row-click="handleTemplateSelect" highlight-current-row>
          <el-table-column prop="name" label="模板名称" />
        </el-table>
        <el-pagination background layout="prev, pager, next" :total="templates.total" :page-size="templates.size" @current-change="handleTemplatePageChange" style="margin-top: 10px;" />
      </el-tab-pane>

      <!-- 选择项目 Tab -->
      <el-tab-pane label="选择项目" name="item">
        <el-row>
          <el-col :span="12">
            <el-input v-model="fmeditemKeyword" placeholder="按项目助记码搜索" class="input-with-select">
              <template #append><el-button :icon="Search" @click="searchFmeditems" /></template>
            </el-input>
          </el-col>
        </el-row>
        <el-table :data="fmeditems.records" style="width: 100%; margin-top: 10px;" @row-click="handleFmeditemSelect" highlight-current-row>
          <el-table-column prop="itemName" label="项目名称" />
          <el-table-column prop="price" label="单价" width="100" />
          <el-table-column prop="deptName" label="执行科室" width="150" />
        </el-table>
        <el-pagination background layout="prev, pager, next" :total="fmeditems.total" :page-size="fmeditems.size" @current-change="handleFmeditemPageChange" style="margin-top: 10px;" />
      </el-tab-pane>
    </el-tabs>

    <el-divider />

    <div v-if="selectedTemplate || selectedFmeditem">
      <h3>项目详情</h3>
      <el-form :model="newItemForm" label-width="120px">
        <el-form-item label="申请名称">
          <el-input v-model="newItemForm.applicationName" />
        </el-form-item>
        <el-form-item label="项目名称">
          <el-input v-model="newItemForm.name" disabled />
        </el-form-item>
        <el-form-item label="目的要求">
          <el-input v-model="newItemForm.objective" type="textarea" />
        </el-form-item>
        <el-form-item label="检查部位">
          <el-input v-model="newItemForm.position" />
        </el-form-item>
        <el-form-item label="是否加急">
          <el-switch v-model="newItemForm.isUrgent" />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="addItemDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddItem" :disabled="!selectedTemplate && !selectedFmeditem">确认新增</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 存为组套 Dialog -->
  <el-dialog title="存为组套" v-model="saveAsTemplateDialogVisible">
    <!-- Dialog content -->
  </el-dialog>

  <!-- Template Details Dialog -->
  <el-dialog :title="'模板详情: ' + currentTemplateName" v-model="templateDetailDialogVisible" width="50%">
    <el-table :data="templateDetailItems">
      <el-table-column property="itemName" label="项目名称"></el-table-column>
      <el-table-column property="price" label="价格"></el-table-column>
      <el-table-column property="deptName" label="执行科室"></el-table-column>
    </el-table>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { Refresh, Search } from '@element-plus/icons-vue';
import { fetchData, postReq, get } from '../../../utils/api';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '../../../store/user.js';

const props = defineProps({
    patient: {
        type: Object,
        default: null
    }
});

const userStore = useUserStore();
const checkApplyList = ref([]);
const checkApplyPage = ref({ total: 0, size: 10, current: 1 });
const currentRegistId = ref(null);
const currentMedicalId = ref(null);
const selectedItems = ref([]);

// For right-side common templates
const templateList = ref([]); 
const templateDetailDialogVisible = ref(false);
const templateDetailItems = ref([]);
const currentTemplateName = ref('');

const addItemDialogVisible = ref(false);
const saveAsTemplateDialogVisible = ref(false);

const activeTab = ref('template');
const templateKeyword = ref('');
const templates = ref({ records: [], total: 0, size: 10 });
const selectedTemplate = ref(null);

const fmeditemKeyword = ref('');
const fmeditems = ref({ records: [], total: 0, size: 10 });
const selectedFmeditem = ref(null);

const newItemForm = ref({
  name: '',
  applicationName: '',
  objective: '',
  position: '',
  isUrgent: false,
  price: 0,
});

watch(() => props.patient, (newPatient) => {
    if (newPatient) {
        loadCheckApply(newPatient.id);
        loadCommonTemplates();
    } else {
        clearList();
        templateList.value = [];
    }
}, { immediate: true });

const totalAmount = computed(() => {
  return checkApplyList.value.reduce((sum, item) => sum + (item.price || 0), 0).toFixed(2);
});

async function loadCheckApply(registerId, pn = 1) {
    if (registerId) {
        currentRegistId.value = registerId;
        try {
            const medRes = await get('/neudoc/medicalrecord/getByRegistId', { registId: registerId });
            if(medRes.data.result && medRes.data.data && medRes.data.data.id){
                currentMedicalId.value = medRes.data.data.id;
        } else {
                currentMedicalId.value = null;
            }
        } catch (error) {
            console.error("获取病历ID失败", error);
            currentMedicalId.value = null;
        }
    }
    
    if (!currentRegistId.value) return;

    const url = `/checkapply/page?registId=${currentRegistId.value}&recordType=1&pn=${pn}&count=${checkApplyPage.value.size}`;
    const result = await fetchData(url);
    if(result.result) {
        checkApplyList.value = result.data.records.map(item => ({...item, execState: formatState(item.state)}));
        checkApplyPage.value.total = result.data.total;
        checkApplyPage.value.current = result.data.current;
    } else {
        checkApplyList.value = [];
        checkApplyPage.value.total = 0;
    }
}

function handleCheckApplyPageChange(pn) {
    loadCheckApply(null, pn);
}

function clearList() {
    checkApplyList.value = [];
    selectedItems.value = [];
    checkApplyPage.value = { total: 0, size: 10, current: 1 };
    currentRegistId.value = null;
    currentMedicalId.value = null;
}

function handleAddItem() {
  addItemDialogVisible.value = true;
  activeTab.value = 'template';
  resetDialogState();
  if(templates.value.records.length === 0) loadTemplates(1);
}

function handleTabClick(tab) {
    resetDialogState();
    if(tab.paneName === 'template' && templates.value.records.length === 0) {
        loadTemplates(1);
    } else if (tab.paneName === 'item' && fmeditems.value.records.length === 0) {
        loadFmeditems(1);
    }
}

function resetDialogState(){
    newItemForm.value = { name: '', applicationName: '', objective: '', position: '', isUrgent: false, price: 0 };
    selectedFmeditem.value = null;
    selectedTemplate.value = null;
    templateKeyword.value = '';
    fmeditemKeyword.value = '';
}

async function loadTemplates(pn = 1) {
    const url = `/checktemplate/page?scope=1&pn=${pn}&count=10&keyword=${templateKeyword.value}`;
    const result = await fetchData(url);
    if(result.result) {
        templates.value = result.data;
    }
}

function handleTemplatePageChange(pn) {
    loadTemplates(pn);
}

function searchTemplates() {
    loadTemplates(1);
}

async function loadFmeditems(pn = 1) {
    const url = `/fmeditem/page?pn=${pn}&count=10&keyword=${fmeditemKeyword.value}`;
    const result = await fetchData(url);
    if(result.result) {
        fmeditems.value = result.data;
    }
}

function handleFmeditemPageChange(pn) {
    loadFmeditems(pn);
}

function searchFmeditems() {
    loadFmeditems(1);
}

async function handleTemplateSelect(template) {
    selectedTemplate.value = template;
    selectedFmeditem.value = null; 
    newItemForm.value.name = template.name;
    newItemForm.value.applicationName = template.name;
}

function handleFmeditemSelect(item) {
    selectedFmeditem.value = item;
    selectedTemplate.value = null; 
    newItemForm.value.name = item.itemName;
    newItemForm.value.price = item.price;
}

async function confirmAddItem() {
    if (!props.patient) {
        ElMessage.error('请先选择患者');
        return;
    }
    if (!currentMedicalId.value){
        try {
            const medRes = await get('/neudoc/medicalrecord/getByRegistId', { registId: props.patient.id });
            if(medRes.data.result && medRes.data.data && medRes.data.data.id){
                currentMedicalId.value = medRes.data.data.id;
        } else {
             ElMessage.error('获取病历信息失败,请先创建病历首页!');
                return;
            }
        } catch (error) {
            ElMessage.error('获取病历信息时发生网络错误');
            return;
        }
    }

    let itemsToAdd = [];

    if (selectedTemplate.value) {
        const url = `/checkrelation/items?templateId=${selectedTemplate.value.id}`;
        const result = await fetchData(url);
        if(result.result && result.data.length > 0) {
            itemsToAdd = result.data.map(item => ({
                medicalId: currentMedicalId.value,
                registId: currentRegistId.value,
                itemId: item.id,
                name: newItemForm.value.applicationName,
                itemName: item.itemName,
                objective: newItemForm.value.objective,
                position: newItemForm.value.position,
                isUrgent: newItemForm.value.isUrgent ? 1 : 0,
                num: 1, 
                doctorId: userStore.getUserInfo.value.id,
                recordType: 1 // 1 for check
            }));
        }
    } else if (selectedFmeditem.value) {
        itemsToAdd.push({
            medicalId: currentMedicalId.value,
            registId: currentRegistId.value,
            itemId: selectedFmeditem.value.id,
            name: newItemForm.value.applicationName,
            itemName: selectedFmeditem.value.itemName,
            objective: newItemForm.value.objective,
            position: newItemForm.value.position,
            isUrgent: newItemForm.value.isUrgent ? 1 : 0,
            num: 1,
            doctorId: userStore.getUserInfo.value.id,
            recordType: 1 // 1 for check
        });
    }

    if (itemsToAdd.length === 0) {
        ElMessage.error('没有要新增的项目');
        return;
    }

    const resp = await postReq("/checkapply/add", itemsToAdd);

    if (resp.data.result) {
        ElMessage.success('新增成功');
        addItemDialogVisible.value = false;
        loadCheckApply(currentRegistId.value);
    } else {
        ElMessage.error(resp.data.errMsg || '新增失败');
    }
}


function handleSelectionChange(val) {
  selectedItems.value = val;
}

async function handleStateChange(state, successMsg, errorMsg, warningMsg){
    if (selectedItems.value.length === 0) {
        ElMessage.warning(warningMsg || '请至少选择一个项目');
        return;
    }
    const ids = selectedItems.value.map(item => item.id);
    const resp = await postReq('/checkapply/updateState', { ids, state: state });
    if (resp.data.result) {
        ElMessage.success(successMsg);
        loadCheckApply(currentRegistId.value);
    } else {
        ElMessage.error(resp.data.errMsg || errorMsg);
    }
}

function handleCommitItem() {
    handleStateChange(2, '开立成功', '开立失败');
}

function handleCancelItem() {
    handleStateChange(0, '作废成功', '作废失败');
}

async function handleDeleteItem() {
    if (selectedItems.value.length === 0) {
        ElMessage.warning('请至少选择一个项目');
        return;
    }
    const uncommittedItems = selectedItems.value.filter(item => item.state === 1);
    if (uncommittedItems.length !== selectedItems.value.length) {
        ElMessage.error('只能删除暂存状态的项目');
        return;
    }
    
    ElMessageBox.confirm('确定要删除选中的项目吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        const ids = uncommittedItems.map(item => item.id);
        const resp = await postReq('/checkapply/del', ids);
        if (resp.data.result) {
            ElMessage.success('删除成功');
            loadCheckApply(currentRegistId.value);
        } else {
            ElMessage.error(resp.data.errMsg || '删除失败');
        }
    }).catch(() => {});
}

function handleRefresh() {
    if (currentRegistId.value) {
        loadCheckApply(currentRegistId.value);
        ElMessage.success('刷新成功');
    } else {
        ElMessage.warning('请先选择一位患者');
    }
}

const formatState = (state) => {
    const stateMap = {
        1: '暂存', 2: '已开立', 3: '已交费', 4: '已登记',
        5: '执行完', 6: '已退费', 0: '已作废'
    };
    return stateMap[state] || '未知';
}

function viewResult(row) { 
    // TODO
}

async function loadCommonTemplates() {
    const url = `/checktemplate/page?recordType=1&doctorId=${userStore.getUserInfo.value.id}`;
    const result = await fetchData(url);
    if (result.result) {
        templateList.value = result.data.records;
    }
}

async function useTemplate(template) {
    if (!currentRegistId.value) {
        ElMessage.error('请先选择一位患者');
        return;
    }
    const res = await fetchData(`/checkrelation/items?templateId=${template.id}`);
    if(!res.result || res.data.length === 0){
        ElMessage.error('模板内容为空');
        return;
    }

    const itemsToAdd = res.data.map(item => ({
        medicalId: currentMedicalId.value,
        registId: currentRegistId.value,
        itemId: item.id,
        name: template.name, // 申请名称使用模板名称
        itemName: item.itemName,
        price: item.price,
        objective: '',
        position: '',
        isUrgent: 0,
        num: 1,
        state: 1, // 暂存
        recordType: 1, // 检查
        doctorId: userStore.getUserInfo.value.id,
    }));

    const addResult = await postReq('/checkapply/add', itemsToAdd);
    if (addResult.data.result) {
        ElMessage.success(`已应用模板【${template.name}】`);
        loadCheckApply(currentRegistId.value);
    } else {
        ElMessage.error(addResult.data.errMsg || '应用模板失败');
    }
}

async function viewTemplate(template) {
    currentTemplateName.value = template.name;
    const result = await fetchData(`/checkrelation/items?templateId=${template.id}`);
    if(result.result){
        templateDetailItems.value = result.data;
        templateDetailDialogVisible.value = true;
    } else {
        ElMessage.error("获取模板详情失败");
    }
}

function handleSaveAsTemplate() {
    if (selectedItems.value.length === 0) {
        ElMessage.warning('请选择要存为模板的项目');
        return;
    }
    
    ElMessageBox.prompt('请输入模板名称', '存为组套', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValidator: (val) => val ? true : '模板名称不能为空',
    }).then(async ({ value }) => {
        const itemIds = selectedItems.value.map(item => item.itemId);
        const templateData = {
            name: value,
            scope: '3', // 3-个人
            recordType: 1, // 1-检查
            itemIds: itemIds,
            doctorId: userStore.getUserInfo.value.id,
            deptId: userStore.getUserInfo.value.deptID,
        };
        const result = await postReq('/checktemplate/add', templateData);
        if (result.data.result) {
            ElMessage.success('模板保存成功');
            loadCommonTemplates(); // 刷新常用模板列表
        } else {
            ElMessage.error(result.data.errMsg || '模板保存失败');
        }
    }).catch(() => {
        ElMessage.info('操作已取消');
    });
}

async function handleDeleteTemplate(template) {
    try {
        await ElMessageBox.confirm(`确定要删除模板【${template.name}】吗？`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        });
        const result = await postReq('/checktemplate/del', template);
        if (result.data.result) {
            ElMessage.success('删除成功');
            loadCommonTemplates();
        } else {
            ElMessage.error(result.data.errMsg || '删除失败');
        }
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('操作失败');
        } else {
            ElMessage.info('已取消删除');
        }
    }
}

defineExpose({
    clearList
});

</script>
<style scoped>
.toolbar {
  margin-bottom: 10px;
}
.total-amount {
  margin-bottom: 10px;
}
.template-section {
  margin-left: 20px;
}
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}
</style> 