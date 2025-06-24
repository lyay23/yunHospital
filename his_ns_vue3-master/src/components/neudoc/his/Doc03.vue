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
          <el-table :data="testApplyList" style="width: 100%" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="30" />
            <el-table-column prop="name" label="申请名称" width="120" align="center"/>
            <el-table-column prop="itemName" label="项目名称" width="180" align="center" />
            <el-table-column prop="execDept" label="执行科室"  align="center"/>
            <el-table-column prop="execState" label="执行状态" align="center" />
            <el-table-column prop="price" label="单价" align="center"/>
            <el-table-column label="检验结果" align="center">
              <template #default="scope">
                <el-link type="primary" v-if="scope.row.state === 5" @click="viewResult(scope.row)">查看结果</el-link>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
                v-if="testApplyList.length > 0"
                background
                layout="prev, pager, next"
                :total="testApplyPage.total"
                :page-size="testApplyPage.size"
                @current-change="handleTestApplyPageChange"
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
                <el-table-column label="操作" align="center">
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
  <el-dialog title="新增检验项目" v-model="addItemDialogVisible" width="60%">
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
        <el-form-item label="检验部位">
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
import { fetchData, postReq } from '../../../utils/api';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '../../../store/user.js';

const props = defineProps({
    patient: {
        type: Object,
        default: null
    }
});

const userStore = useUserStore();
const testApplyList = ref([]);
const testApplyPage = ref({ total: 0, size: 10, current: 1 });
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
        loadTestApply(newPatient.id);
        loadCommonTemplates();
    } else {
        clearList();
        templateList.value = [];
    }
}, { immediate: true });

const totalAmount = computed(() => {
  return testApplyList.value.reduce((sum, item) => sum + (item.price || 0), 0).toFixed(2);
});

async function loadTestApply(registerId, pn = 1) {
    if (registerId) {
        currentRegistId.value = registerId;
        const medRes = await fetchData(`/neudoc/getMedicalRecord?registId=${registerId}`);
        if(medRes.result && medRes.data){
            currentMedicalId.value = medRes.data.id;
        } else {
            currentMedicalId.value = null;
        }
    }
    
    if (!currentRegistId.value) return;
    // 使用 /checkapply/page 接口, 但 recordType 为 2
    const url = `/checkapply/page?registId=${currentRegistId.value}&recordType=2&pn=${pn}&count=${testApplyPage.value.size}`;
    const result = await fetchData(url);
    if(result.result) {
        testApplyList.value = result.data.records.map(item => ({...item, execState: formatState(item.state)}));
        testApplyPage.value.total = result.data.total;
        testApplyPage.value.current = result.data.current;
    } else {
        testApplyList.value = [];
        testApplyPage.value.total = 0;
    }
}

function handleTestApplyPageChange(pn) {
    loadTestApply(null, pn);
}

function clearList() {
    testApplyList.value = [];
    selectedItems.value = [];
    testApplyPage.value = { total: 0, size: 10, current: 1 };
    currentRegistId.value = null;
    currentMedicalId.value = null;
}

function handleAddItem() {
  addItemDialogVisible.value = true;
  activeTab.value = 'template';
  resetDialogState();
  loadTemplates(1);
}

function resetDialogState() {
    selectedTemplate.value = null;
    selectedFmeditem.value = null;
    templateKeyword.value = '';
    fmeditemKeyword.value = '';
    newItemForm.value = {
      name: '',
      applicationName: '',
      objective: '',
      position: '',
      isUrgent: false,
      price: 0,
    };
}

async function handleTabClick(tab) {
  resetDialogState();
  if (tab.paneName === 'template') {
    await loadTemplates(1);
  } else if (tab.paneName === 'item') {
    await loadFmeditems(1);
  }
}

async function loadTemplates(pn) {
    // 使用 /checktemplate/page 接口, recordType 为 2
    const url = `/checktemplate/page?scope=1&recordType=2&pn=${pn}&count=${templates.value.size}&keyword=${templateKeyword.value}`;
    const result = await fetchData(url);
    if (result.result) {
        templates.value = result.data;
    }
}

function handleTemplatePageChange(pn) {
    loadTemplates(pn);
}

function searchTemplates() {
    loadTemplates(1);
}

function handleTemplateSelect(row) {
    selectedTemplate.value = row;
    selectedFmeditem.value = null;
    // 假设模板中包含项目列表
    newItemForm.value.name = row.name;
    newItemForm.value.applicationName = row.name;
    newItemForm.value.price = row.price; 
}

async function loadFmeditems(pn) {
    // 注意：expclassId=72 假设是检验费用
    const url = `/fmeditem/page?expclassId=72&pn=${pn}&count=${fmeditems.value.size}&keyword=${fmeditemKeyword.value}`;
    const result = await fetchData(url);
    if (result.result) {
        fmeditems.value = result.data;
    }
}

function handleFmeditemPageChange(pn) {
    loadFmeditems(pn);
}

function searchFmeditems() {
    loadFmeditems(1);
}

