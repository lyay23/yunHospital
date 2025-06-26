<template>
  <!-- 9.患者费用明细查询 -->
	<div class="container" >
    <el-row>
      <el-form :inline="true" :model="query" class="demo-form-inline">
        <el-form-item label="病历号/姓名">
          <el-input v-model="query.keyword" placeholder="病历号/姓名"></el-input>
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
      </el-form>
    </el-row>
	
	<el-row style="background-color: #EAF1F5">
		<el-tag size="mini">患者账单明细：</el-tag>
	</el-row>
	<el-row style="margin: 5px;">
		<el-table :data="tableData01" style="width: 100%" ref="singleTable" >
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
            <el-tag :type="scope.row.backId ? 'info' : 'success'">
              {{ scope.row.backId ? '已退费' : '已收费' }}
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

<script>
  import { fetchData } from '@/utils/api';
export default {
  props: {
    patient: {
      type: Object,
      default: null
    }
  },
  data() {
  	return {
      query: {
        keyword: '',
        itemType: ''
      },
  		tableData01:[]
  	}
  },
  computed: {
    totalAmount() {
      return this.tableData01.reduce((acc, item) => acc + item.price * item.amount, 0).toFixed(2);
    }
  },
  methods:{
    fetchPatientCosts() {
      let params = { ...this.query };
      if (params.itemType === '') {
        delete params.itemType;
      }
      
      if (!params.keyword) {
        this.tableData01 = [];
        return;
      }

      fetchData('/patientcosts/list', params).then(resp => {
        console.log("从后台收到的费用数据:", resp);
        if (resp && resp.data) {
          this.tableData01 = resp.data;
        } else {
          this.tableData01 = [];
        }
      })
    },
	  onSubmit(){
		  this.fetchPatientCosts();
	  }
  },
  watch: {
    patient: {
      handler(newPatient) {
        if (newPatient && newPatient.caseNumber) {
          this.query.keyword = newPatient.caseNumber;
          this.fetchPatientCosts();
        } else {
          this.query.keyword = '';
          this.tableData01 = [];
        }
      },
      immediate: true,
      deep: true
    }
  }
}
</script>

<style>
</style>
