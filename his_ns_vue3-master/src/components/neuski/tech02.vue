<template>
  <el-container style="height: 100%; border: 1px solid #eee">
    <!-- 弹窗 -->
    <el-dialog title="填写检验结果" v-model="dialogVisible" width="50%">
      <span>结果描述：</span>
      <el-input v-model="resultDesc" placeholder="请输入结果描述" type="textarea" rows="3"/>
      <br/><br/>
      <span>结果图片：</span>
      <el-upload ref="upload" :action="uploadUrl" list-type="picture-card" :on-preview="handlePictureCardPreview" :on-remove="handleRemove" :on-success="handleUploadSuccess" :file-list="fileList" :multiple="true" :headers="uploadHeaders">
        <el-icon><Plus /></el-icon>
      </el-upload>
      <el-dialog v-model="previewVisible">
        <img w-full :src="previewImageUrl" alt="Preview Image" style="width: 100%"/>
      </el-dialog>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false" size="small">取消</el-button>
          <el-button style="margin-left: 10px;" size="small" type="success" @click="submitResult">提交</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 左侧患者列表 -->
    <el-aside width="350px" class="aside-card">
      <el-container>
        <el-header class="search-header">
          <el-input placeholder="请输入病历号/姓名" v-model="keywords" class="input-with-select search-input" style="width: 100%">
            <template #prepend>患者查询：</template>
            <template #append><el-button :icon="Search" @click="searchPatient"></el-button></template>
          </el-input>
        </el-header>
        <el-main class="patient-list-main" style="height: 700px; border: 1px solid #eee; padding: 0; margin: 0">
          <el-tabs type="border-card">
            <el-tab-pane label="医技检验">
              <el-tag>患者：</el-tag>
              <el-table ref="singleTable" :data="patients" highlight-current-row @current-change="selectPatient" style="width: 100%" :show-header="false" size="mini" class="patient-table">
                <el-table-column property="caseNumber" label="病历号"/>
                <el-table-column property="realName" label="姓名" width="80"/>
                <el-table-column property="id" label="id" width="100"/>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-main>
      </el-container>
    </el-aside>

    <!-- 右侧内容 -->
    <el-container direction="vertical" class="main-card" style="height: 100%">
      <el-row style="background-color: #EAF1F5">
        <el-col :span="9" style="margin-top: 4px;">
          <el-tag size="mini">检验明细信息：</el-tag>
        </el-col>
        <el-col :span="5">
          <el-button type="text" size="small" :icon="Check" @click="handleExecute" :disabled="!isExecuteEnabled">执行确认</el-button>
        </el-col>
        <el-col :span="5">
          <el-button type="text" size="small" :icon="Close" @click="handleCancelExecute" :disabled="!isCancelEnabled">取消执行</el-button>
        </el-col>
        <el-col :span="5">
          <el-button type="text" size="small" :icon="Edit" @click="openResultDialog" :disabled="!isResultEnabled">填写结果</el-button>
        </el-col>
      </el-row>
      <el-form ref="form" :model="currentCheckApply" label-width="80px" size="mini" :inline="true" class="detail-form">
        <el-row><div style="font-size:large;text-align: center">检验明细单</div><hr/></el-row>
        <el-row>
            <el-col :span="8"><el-form-item label="病历号:"><el-input v-model="currentCheckApply.caseNumber" readonly/></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="患者姓名:"><el-input v-model="currentCheckApply.patientName" readonly/></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="年龄:"><el-input v-model="currentCheckApply.age" readonly/></el-form-item></el-col>
        </el-row>
        <el-row>
            
            <el-col :span="8"><el-form-item label="就诊科室:"><el-input v-model="currentCheckApply.deptName" readonly/></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="处方状态:"><el-input :value="formatState(currentCheckApply.state)" readonly/></el-form-item></el-col>
        </el-row>
        <el-row>
            <el-col :span="8"><el-form-item label="收费日期:"><el-input v-model="currentCheckApply.creationTime" readonly/></el-form-item></el-col>
            <el-col :span="8"><el-form-item label="开单医生:"><el-input v-model="currentCheckApply.doctorName" readonly/></el-form-item></el-col>
           
        </el-row>
    </el-form>

      <el-divider content-position="left">检验信息:</el-divider>
      <el-table :data="checkItems" stripe style="width: 100%" @selection-change="handleSelectionChange" class="detail-table">
        <el-table-column type="selection" width="55"/>
        <el-table-column prop="itemName" label="检验名称"/>
        <el-table-column prop="position" label="检验部位"/>
        <el-table-column prop="quantity" label="数量" />
        <el-table-column prop="price" label="单价"/>
        <el-table-column prop="totalAmount" label="总金额" />
        <el-table-column prop="state" label="状态"><template #default="scope"><span>{{ formatState(scope.row.state) }}</span></template></el-table-column>
      </el-table>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { getReq, postReq, putReq } from '../../utils/api';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Check, Close, Edit, Plus } from '@element-plus/icons-vue';
