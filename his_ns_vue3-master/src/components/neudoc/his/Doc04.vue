<template>
  <el-card class="box-card">
    <template #header>
      <div class="card-header">
        <span>门诊确诊</span>
        <el-button type="primary" :disabled="!patient || !patient.id || isDiagnosed" @click="saveAll" :icon="DocumentChecked">确诊保存</el-button>
      </div>
    </template>
    <div v-if="!patient || !patient.id">
      <el-empty description="请先在左侧选择一位患者" />
    </div>
    <div v-else>
      <!-- 病历信息表单 -->
      <el-form :model="medicalRecord" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="病历号">
              <el-input v-model="patient.caseNumber" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="患者姓名">
              <el-input v-model="patient.realName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="年龄">
              <el-input v-model="patient.age" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">病历信息</el-divider>
        <el-form-item label="初步诊断">
          <el-input v-model="medicalRecord.diagnosis" type="textarea" :rows="3" placeholder="医生根据患者情况，得出的初步诊断结果" :disabled="isDiagnosed" />
        </el-form-item>
        <el-form-item label="处理意见">
          <el-input v-model="medicalRecord.handling" type="textarea" :rows="3" placeholder="医生对病情的处理意见" :disabled="isDiagnosed" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">最终诊断</el-divider>
      
      <!-- 操作按钮 -->
      <div class="action-bar" style="margin-bottom: 10px;">
        <el-button type="primary" @click="openAddDialog" :icon="Plus" :disabled="isDiagnosed">添加诊断</el-button>
        <el-button type="danger" @click="handleDeleteBatch" :icon="Delete" :disabled="isDiagnosed">删除诊断</el-button>
      </div>

      <!-- 最终诊断列表 -->
      <el-table :data="finalDiagnoses" @selection-change="handleSelectionChange" border :row-style="{ cursor: isDiagnosed ? 'default' : 'pointer' }">
        <el-table-column type="selection" width="55" align="center" :selectable="() => !isDiagnosed" />
        <el-table-column prop="disease.diseaseCode" label="疾病编码" width="120" />
        <el-table-column prop="disease.diseaseName" label="疾病名称" />
        <el-table-column label="发病日期" width="220">
          <template #default="scope">
            <el-date-picker
              v-model="scope.row.siskDate"
              type="datetime"
              placeholder="选择发病日期"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 100%"
              :disabled="isDiagnosed"
            />
          </template>
        </el-table-column>
        <el-table-column label="诊断状态" width="120">
          <template #default="scope">
            <el-select v-model="scope.row.diagnoseCate" placeholder="请选择" :disabled="isDiagnosed">
              <el-option label="疑诊" :value="1"></el-option>
              <el-option label="确诊" :value="2"></el-option>
            </el-select>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加诊断对话框 -->
    <el-dialog title="添加诊断" v-model="dialogVisible" width="60%">
      <el-input v-model="searchKeyword" placeholder="按疾病名称或编码搜索" @keyup.enter="loadDiseases">
        <template #append>
          <el-button :icon="Search" @click="() => { currentPage = 1; loadDiseases(); }"></el-button>
        </template>
      </el-input>
      <el-table ref="diseaseTableRef" :data="diseaseList" @selection-change="handleDiseaseSelectionChange" height="300px" style="margin-top: 15px;">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="diseaseCode" label="疾病编码" width="120" />
        <el-table-column prop="diseaseName" label="疾病名称" />
        <el-table-column prop="diseCategory.diseCategoryName" label="疾病分类" />
      </el-table>
      <el-pagination
        style="margin-top: 15px;"
        layout="prev, pager, next, jumper, ->, total"
        :total="total"
        :current-page="currentPage"
        :page-size="pageSize"
        @current-change="handlePageChange"
      />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addSelectedDiseases">确认添加</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, watch, onMounted, defineProps } from 'vue';
import { get, post, put, del } from '@/utils/api';
import { ElMessage, ElMessageBox, ElTable } from 'element-plus';
import { DocumentChecked, Plus, Delete, Search } from '@element-plus/icons-vue';
import { useUserStore } from '@/store/user';
import dayjs from 'dayjs';

const props = defineProps({
  patient: {
    type: Object,
    required: true,
  },
  isDiagnosed: {
    type: Boolean,
    default: false
  }
});

const medicalRecord = ref({});
const finalDiagnoses = ref([]);
const tableSelection = ref([]); // 用于最终诊断列表的多选

// --- 数据加载 ---
const loadData = async () => {
  if (!props.patient.id) return;

  // 1. 加载病历信息
  const mrRes = await get('/neudoc/medicalrecord/getByRegistId', { registId: props.patient.id });
  if (mrRes.data && mrRes.data.data) {
    medicalRecord.value = mrRes.data.data;
    // 如果返回的数据直接包含了诊断列表，就用它
    if (mrRes.data.data.medicalDiseases) {
      finalDiagnoses.value = mrRes.data.data.medicalDiseases;
    } else if (medicalRecord.value.id) {
      // 否则，如果病历存在，就单独请求诊断列表
      const fdRes = await get('/neudoc/medicaldisease/list', { medicalId: medicalRecord.value.id, diagnoseType: 2 });
      finalDiagnoses.value = fdRes.data.data || [];
    } else {
      finalDiagnoses.value = [];
    }
  } else {
    // 如果没有病历，可以初始化一个空对象
    medicalRecord.value = {
        registId: props.patient.id,
        diagnosis: '',
        handling: '',
    };
    finalDiagnoses.value = [];
  }
};

