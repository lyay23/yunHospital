<template>
  <!--   医技检查      -->
  <el-container style="height: 100%; border: 1px solid #eee">
    <!-- 页面弹窗 -->
    <el-dialog
      title="添加检查结果"
      v-model="dialogVisible"
      width="50%">
      <span>结果描述：</span>
      <el-input v-model="resultDesc" placeholder="请输入结果描述" type="textarea" rows='3' class="input-with-select" style="width: 100%"/>
      <br/><br/>
      <span>结果图片：</span>
      <el-upload
        ref="upload"
        :action="uploadUrl"
        list-type="picture-card"
        :on-preview="handlePictureCardPreview"
        :on-remove="handleRemove"
        :on-success="handleUploadSuccess"
        :file-list="fileList"
        :multiple="true"
        :headers="uploadHeaders">
        <el-icon><Plus /></el-icon>
      </el-upload>
      <el-dialog v-model="previewVisible">
        <img w-full :src="previewImageUrl" alt="Preview Image" style="width: 100%"/>
      </el-dialog>

      <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button style="margin-left: 10px;" size="small" type="success" @click="submitResult">提 交</el-button>
      </span>
      </template>
    </el-dialog>
    <!-- 页面正文 -->
    <el-aside width="350px">
      <el-container >
        <el-header >
          <el-input placeholder="请输入病历号/姓名" v-model="keywords" class="input-with-select" style="width: 100%">
            <template #prepend>患者查询：</template>
            <template #append>
              <el-button :icon="Search" @click="searchPatient"></el-button>
            </template>
          </el-input>
        </el-header>
        <el-main style="height: 700px; border: 1px solid #eee ;padding: 0;margin: 0">
          <el-tabs type="border-card" >
            <el-tab-pane label="患者检查" >
              <el-tag>患者：</el-tag>
              <el-table ref="singleTable" :data="patients" highlight-current-row @current-change="selectPatient"
                        style="width: 100%" :show-header="false" size="mini">
                <el-table-column property="caseNumber" label="病历号" >
                </el-table-column>
                <el-table-column property="realName" label="姓名" width="80">
                </el-table-column>
                <el-table-column property="id" label="id" width="100">
                </el-table-column>
              </el-table><br/><br/>

            </el-tab-pane>
          </el-tabs>
        </el-main>
      </el-container>
    </el-aside>

    <el-container direction="vertical" style="height: 100%">
      <el-row style="background-color: #EAF1F5">
        <el-col :span="9" style="margin-top: 4px;">
          <el-tag size="mini">检查明细信息：</el-tag>
        </el-col>
        <el-col :span="5" >
          <el-button type="text" size="small" :icon="Check" @click="handleExecute" :disabled="!isExecuteEnabled">执行确认</el-button>
        </el-col>
        <el-col :span="5" >
          <el-button type="text" size="small" :icon="Close" @click="handleCancelExecute" :disabled="!isCancelEnabled">取消执行</el-button>
        </el-col>
        <el-col :span="5" >
          <el-button type="text" size="small" :icon="Edit" @click="openResultDialog" :disabled="!isResultEnabled">填写结果</el-button>
        </el-col>
      </el-row>
      <el-form ref="form" :model="currentCheckApply" label-width="80px" size="mini" :inline="true">
        <el-row>
          <div style="font-size:large;text-align: center" >检查明细单</div>
          <hr>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="病历号:">
              <el-input v-model="currentCheckApply.caseNumber" readonly />
          </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="患者姓名:">
              <el-input v-model="currentCheckApply.realName" readonly />
          </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="年龄:">
              <el-input v-model="currentCheckApply.age" readonly />
          </el-form-item>
          </el-col>
        </el-row>
        <el-row>
      
          <el-col :span="6">
            <el-form-item label="就诊科室:">
              <el-input v-model="currentCheckApply.deptName" readonly />
          </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="处方状态:">
              <el-input :value="formatState(currentCheckApply.state)" readonly />
          </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="收费日期:">
              <el-input v-model="currentCheckApply.registTime" readonly />
          </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="开单医生:">
              <el-input v-model="currentCheckApply.doctorName" readonly />
          </el-form-item>
          </el-col>
    
        </el-row>
      </el-form>

      <el-divider content-position="left">检查信息:</el-divider>
      <el-table :data="checkItems" stripe style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="itemName" label="检查名称" />
        <el-table-column prop="position" label="检查部位" />
        <el-table-column prop="num" label="数量" />
        <el-table-column prop="price" label="单价" />
        <el-table-column prop="totalAmount" label="总金额" />
        <el-table-column prop="state" label="状态">
          <template #default="scope">
            <span>{{ formatState(scope.row.state) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-container>
  </el-container>
</template>
<style scoped>
  *{
    font-size: 14px;
    font-weight: 500;
    font-family: "Microsoft YaHei","微软雅黑","Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB",Arial,sans-serif;;
  }
</style>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { getReq, postReq } from '../../utils/api';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Check, Close, Edit, Plus } from '@element-plus/icons-vue';

// --- 数据定义 ---
const keywords = ref('');
const patients = ref([]);
const currentPatient = ref({});
const currentCheckApply = ref({});
const checkItems = ref([]);
const selectedItems = ref([]);

// 弹窗相关
const dialogVisible = ref(false);
const upload = ref(null);
const resultDesc = ref('');
const fileList = ref([]); // 用于el-upload显示
const uploadedImageUrls = ref([]); // 用于存储上传成功的URL
const uploadUrl = ref('/api/upload'); // 你的上传地址
const uploadHeaders = ref({'token': sessionStorage.getItem('token') || ''});
const previewImageUrl = ref('');
const previewVisible = ref(false);

// --- 计算属性 ---
// "执行确认"按钮是否可用
const isExecuteEnabled = computed(() => {
  return selectedItems.value.length > 0 && selectedItems.value.every(item => item.state === 2); // 只能操作已开立
});
// "取消执行"按钮是否可用
const isCancelEnabled = computed(() => {
  return selectedItems.value.length > 0 && selectedItems.value.every(item => item.state === 4); // 只能操作已登记
});
// "填写结果"按钮是否可用
const isResultEnabled = computed(() => {
  return selectedItems.value.length > 0 && selectedItems.value.every(item => item.state === 4); // 只能操作已登记
});

// --- 方法定义 ---

// 页面加载时执行
onMounted(() => {
  searchPatient();
});

// 1. 患者查询
const searchPatient = async () => {
  const today = new Date().toISOString().split('T')[0];
  const result = await getReq(`/register/page?count=100&regDate=${today}`);
  if (result.status === 200 && result.data.result) {
    patients.value = result.data.data.records.map(r => ({
      id: r.id, caseNumber: r.caseNumber, realName: r.realName, age: r.age,
    }));
  } else {
    ElMessage.error('查询患者失败');
  }
};

// 2. 选择患者，加载检查申请
const selectPatient = async (patient) => {
  if (!patient) return;
  currentPatient.value = patient;
  const result = await getReq(`/checkapply/getCheckApply?registId=${patient.id}`);
  if (result.status === 200 && result.data.result && result.data.data && result.data.data.checkApply) {
    currentCheckApply.value = result.data.data.checkApply;
    // 添加客户端过滤，只显示检查项目 (recordType = 1)
    if (result.data.data.checkDetail) {
      checkItems.value = result.data.data.checkDetail.filter(item => item.recordType === 1);
    } else {
      checkItems.value = [];
    }
  } else {
    currentCheckApply.value = {};
    checkItems.value = [];
  }
};

const handleSelectionChange = (selection) => {
  selectedItems.value = selection;
};

// 3. 执行确认
const handleExecute = async () => {
  const ids = selectedItems.value.map(item => item.id);
  const res = await postReq('/checkapply/execute', ids);
  if (res.status === 200 && res.data.result) {
    ElMessage.success('执行成功');
    selectPatient(currentPatient.value);
  } else {
    ElMessage.error(res.data.errMsg || '执行失败');
  }
};

// 4. 取消执行
const handleCancelExecute = async () => {
  const ids = selectedItems.value.map(item => item.id);
  const res = await postReq('/checkapply/cancel-execute', ids);
  if (res.status === 200 && res.data.result) {
    ElMessage.success('取消成功');
    selectPatient(currentPatient.value);
  } else {
    ElMessage.error(res.data.errMsg || '取消失败');
  }
};

// 5. 结果录入相关
const openResultDialog = () => {
  // 清空上次的结果
  resultDesc.value = '';
  fileList.value = [];
  uploadedImageUrls.value = [];
  dialogVisible.value = true;
};

const submitResult = async () => {
  const payload = {
    checkApplyIds: selectedItems.value.map(item => item.id),
    registId: currentPatient.value.id,
    resultDesc: resultDesc.value,
    resultImages: uploadedImageUrls.value.join(','),
  };

  const res = await postReq('/checkapply/save-result', payload);
  if (res.status === 200 && res.data.result) {
    ElMessage.success('提交结果成功');
    dialogVisible.value = false;
    selectPatient(currentPatient.value); // 刷新列表
  } else {
    ElMessage.error(res.data.errMsg || '提交失败');
  }
};

const handleRemove = (file, fileList) => {
  // 从已上传URL列表中移除
  const index = uploadedImageUrls.value.indexOf(file.response?.data || file.url);
  if (index > -1) {
    uploadedImageUrls.value.splice(index, 1);
  }
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
    // 从文件列表中移除上传失败的文件
    const index = fileList.findIndex(f => f.uid === file.uid);
    if(index > -1) {
      fileList.splice(index, 1);
    }
  }
};

// 辅助函数
const formatState = (state) => {
  if (state === 0) return '已作废';
  if (state === 1) return '暂存';
  if (state === 2) return '已开立';
  if (state === 3) return '已收费';
  if (state === 4) return '已登记';
  if (state === 5) return '执行完';
  if (state === 6) return '已退费';
  return '未知';
};
</script>