import { formatDateTime } from '../../utils/utils';

const keywords = ref('');
const patients = ref([]);
const currentPatient = ref({});
const currentCheckApply = ref({});
const checkItems = ref([]);
const selectedItems = ref([]);

const dialogVisible = ref(false);
const upload = ref(null);
const resultDesc = ref('');
const fileList = ref([]);
const uploadedImageUrls = ref([]);
const uploadUrl = ref('/api/upload');
const uploadHeaders = ref({'token': sessionStorage.getItem('token') || ''});
const previewImageUrl = ref('');
const previewVisible = ref(false);

const isExecuteEnabled = computed(() => selectedItems.value.length > 0 && selectedItems.value.every(item => item.state === 2 || item.state === 3));
const isCancelEnabled = computed(() => selectedItems.value.length > 0 && selectedItems.value.every(item => item.state === 4));
const isResultEnabled = computed(() => selectedItems.value.length > 0 && selectedItems.value.every(item => item.state === 4));

onMounted(() => {
  searchPatient();
});

const searchPatient = async () => {
  const today = new Date().toISOString().split('T')[0];
  const result = await getReq(`/register/page?count=100&visitState=2&regDate=${today}`);
  if (result.status === 200 && result.data.result) {
    patients.value = result.data.data.records;
  } else {
    patients.value = [];
    ElMessage.error('查询今日患者失败');
  }
};

const selectPatient = async (patient) => {
  if (!patient) {
    currentPatient.value = null;
    currentCheckApply.value = {};
    checkItems.value = [];
    return;
  }
  currentPatient.value = patient;

  const result = await getReq(`/checkapply/page`, { registId: patient.id, recordType: 2, count: 200 });
  if (result.status === 200 && result.data.result) {
    const records = result.data.data.records;
    
    checkItems.value = records.map(item => ({
        ...item,
        quantity: Number(item.quantity) || 1,
        totalAmount: (Number(item.price) || 0) * (Number(item.quantity) || 1),
    }));

    if (records.length > 0) {
      const firstItem = records[0];
      currentCheckApply.value = {
        ...firstItem,
        caseNumber: patient.caseNumber,
        patientName: patient.realName,
        age: patient.age,
        creationTime: firstItem.creationTime ? formatDateTime(firstItem.creationTime) : '',
        settleCategoryName: firstItem.settleCategoryName,
        deptName: firstItem.deptName,
        doctorName: firstItem.doctorName,
        invoiceNo: firstItem.invoiceNo,
      };
    } else {
      currentCheckApply.value = {};
    }
  } else {
    currentCheckApply.value = {};
    checkItems.value = [];
  }
};

const handleSelectionChange = (selection) => {
  selectedItems.value = selection;
};

const updateItemsState = async (ids, state) => {
  const res = await postReq('/checkapply/updateState', { ids, state });
  if (res.data.result) {
    // 本地更新状态
    ids.forEach(id => {
      const item = checkItems.value.find(i => i.id === id);
      if (item) item.state = state;
    });
    selectedItems.value = []; // 清空选择
    return true;
  } else {
    ElMessage.error(res.data.errMsg || '操作失败');
    return false;
  }
};

const handleExecute = async () => {
  const ids = selectedItems.value.map(item => item.id);
  // 直接调用后端更新，成功后重新加载数据
  const res = await postReq('/checkapply/updateState', { ids, state: 4 }); // 4: 已登记
  if (res.data.result) {
    ElMessage.success('执行确认成功');
    // 重新加载当前患者的检验列表以刷新所有状态
    if (currentPatient.value?.id) {
      await selectPatient(currentPatient.value);
    }
  } else {
    ElMessage.error(res.data.errMsg || '执行确认失败');
  }
};

