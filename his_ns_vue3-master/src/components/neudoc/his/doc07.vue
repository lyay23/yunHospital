<template>
  <!-- 9.患者费用明细查询 -->
	<div class="container" >
    <el-row>
      <el-form :inline="true" :model="query" class="demo-form-inline">
        <el-form-item label="病历号/姓名">
          <el-input v-model="query.keyword" placeholder="病历号/姓名" disabled></el-input>
        </el-form-item>
        <el-form-item label="项目类别">
          <el-select v-model="query.itemType" placeholder="项目类别" clearable style="width: 120px;">
            <el-option label="全部" value=""></el-option>
            <el-option label="非药品" value="1"></el-option>
            <el-option label="药品" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="handlePayment">检查缴费</el-button>
        </el-form-item>
      </el-form>
    </el-row>
	
	<el-row style="background-color: #EAF1F5">
		<el-tag size="mini">患者账单明细：</el-tag>
	</el-row>
	<el-row style="margin: 5px;">
		<el-table :data="tableData" style="width: 100%" ref="singleTable" >
			<el-table-column type="index" width="50">
    		</el-table-column>
		    <el-table-column property="name" label="名称" >
		    </el-table-column>
		    <el-table-column property="price" label="单价" width="100">
		    </el-table-column>
		    <el-table-column property="amount" label="数量" width="100">
		    </el-table-column>
		    <el-table-column label="金额" width="100">
          <template #default="scope">
            {{ (scope.row.price * scope.row.amount).toFixed(2) }}
          </template>
		    </el-table-column>
		    <el-table-column label="收费状态" width="100">
          <template #default="scope">
            <el-tag :type="getCostTagType(scope.row)">
              {{ getCostStatus(scope.row) }}
            </el-tag>
          </template>
		    </el-table-column>
		</el-table>
	</el-row>
	<el-row style="margin: 5px;">
		<el-tag size="mini">金额合计：</el-tag><span>{{totalAmount}}元</span>
	</el-row>
	
   </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { getReq, postReq } from '@/utils/api';
import { ElMessage } from 'element-plus';

const props = defineProps({
  patient: {
    type: Object,
    default: null
  }
});

const query = ref({
  keyword: '',
  itemType: ''
});

const tableData = ref([]);

const totalAmount = computed(() => {
  return tableData.value
    .filter(item => !item.payTime && !item.backId) // 只计算未收费且未退费的项目
    .reduce((acc, item) => acc + item.price * item.amount, 0)
    .toFixed(2);
});

const fetchPatientCosts = async () => {
  let params = { ...query.value };
  if (params.itemType === '') {
    delete params.itemType;
  }
  
  if (!params.keyword) {
    tableData.value = [];
    return;
  }

  const response = await getReq('/patientcosts/list', params);
  
  if (response && response.data && response.data.result) {
    tableData.value = response.data.data;
  } else {
    tableData.value = [];
    ElMessage.error('获取费用列表失败');
  }
};

const onSubmit = () => {
  fetchPatientCosts();
};

const handlePayment = async () => {
  if (!props.patient || !props.patient.id) {
    ElMessage.warning('请先选择一个患者');
    return;
  }
  const res = await postReq('/patientcosts/doPay', { registId: props.patient.id });
  if (res.data.result) {
    ElMessage.success('模拟缴费成功');
    fetchPatientCosts(); // 重新加载费用
  } else {
    ElMessage.error(res.data.errMsg || '缴费失败');
  }
};

const getCostTagType = (row) => {
  if (row.backId) return 'info'; // 已退费
  if (row.payTime) return 'success'; // 已收费
  return 'warning'; // 未收费
};

const getCostStatus = (row) => {
  if (row.backId) return '已退费';
  if (row.payTime) return '已收费';
  return '未收费';
};

watch(() => props.patient, (newPatient) => {
  if (newPatient && newPatient.caseNumber) {
    query.value.keyword = newPatient.caseNumber;
    fetchPatientCosts();
  } else {
    query.value.keyword = '';
    tableData.value = [];
  }
}, { immediate: true, deep: true });
</script>

<style>
</style>
