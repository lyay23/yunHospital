<template>
	<div class="m-2">
		<el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
			<el-tab-pane label="用户管理" name="first">
				<!-- 搜索栏 start -->
				<el-row class="search-bar">
					<div class="search-controls">
						<el-input v-model.trim="kw" placeholder="请输入姓名或身份证号" />
						<el-button type="primary" @click="loadData(1)">查询</el-button>
					</div>
					<el-button type="primary" @click="showAdd">新增用户</el-button>
				</el-row>
				<div style="margin-top: 40px;"></div>
				<!-- 搜索栏 end -->
				<!-- 表格栏 start -->
				<el-table :data="data.records" style="width: 100%" v-loading="loading">
					<el-table-column type="index" label="序号" align="center" width="70"
						:index="index => (data.current - 1) * data.size + index + 1" />
					<el-table-column prop="realName" label="姓名" align="center" />
					<el-table-column prop="gender" label="性别" align="center">
						<template #default="scope">
							{{ scope.row.gender == 71 ? "男" : "女" }}
						</template>
					</el-table-column>
					<el-table-column prop="idnumber" label="身份证" align="center"  width="200"/>
					<el-table-column prop="birthdate" label="出生日期" align="center" />
					<el-table-column prop="phone" label="手机" align="center" />
					<el-table-column prop="createdate" label="注册日期" align="center" />
					<el-table-column prop="channelName" label="注册渠道" align="center" />
					<el-table-column fixed="right" label="操作" align="center" width="150">
						<template #default="scope">
							<el-button link type="primary" @click="showEdit(scope.row)">编辑</el-button>
							<el-button link type="danger" @click="del(scope.row.id)">删除</el-button>
						</template>
					</el-table-column>
				</el-table>
				<!-- 表格栏 end -->
				<div style="margin-top: 20px;"></div>
				<!-- page start -->
				<el-row>
					<el-col :span="20">
						<el-pagination background layout="prev, pager, next" :total="data.total" :page-count="data.pages"
							v-model:current-page="currentPage" @current-change="handleCurrentChange" />
					</el-col>
				</el-row>
				<!-- page end -->
			</el-tab-pane>
		</el-tabs>
	</div>

	<!-- 对话框 start -->
	<el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleClose">
		<el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
			<el-form-item label="姓名" prop="realName">
				<el-input v-model="form.realName" autocomplete="off" />
			</el-form-item>
			<el-form-item label="身份证号" prop="idnumber">
				<el-input v-model="form.idnumber" autocomplete="off" />
			</el-form-item>
			<el-form-item label="出生日期" prop="birthdate">
				<el-date-picker v-model="form.birthdate" type="date" placeholder="选择一个日期" format="YYYY-MM-DD"
					value-format="YYYY-MM-DD" />
			</el-form-item>
			<el-form-item label="性别" prop="gender">
				<el-select v-model="form.gender" placeholder="请选择性别">
					<el-option label="男" :value="71" />
					<el-option label="女" :value="72" />
				</el-select>
			</el-form-item>
			<el-form-item label="手机号码" prop="phone">
				<el-input v-model="form.phone" autocomplete="off" />
			</el-form-item>
			<el-form-item label="注册渠道" prop="channel">
				<el-select v-model="form.channel" placeholder="请选择注册渠道">
					<el-option v-for="item in channelOptions" :key="item.value" :label="item.label" :value="item.value" />
				</el-select>
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="dialogVisible = false">取消</el-button>
				<el-button type="primary" @click="save">保存</el-button>
			</span>
		</template>
	</el-dialog>
	<!-- 对话框 end -->
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { fetchData, postReq } from '../../utils/api';
import { ElMessage, ElMessageBox } from 'element-plus';
import router from '../../router';

const activeName = ref('first');
const kw = ref('');
const loading = ref(false);
const data = ref({ records: [], total: 0, pages: 0, current: 1, size: 10 });
const currentPage = ref(1);

const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref(null);

const defaultForm = {
	id: null,
	realName: '',
	gender: '',
	idnumber: '',
	birthdate: '',
	phone: '',
	channel: 181,
};
const form = ref(JSON.parse(JSON.stringify(defaultForm)));

const rules = {
	realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
	idnumber: [
		{ required: true, message: '请输入有效身份证号码', trigger: 'blur' },
		{ pattern: /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入有效身份证号码', trigger: 'blur' },
	],
	birthdate: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
	gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
	phone: [
		{ required: true, message: '请输入手机号', trigger: 'blur' },
		{ pattern: /^1[3456789]\d{9}$/, message: '请输入有效手机号', trigger: 'blur' }
	],
	channel: [{ required: true, message: '请选择注册渠道', trigger: 'change' }],
};

const channelOptions = ref([
	{ value: 181, label: '窗口' },
	{ value: 182, label: '微信' },
	{ value: 183, label: 'AndroidApp' },
	{ value: 184, label: '苹果App' }
]);

const handleClick = (tab, event) => {
	// a simple handler
};

onMounted(() => {
	loadData(1);
});

async function loadData(pn) {
	loading.value = true;
	const url = `/customer/page?count=${data.value.size}&pn=${pn}&keyword=${kw.value}`;
	try {
		const result = await fetchData(url, null);
		if (result.result) {
			data.value = result.data;
		} else {
			if (result.errMsg === '未登录') {
				router.push('/login');
			}
		}
	} catch (error) {
		ElMessage.error('数据加载失败');
	} finally {
		loading.value = false;
	}
}

function handleCurrentChange(number) {
	loadData(number);
}

function handleClose() {
	if (formRef.value) {
		formRef.value.resetFields();
	}
	form.value = JSON.parse(JSON.stringify(defaultForm));
}

function showAdd() {
	dialogTitle.value = '新增用户';
	dialogVisible.value = true;
}

function showEdit(item) {
	dialogTitle.value = '编辑用户';
	form.value = { ...item };
	dialogVisible.value = true;
}

async function save() {
	if (!formRef.value) return;
	await formRef.value.validate(async (valid) => {
		if (valid) {
			const isUpdate = !!form.value.id;
			if (!isUpdate) {
				const now = new Date();
				const year = now.getFullYear();
				const month = (now.getMonth() + 1).toString().padStart(2, '0');
				const day = now.getDate().toString().padStart(2, '0');
				form.value.createdate = `${year}-${month}-${day}`;
			}
			const url = isUpdate ? '/customer/update' : '/customer/add';
			try {
				const resp = await postReq(url, form.value);
				if (resp.data.result) {
					dialogVisible.value = false;
					const targetPage = isUpdate ? data.value.current : 1;
                    if (!isUpdate) currentPage.value = 1;
					loadData(targetPage);
					ElMessage.success(isUpdate ? '修改成功' : '新增成功');
				} else {
					ElMessage.error(resp.data.errMsg || '操作失败');
				}
			} catch (error) {
				ElMessage.error('操作失败');
			}
		}
	});
}

function del(id) {
	ElMessageBox.confirm('确认删除该用户吗?', '警告', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'warning',
	}).then(async () => {
		try {
			const resp = await postReq(`/customer/del/${id}`);
			if (resp.data.result) {
				loadData(data.value.current);
				ElMessage.success('删除成功');
			} else {
				ElMessage.error(resp.data.errMsg || '删除失败');
			}
		} catch (error) {
			ElMessage.error('删除失败');
		}
	});
}
</script>

<style scoped>
.search-bar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20px;
}

.search-controls {
	display: flex;
	align-items: center;
	gap: 15px;
}
</style>