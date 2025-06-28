<template>
  <!--   医技检查      -->
  <el-container style="height: 100%; border: 1px solid #eee">
    <!-- 页面弹窗 -->
    <el-dialog
      title="添加检查结果"
      v-model="dialogVisible"
      width="30%">
      <span>添加检查结果</span>
      <el-input placeholder="请输入内容" type="textarea" rows='3' class="input-with-select" style="width: 100%">
      </el-input>
      <el-upload
        class="upload-demo"
        ref="upload"
        action="https://localhost:8080/his/upload01"
        :on-remove="handleRemove"
        :file-list="fileList"
        :auto-upload="false">
        <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
        <div slot="tip" class="el-upload__tip">选择检查结果图像，可以上传多张；只能上传jpg/png文件，且不超过500kb</div>
      </el-upload>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
      <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">提交</el-button>
      </span>
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
          <el-button type="text" size="small" :icon="Check" @click="doCheck" :disabled="!currentCheckApply.id">执行确认</el-button>
        </el-col>
        <el-col :span="5" >
          <el-button type="text" size="small" :icon="Close" @click="cancelCheck" :disabled="!currentCheckApply.id">取消执行</el-button>
        </el-col>
        <el-col :span="5" >
          <el-button type="text" size="small" :icon="Edit" @click="dialogVisible=true" :disabled="!currentCheckApply.id">填写结果</el-button>
        </el-col>
      </el-row>
      <el-form ref="form" :model="currentCheckApply" label-width="80px" size="mini" :inline="true">
        <el-row>
          <div style="font-size:large;text-align: center" >检查明细单</div>
          <hr>
        </el-row>
        <el-row>
          <el-form-item label="病历号:" >
            <el-input type="text" size="mini" v-model="currentCheckApply.caseNumber" disabled></el-input>
          </el-form-item>
          <el-form-item label="患者姓名:" >
            <el-input type="text" size="mini" v-model="currentPatient.realName" disabled></el-input>
          </el-form-item>
          <el-form-item label="年龄:" >
            <el-input type="text" size="mini" v-model="currentPatient.age" disabled></el-input>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="结算类别:" >
            <el-input type="text" size="mini" v-model="currentCheckApply.settleCat" disabled></el-input>
          </el-form-item>
          <el-form-item label="就诊科室:" >
            <el-input type="text" size="mini" v-model="currentCheckApply.deptName" disabled></el-input>
          </el-form-item>
          <el-form-item label="处方状态:" >
            <el-input type="text" size="mini" v-model="currentCheckApply.state" disabled></el-input>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item label="收费日期" >
            <el-input type="text" size="mini" v-model="currentCheckApply.creationTime" disabled></el-input>
          </el-form-item>
          <el-form-item label="开单医生" >
            <el-input type="text" size="mini" v-model="currentCheckApply.docName" disabled></el-input>
          </el-form-item>
          <el-form-item label="发票号" >
            <el-input type="text" size="mini" v-model="currentPatient.invoiceNum" disabled></el-input>
          </el-form-item>
        </el-row>
        <el-row style="background-color: #EAF1F5">
          <el-col :span="24" style="margin-top: 4px;">
            <el-tag size="mini">检查信息：</el-tag>
          </el-col>
        </el-row>

        <el-row>
          <el-table :data="checkItems"    style="width: 100%">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="itemName" label="检查名称" >
            </el-table-column>
            <el-table-column prop="deptName" label="检查部位" >
            </el-table-column>
            <el-table-column prop="amount" label="数量" width="50px">
            </el-table-column>
            <el-table-column prop="price" label="单价" width="50px">
            </el-table-column>
            <el-table-column prop="total" label="总金额" width="70px">
            </el-table-column>
            <el-table-column prop="state" label="状态" width="100px">
               <template #default="scope">
                  {{ scope.row.state === 1 ? '已开立' : scope.row.state === 2 ? '已缴费' : scope.row.state === 3 ? '已登记' : scope.row.state === 4 ? '已检查' : scope.row.state === 5 ? '已退费' : '未知' }}
                </template>
            </el-table-column>
          </el-table>
        </el-row>
      </el-form>

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
import { ref, onMounted } from 'vue';
import { getReq, postReq } from '../../utils/api';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Check, Close, Edit } from '@element-plus/icons-vue';

// --- 数据定义 ---
const keywords = ref('');
const patients = ref([]);
const currentPatient = ref({});
const currentCheckApply = ref({});
const checkItems = ref([]);
const dialogVisible = ref(false);
const fileList = ref([]);
const upload = ref(null);

// --- 方法定义 ---

// 页面加载时执行
onMounted(() => {
  searchPatient();
});

// 1. 患者查询 - 修改为调用/register/page，并增加日期过滤
const searchPatient = async () => {
  // 获取今天的日期 YYYY-MM-DD
  const today = new Date().toISOString().split('T')[0];
  // 我们查询的是当天已缴费(2)的患者
  const result = await getReq(`/register/page?state=2&count=100&regDate=${today}`);
  // result是axios的响应，result.data是后端的JsonResult
  if (result.status === 200 && result.data.result) {
    // 适配返回的数据结构
    // 后端返回的RegisterVo是扁平结构，直接映射
    patients.value = result.data.data.records.map(r => ({
      id: r.id,
      caseNumber: r.caseNumber,
      realName: r.realName,
      age: r.age,
      // ... 其他需要映射的字段
    }));
  } else {
    ElMessage.error('查询患者失败');
  }
};

// 2. 选择患者，加载检查申请
const selectPatient = async (patient) => {
  if (!patient) return;
  currentPatient.value = patient;
  // 这里registId使用的是挂号记录的ID
  const result = await getReq(`/neudoc/checkapply/getCheckApply?registId=${patient.id}`);
  if (result.code === 200 && result.data && result.data.checkApply) {
    currentCheckApply.value = result.data.checkApply;
    checkItems.value = result.data.checkDetail;

    // 格式化状态
    currentCheckApply.value.state = formatState(currentCheckApply.value.state);

  } else {
    currentCheckApply.value = {};
    checkItems.value = [];
    ElMessage.info('该患者没有待处理的检查申请');
  }
};

// 3. 执行确认
const doCheck = () => {
  ElMessageBox.confirm('确定要执行这些检查吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const result = await postReq('/neudoc/checkapply/doCheck', { id: currentCheckApply.value.id });
    if (result.code === 200) {
      ElMessage.success('执行成功');
      // 刷新数据
      selectPatient(currentPatient.value);
    } else {
      ElMessage.error(result.message || '执行失败');
    }
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

// 4. 取消执行
const cancelCheck = () => {
    ElMessageBox.confirm('确定要取消执行吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const result = await postReq('/neudoc/checkapply/cancelCheck', { id: currentCheckApply.value.id });
    if (result.code === 200) {
      ElMessage.success('取消成功');
      // 刷新数据
      selectPatient(currentPatient.value);
    } else {
      ElMessage.error(result.message || '取消失败');
    }
  }).catch(() => {
    ElMessage.info('已取消操作');
  });
};

// 结果录入相关
const submitUpload = () => {
  upload.value.submit();
  dialogVisible.value = false;
  ElMessage.success('提交结果成功');
};

const handleRemove = (file) => {
    ElMessage.info(`移除图片 ${file.name}`);
};

// 辅助函数
const formatState = (state) => {
  switch (state) {
    case 1: return '已开立';
    case 2: return '已缴费';
    case 3: return '已登记';
    case 4: return '已检查';
    case 5: return '已退费';
    default: return '未知状态';
    }
  }
</script>