function handleFmeditemSelect(row) {
    selectedFmeditem.value = row;
    selectedTemplate.value = null;
    newItemForm.value.name = row.itemName;
    newItemForm.value.applicationName = row.itemName;
    newItemForm.value.price = row.price;
}

async function confirmAddItem() {
    if (!currentMedicalId.value) {
        ElMessage.error('无有效的病历信息，无法添加项目。');
        return;
    }

    const itemData = {
        medicalId: currentMedicalId.value,
        registId: currentRegistId.value,
        state: 1, // 1 for '暂存'
        recordType: 2, // 2 for '检验'
        isUrgent: newItemForm.value.isUrgent ? 1 : 0,
        objective: newItemForm.value.objective,
        position: newItemForm.value.position,
        name: newItemForm.value.applicationName,
        num: 1, // 默认数量为1
        doctorId: userStore.getUserInfo.value.id
    };

    let itemsToSubmit = [];
    if (selectedTemplate.value) {
        //  后台需要根据模板id，查询模板明细，然后批量插入
        itemsToSubmit.push({
            ...itemData,
            itemId: selectedTemplate.value.id, 
            itemName: selectedTemplate.value.name,
            price: selectedTemplate.value.price,
            isTemplate:true //标识是模板
        });
    } else if (selectedFmeditem.value) {
        itemsToSubmit.push({
            ...itemData,
            itemId: selectedFmeditem.value.id,
            itemName: selectedFmeditem.value.itemName,
            price: selectedFmeditem.value.price,
        });
    }
    
    // 使用 /checkapply/add 接口, 后端需要一个list
    const result = await postReq('/checkapply/add', itemsToSubmit);
    if (result.data.result) {
        ElMessage.success('新增成功');
        addItemDialogVisible.value = false;
        loadTestApply(currentRegistId.value);
    } else {
        ElMessage.error(result.data.errMsg || '新增失败');
    }
}

function handleSelectionChange(val) {
    selectedItems.value = val;
}

async function handleDeleteItem() {
    if (selectedItems.value.length === 0) {
        ElMessage.warning('请选择要删除的项目');
        return;
    }
    await ElMessageBox.confirm('确定删除所选项目吗？', '提示', { type: 'warning' });
    const ids = selectedItems.value.map(item => item.id);
    // 使用 /checkapply/del 接口
    const result = await postReq('/checkapply/del', ids);
    if (result.data.result) {
        ElMessage.success('删除成功');
        loadTestApply(currentRegistId.value);
    } else {
        ElMessage.error(result.data.errMsg || '删除失败');
    }
}

async function handleCommitItem() {
    if (selectedItems.value.length === 0) {
        ElMessage.warning('请选择要开立的项目');
        return;
    }
    await ElMessageBox.confirm('确定开立所选项目吗？', '提示', { type: 'warning' });
    const ids = selectedItems.value.map(item => item.id);
    // 使用 /checkapply/updateState 接口
    const result = await postReq('/checkapply/updateState', { ids, state: 2 });
    if (result.data.result) {
        ElMessage.success('开立成功');
        loadTestApply(currentRegistId.value);
    } else {
        ElMessage.error(result.data.errMsg || '开立失败');
    }
}

async function handleCancelItem() {
    if (selectedItems.value.length === 0) {
        ElMessage.warning('请选择要作废的项目');
        return;
    }
    await ElMessageBox.confirm('确定作废所选项目吗？', '提示', { type: 'warning' });
    const ids = selectedItems.value.map(item => item.id);
    // 使用 /checkapply/updateState 接口
    const result = await postReq('/checkapply/updateState', { ids, state: 6 });
    if (result.data.result) {
        ElMessage.success('作废成功');
        loadTestApply(currentRegistId.value);
    } else {
        ElMessage.error(result.data.errMsg || '作废失败');
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
            recordType: 2, // 2-检验
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

function handleRefresh() {
    if (currentRegistId.value) {
        loadTestApply(currentRegistId.value);
        ElMessage.success('刷新成功');
    } else {
        ElMessage.warning('请先选择一位患者');
    }
}

async function loadCommonTemplates() {
    // 使用 /checktemplate/page 接口, recordType 为 2, 并传入医生ID
    const url = `/checktemplate/page?recordType=2&doctorId=${userStore.getUserInfo.value.id}`;
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
        recordType: 2, // 检验
        doctorId: userStore.getUserInfo.value.id,
    }));

    const addResult = await postReq('/checkapply/add', itemsToAdd);
    if (addResult.data.result) {
        ElMessage.success(`已应用模板【${template.name}】`);
        loadTestApply(currentRegistId.value);
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

function viewResult(item) {
    // Logic to view check result
    console.log('Viewing result for:', item);
}

function formatState(state) {
    switch(state) {
        case 1: return '暂存';
        case 2: return '已开立';
        case 3: return '已交费';
        case 4: return '已登记';
        case 5: return '已出结果';
        case 6: return '已作废';
        default: return '未知';
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
  padding-left: 10px;
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