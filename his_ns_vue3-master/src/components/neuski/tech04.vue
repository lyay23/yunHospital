<template>
  <!-- 医技管理 -->
  <el-container v-loading="loading">
    <el-header style="padding: 10px">
      <el-row>
        <el-col :span="18">
          <div class="grid-content bg-purple" style="display: flex; align-items: center;">
            <el-input placeholder="请输入医技名称或助记码" size="mini" v-model="keywords01" style="width: 200px; margin-right: 10px;"></el-input>
            <el-button type="primary" size="mini" :icon="Search" @click="searchData">查询医技</el-button>
            <el-button type="primary" size="mini" :icon="Edit" @click="showAddDialog" style="margin-left: 10px;">新增医技</el-button>
          </div>
        </el-col>
        <el-col :span="6" align="right">
          <div class="grid-content bg-purple">
            <el-upload class="upload-demo"
              action="/api/fmeditem/import"
              :headers="uploadHeaders"
              :on-progress="beginUpLoad"
              :on-success="showUpSuccess"
              :on-error="showUpError"
              :show-file-list="false">
              <el-button size="mini" type="primary" :icon="UploadFilled">导入医技</el-button>
            </el-upload>
          </div>
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <el-table
        stripe
        ref="multipleTable" size="mini"
        :data="categories" @selection-change="handleSelectionChange01"
        tooltip-effect="dark"
        style="width: 100%">
        <el-table-column
          type="selection"
          width="55" align="left">
        </el-table-column>
        <el-table-column label="项目编码" prop="itemCode" width="140" align="left" ></el-table-column>
        <el-table-column label="项目名称" prop="itemName" align="left"></el-table-column>
        <el-table-column label="项目规格" prop="format" width="140" align="left"></el-table-column>
        <el-table-column label="项目单价" prop="price" width="80" align="left"></el-table-column>
        <el-table-column label="拼音助记码" prop="mnemonicCode" width="200" align="left"></el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <el-button type="primary"
                       size="mini"
                       @click="showEditDialog(scope.$index, scope.row)">编辑
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-row>
        <el-col :span="8" align="left">
          <el-button type="danger" style="margin-top: 10px;width: 100px;" size="mini"
                     @click="deleteAll"  :disabled="selItems.length==0">批量删除
          </el-button>
        </el-col>
        <el-col :span="16" align="right">
          <el-pagination
            background
            :page-size="pageSize" :current-page.sync="currentPage01"
            layout="prev, pager, next"
            :total="totalCount" @current-change="currentChange" v-show="categories.length>0">
          </el-pagination>
        </el-col>
      </el-row>
    </el-main>

    <!-- 编辑弹框---start -->
    <el-dialog title="新增项目" v-model="dialogFormVisible" width="700px">
      <el-form label-width="100px" :inline="true" :model="formEdit01" :rules="rules" ref="addDrugForm" class="demo-form-inline" size="mini">
        <el-form-item label="项目编码" prop="itemCode">
          <el-input v-model="formEdit01.itemCode" placeholder="项目编码" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="项目名称" prop="itemName">
          <el-input v-model="formEdit01.itemName" placeholder="项目名称" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="项目助记码">
          <el-input v-model="formEdit01.mnemonicCode" placeholder="项目助记码" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="项目规格">
          <el-input v-model="formEdit01.format" placeholder="项目规格" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="项目单价" prop="price">
          <el-input v-model="formEdit01.price" placeholder="项目单价 - - 两位小数" size="mini"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="warning" @click="dialogFormVisible = false" size="mini">取 消</el-button>
          <el-button type="primary" @click="addNewData('addDrugForm')" size="mini">保 存</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog title="编辑药品" v-model="dialogFormVisible02" width="700px">
      <el-form :label-position="labelPosition" label-width="100px" :inline="true" :model="formEdit02" class="demo-form-inline" size="mini">
        <el-form-item label="药品编码">
          <el-input v-model="formEdit02.itemCode" placeholder="药品编码" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="药品名称">
          <el-input v-model="formEdit02.itemName" placeholder="药品名称" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="药品助记码">
          <el-input v-model="formEdit02.mnemonicCode" placeholder="药品助记码" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="药品规格">
          <el-input v-model="formEdit02.format" placeholder="药品规格" size="mini"></el-input>
        </el-form-item>
        <el-form-item label="药品单价">
          <el-input v-model="formEdit02.price" placeholder="药品单价" size="mini"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="warning" @click="dialogFormVisible02 = false" size="mini">取 消</el-button>
          <el-button type="primary" @click="updateData" size="mini">保 存</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 编辑弹框---end -->
  </el-container>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { postReq, putRequest, deleteRequest, getReq } from '../../utils/api';
import { Search, Edit, UploadFilled } from '@element-plus/icons-vue';