const handleCancelExecute = async () => {
  const ids = selectedItems.value.map(item => item.id);
  const success = await updateItemsState(ids, 0); // 0: 已作废
  if (success) ElMessage.success('取消执行成功');
};

const openResultDialog = () => {
  resultDesc.value = '';
  fileList.value = [];
  uploadedImageUrls.value = [];
  dialogVisible.value = true;
};

const submitResult = async () => {
  const ids = selectedItems.value.map(item => item.id);
  const payload = {
    checkApplyIds: ids,
    registId: currentPatient.value.id,
    resultDesc: resultDesc.value,
    resultImages: uploadedImageUrls.value.join(','),
  };

  const res = await postReq('/checkapply/save-result', payload);
  if (res.data.result) {
    ElMessage.success('提交结果成功');
    dialogVisible.value = false;
    // 本地更新状态为 5: 执行完
    await updateItemsState(ids, 5);
  } else {
    ElMessage.error(res.data.errMsg || '提交失败');
  }
};

const handleRemove = (file) => {
  const urlToRemove = file.response ? file.response.data : file.url;
  const index = uploadedImageUrls.value.indexOf(urlToRemove);
  if (index > -1) uploadedImageUrls.value.splice(index, 1);
};

const handlePictureCardPreview = (file) => {
  previewImageUrl.value = file.url;
  previewVisible.value = true;
};

const handleUploadSuccess = (response, file, fileList) => {
  if (response.result) {
    uploadedImageUrls.value.push(response.data);
  } else {
    ElMessage.error(response.errMsg || '图片上传失败');
    fileList.splice(fileList.findIndex(f => f.uid === file.uid), 1);
  }
};

const formatState = (state) => {
  const stateMap = {0: '已作废', 1: '暂存', 2: '已开立', 3: '已交费', 4: '已登记', 5: '执行完', 6: '已退费'};
  return stateMap[state] || '未知';
};
</script>

<style scoped>
*{
  font-size: 14px;
  font-weight: 500;
  font-family: "Microsoft YaHei","微软雅黑","Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB",Arial,sans-serif;
}

/* 侧边栏卡片美化 */
.aside-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  padding: 18px 10px 10px 10px;
  margin: 18px 10px 18px 0;
  min-height: 90vh;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.search-header {
  padding-bottom: 8px;
  background: none;
  border: none;
}
.search-input .el-input__wrapper {
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(64,158,255,0.08);
  border: 1px solid #e0eaff;
  background: #f8fafc;
}
.search-input .el-input__inner {
  font-size: 15px;
}
.search-input .el-button {
  border-radius: 8px;
  background: #409EFF;
  color: #fff;
}
.patient-list-main {
  padding: 0;
  background: none;
}
.patient-table {
  border-radius: 10px;
  overflow: hidden;
  font-size: 15px;
}
.patient-table tr {
  transition: background 0.2s;
}
.patient-table tr:hover td {
  background: #e6f0ff !important;
}

/* 主内容区卡片美化 */
.main-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  padding: 24px 24px 16px 24px;
  margin: 18px 0 18px 0;
  min-height: 90vh;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.detail-form .el-input__wrapper {
  border-radius: 8px;
  border: 1px solid #e0eaff;
  background: #f8fafc;
  font-size: 15px;
}
.detail-form .el-form-item__label {
  font-size: 15px;
  font-weight: 600;
  color: #2c3e50;
}
.detail-table {
  border-radius: 10px;
  overflow: hidden;
  font-size: 15px;
  margin-top: 10px;
}
.detail-table th {
  background: #f5f7fa !important;
  font-weight: 600;
  color: #666;
  font-size: 14px;
  padding: 6px 0;
}
.detail-table td {
  background: #f8fafc;
  border-radius: 8px;
  font-size: 15px;
  color: #222;
  padding: 6px 0;
  transition: background 0.2s;
}
.detail-table tr:hover td {
  background: #e6f0ff !important;
}
.el-button[type="text"] {
  color: #409EFF;
  font-weight: 600;
  border-radius: 8px;
  transition: background 0.2s;
}
.el-button[type="text"]:hover {
  background: #f4f8ff;
}
</style>