watch(() => props.patient, (newPatient) => {
  if (newPatient && newPatient.id) {
    loadData();
  } else {
    medicalRecord.value = {};
    finalDiagnoses.value = [];
  }
}, { immediate: true, deep: true });

// --- 最终诊断表格操作 ---
const handleSelectionChange = (selection) => {
  tableSelection.value = selection;
};

const handleDeleteBatch = async () => {
  if (tableSelection.value.length === 0) {
    ElMessage.warning('请至少选择一项进行删除');
    return;
  }

  await ElMessageBox.confirm('确定要删除选中的诊断吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  });

  const toDeleteIds = tableSelection.value
    .filter(item => item.id) // 只删除已经保存在数据库中的记录
    .map(item => item.id);

  if (toDeleteIds.length > 0) {
    try {
      // 假设后端有一个批量删除接口，或者循环调用单个删除
      // 这里我们用循环调用单个删除接口作为示例
      for (const id of toDeleteIds) {
        await del(`/neudoc/medicaldisease/del/${id}`);
      }
    } catch (error) {
      ElMessage.error('删除失败，请重试');
      return; // 如果API调用失败，则中止
    }
  }

  // 从前端列表中移除所有选中的项（无论是否已保存）
  const selectionSet = new Set(tableSelection.value.map(item => item.diseaseId));
  finalDiagnoses.value = finalDiagnoses.value.filter(item => !selectionSet.has(item.diseaseId));
  
  ElMessage.success('删除成功');
};


// --- 添加诊断对话框逻辑 ---
const dialogVisible = ref(false);
const searchKeyword = ref('');
const diseaseList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const diseaseTableRef = ref(null);
const dialogSelection = ref([]); // 用于对话框中疾病列表的多选

const openAddDialog = () => {
  dialogVisible.value = true;
  searchKeyword.value = '';
  currentPage.value = 1;
  loadDiseases();
};

const loadDiseases = async () => {
  const params = {
    pn: currentPage.value,
    count: pageSize.value,
    keyword: searchKeyword.value,
  };
  // 修正API路径和参数
  const res = await get('/disease/page', params);
  // 后端返回的数据结构是 { data: { records: [], total: 0 } }
  if(res.data && res.data.data){
    diseaseList.value = res.data.data.records;
    total.value = res.data.data.total;
  } else {
    diseaseList.value = [];
    total.value = 0;
  }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  loadDiseases();
};

const handleDiseaseSelectionChange = (selection) => {
  dialogSelection.value = selection;
};

const addSelectedDiseases = () => {
  const existingDiseaseIds = new Set(finalDiagnoses.value.map(d => d.diseaseId));
  
  dialogSelection.value.forEach(disease => {
    if (!existingDiseaseIds.has(disease.id)) {
      finalDiagnoses.value.push({
        // 前端统一使用一套字段，在保存时映射到后端
        disease: disease,
        diseaseId: disease.id,
        siskDate: dayjs().format('YYYY-MM-DD HH:mm:ss'), // 用于日期选择器 v-model, 直接使用后端字段名
        diagnoseCate: 2, // 用于下拉框 v-model, 直接使用后端字段名, 默认确诊
        diagnoseType: 2, // 终诊标志
      });
      existingDiseaseIds.add(disease.id); // 更新set防止重复添加
    }
  });
  dialogVisible.value = false;
  ElMessage.success(`已添加 ${dialogSelection.value.length} 项诊断`);
};

// --- 保存逻辑 ---
const saveAll = async () => {
  if (!props.patient || !props.patient.id) {
    ElMessage.error("请先选择一位患者");
    return;
  }
  
  try {
    // 构建一个符合后端MedicalRecordVo的payload
    const payload = {
      ...medicalRecord.value,
      registId: props.patient.id,
      caseNumber: props.patient.caseNumber,
      // 直接从 finalDiagnoses 中取值，因为字段名已经匹配
      medicalDiseases: finalDiagnoses.value,
    };
    
    const res = await post('/neudoc/medicalrecord/save', payload);

    if (res.data && res.data.result) {
      // 确诊信息保存成功后，调用诊毕接口
      try {
        const finishRes = await post(`/register/finish?id=${props.patient.id}`);
        if (finishRes.data && finishRes.data.result) {
            ElMessage.success('患者已诊毕，病历已锁定');
        } else {
            ElMessage.error(finishRes.data.errMsg || '标记诊毕失败');
        }
      } catch (finishError) {
        ElMessage.error('调用诊毕接口时发生网络或服务器错误');
      }

      // 保存后重新加载数据，以获取新ID等信息
      await loadData();
    } else {
       ElMessage.error(res.data.errMsg || '保存失败');
    }
  } catch (error) {
    console.error("保存失败:", error);
    ElMessage.error('保存过程中发生网络或服务器错误');
  }
};

</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.box-card {
  margin: 10px;
}
</style> 