const loading = ref(false);
const keywords01 = ref('');
const categories = ref([]);
const selItems = ref([]);
const pageSize = ref(10);
const currentPage01 = ref(1);
const totalCount = ref(0);
const dialogFormVisible = ref(false);
const dialogFormVisible02 = ref(false);
const addDrugForm = ref(null);
const labelPosition = ref('right');
const uploadHeaders = {
  token: localStorage.getItem("token")
};

const formEdit01 = reactive({
  itemCode: '',
  itemName: '',
  mnemonicCode: '',
  format: '',
  price: '',
});

const formEdit02 = reactive({
  id: null,
  itemCode: '',
  itemName: '',
  mnemonicCode: '',
  format: '',
  price: '',
});

const rules = reactive({
  itemCode: [{ required: true, message: '项目编码不能为空', trigger: 'blur' }],
  itemName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
  price: [{ required: true, message: '项目单价不能为空', trigger: 'blur' }],
});

onMounted(() => {
  loadDatas(1, pageSize.value, keywords01.value);
});

const beginUpLoad = () => {
  loading.value = true;
};

const showUpSuccess = (response, file, fileList) => {
  loading.value = false;
  if (response.result) {
    ElMessage.success(`成功导入 ${response.data} 条数据`);
    loadDatas(1, pageSize.value, ''); // 刷新列表
  } else {
    ElMessage.error(response.errMsg || "文件上传失败,请重新上传");
  }
};

const showUpError = (response, file, fileList) => {
  loading.value = false;
  ElMessage.error("文件上传失败,请重新上传");
};

const handleSelectionChange01 = (val) => {
  selItems.value = val;
};

const searchData = () => {
  loadDatas(1, pageSize.value, keywords01.value);
};

const showAddDialog = () => {
  dialogFormVisible.value = true;
};

const addNewData = (formName) => {
  addDrugForm.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        const resp = await postReq("/fmeditem/add", formEdit01);
        if (resp.data.result) {
          ElMessage.success('新增成功!');
          dialogFormVisible.value = false;
          loadDatas(1, pageSize.value, '');
        } else {
          ElMessage.error(resp.data.errMsg || '新增失败!');
        }
      } catch (error) {
        ElMessage.error('网络错误，新增失败!');
      } finally {
        loading.value = false;
      }
    } else {
      ElMessage.error('请填写必要字段');
      return false;
    }
  });
};

const showEditDialog = (index, row) => {
  Object.assign(formEdit02, row);
  dialogFormVisible02.value = true;
};

const updateData = async () => {
  loading.value = true;
  try {
    const resp = await putRequest("/fmeditem/update", formEdit02);
    if (resp.data.result) {
      ElMessage.success('更新成功!');
      dialogFormVisible02.value = false;
      loadDatas(currentPage01.value, pageSize.value, keywords01.value);
    } else {
      ElMessage.error(resp.data.errMsg || '更新失败!');
    }
  } catch (error) {
    ElMessage.error('网络错误，更新失败!');
  } finally {
    loading.value = false;
  }
};

const handleDelete = (index, row) => {
  ElMessageBox.confirm(`此操作将永久删除[${row.itemName}], 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    loading.value = true;
    try {
      const resp = await deleteRequest(`/fmeditem/del?id=${row.id}`);
      if (resp.data.result) {
        ElMessage.success('删除成功!');
        loadDatas(currentPage01.value, pageSize.value, keywords01.value);
      } else {
        ElMessage.error(resp.data.errMsg || '删除失败!');
      }
    } catch (error) {
      ElMessage.error('网络错误，删除失败!');
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

const deleteAll = () => {
  ElMessageBox.confirm(`此操作将永久删除[${selItems.value.length}]条记录, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    loading.value = true;
    let ids = selItems.value.map(item => item.id);
    try {
      const resp = await postReq("/fmeditem/del-batch", ids);
      if (resp.data.result) {
        ElMessage.success('批量删除成功!');
        loadDatas(currentPage01.value, pageSize.value, keywords01.value);
      } else {
        ElMessage.error(resp.data.errMsg || '批量删除失败!');
      }
    } catch (error) {
      ElMessage.error('网络错误，批量删除失败!');
    } finally {
      loading.value = false;
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};

const currentChange = (currentPage) => {
  currentPage01.value = currentPage;
  loadDatas(currentPage, pageSize.value, keywords01.value);
};

const loadDatas = async (pn, count, keyword) => {
  loading.value = true;
  try {
    let url = `/fmeditem/page?pn=${pn}&count=${count}&keyword=${keyword}`;
    const resp = await getReq(url);
    if (resp.data.result) {
      categories.value = resp.data.data.records;
      totalCount.value = resp.data.data.total;
    } else {
      ElMessage.error('数据加载失败!');
    }
  } catch (error) {
    ElMessage.error('数据加载失败!');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
</style>
