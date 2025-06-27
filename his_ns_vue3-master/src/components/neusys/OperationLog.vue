<template>
  <div class="operation-log-container">
    <el-card>
      <div style="margin-bottom: 16px; display: flex; align-items: center;">
        <el-input v-model="searchKeyword" placeholder="请输入操作用户或表名" style="width: 240px; margin-right: 8px;" clearable />
        <el-button type="primary" @click="fetchLogs">查询</el-button>
      </div>
      <el-table :data="logs" border stripe style="width: 100%">
        <el-table-column label="序号" width="80" type="index" :index="indexMethod"/>
        <el-table-column prop="userName" label="登录名" width="120"/>
        <el-table-column prop="realName" label="操作用户" width="120"/>
        <el-table-column prop="operationTime" label="操作时间" width="180"/>
        <el-table-column label="操作方法" min-width="160">
          <template #default="scope">
            <span>{{ getChineseAction(scope.row) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          background
          layout="prev, pager, next, sizes, total"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
          :page-sizes="[10, 20, 50, 100]"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { getReq } from '../../utils/api'
import { ElMessage } from 'element-plus'

const logs = ref([])
const total = ref(0)
const pageSize = ref(10)
const currentPage = ref(1)
const searchKeyword = ref('')

const getChineseAction = (row) => {
  const { className, methodName } = row;

  // 统一处理方法名，例如将addUser, add, save都视为"新增"
  let action = '未知操作';
  if (methodName.toLowerCase().startsWith('add') || methodName.toLowerCase().startsWith('save')) {
    action = '新增';
  } else if (methodName.toLowerCase().startsWith('update')) {
    action = '更新';
  } else if (methodName.toLowerCase().startsWith('del')) {
    action = '删除';
  } else if (methodName.toLowerCase().startsWith('finish')) {
    action = '诊毕';
  }

  const controllerName = className.substring(className.lastIndexOf('.') + 1);
  const controllerMap = {
      'CheckApplyController': '检查申请',
      'CheckTemplateController': '检查模板',
      'DrugsController': '药品',
      'DrugsTemplateController': '药品模板',
      'MedicalDiseaseController': '诊断',
      'MedicalRecordController': '病历',
      'PrescriptionController': '处方',
      'PrescriptionDetailedController': '处方明细',
      'CustomerController': '患者',
      'InvoiceController': '发票',
      'MedicalCardController': '就诊卡',
      'PatientcostsController': '患者费用',
      'RegisterController': '挂号',
      'ConstantItemController': '常数项',
      'ConstantTypeController': '常量类型',
      'DepartmentController': '科室',
      'DiseaseController': '疾病目录',
      'DisecategoryController': '疾病分类',
      'ExpenseclassController': '费用科目',
      'FmeditemController': '非药品项目',
      'RegistlevelController': '挂号级别',
      'RuleController': '排班规则',
      'SchedulingController': '排班计划',
      'SettlecategoryController': '结算类别',
      'UserController': '用户',
      'AIChatController': 'AI助手'
  };

  const resource = controllerMap[controllerName] || controllerName;

  // 对于查询、登录、登出等通用操作，直接显示操作名
  if (['查询', '登录', '登出', '诊毕'].includes(action)) {
      return action;
  }
  // 对于大部分增删改操作，显示为"操作+资源"
  if (action !== '未知操作') {
    return `${action}${resource}`;
  }

  // 如果没有匹配，返回原始方法名
  return methodName;
};

const indexMethod = (index) => {
  return (currentPage.value - 1) * pageSize.value + index + 1
}

const fetchLogs = async () => {
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    const resp = await getReq('/operationLog/page', params)
    if (resp.data && Array.isArray(resp.data.records)) {
      logs.value = resp.data.records || []
      total.value = resp.data.total || 0
    } else {
      logs.value = []
      total.value = 0
      ElMessage.error('获取操作日志失败')
    }
  } catch (e) {
    logs.value = []
    total.value = 0
    ElMessage.error('网络错误，获取操作日志失败')
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchLogs()
}
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchLogs()
}

onMounted(() => {
  fetchLogs()
})

onActivated(() => {
  fetchLogs();
});
</script>

<style scoped>
.operation-log-container {
  padding: 16px;
}
</style> 