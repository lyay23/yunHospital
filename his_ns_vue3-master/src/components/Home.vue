<template>
	<div class="home_container">
		<el-container class="main-container">
			<!--头部 start-->
			<el-header class="top-header">
				<el-text class="home_title">东软云医院HIS系统</el-text>
				<div class="home_userinfoContainer">
					<el-dropdown @command="handleCommand">
						<el-button type="primary">
							<el-avatar size="small" style="margin-right:10px ;"
								src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
							{{userStore.getUserInfo.value.realName}}<el-icon class="el-icon--right">
								<arrow-down />
							</el-icon>
						</el-button>
						<template #dropdown>
							<el-dropdown-menu>
								<el-dropdown-item command="settings">我的设置</el-dropdown-item>
								<el-dropdown-item command="logout">退出登录</el-dropdown-item>
							</el-dropdown-menu>
						</template>
					</el-dropdown>
				</div>
			</el-header>
			<!--头部 end-->

			<el-container class="content-container">
				<!--左侧边栏 start  -->
				<el-aside width="200px" class="sidebar">
					<el-menu :default-active="editableTabsValue" class="el-menu-vertical" router @select="addTab">
						<el-menu-item v-for="menu in menus.children" :index="menu.path" :key="menu.path">
							<el-icon>
								<document />
							</el-icon>
							<span>{{menu.name}}</span>
						</el-menu-item>
					</el-menu>
				</el-aside>
				<!--左侧边栏 end  -->

				<el-main class="main-content">
					<el-tabs v-model="editableTabsValue" type="card" closable @edit="handleTabsEdit" @tab-click="clickTag">
						<el-tab-pane v-for="item in editableTabs" :key="item.name" :label="item.title" :name="item.name">
							<router-view v-slot="{ Component }">
								<keep-alive>
									<component :is="Component" />
								</keep-alive>
							</router-view>
						</el-tab-pane>
					</el-tabs>
				</el-main>
			</el-container>
		</el-container>
	</div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { postReq } from '../utils/api'
import { useUserStore } from '../store/user.js'
import { ElMessage } from 'element-plus'

//路由
const router = useRouter()
//获取用户仓库对象
const userStore = useUserStore()

// 使用计算属性获取菜单，这样可以响应式更新
const menus = computed(() => {
	const userType = userStore.getUserInfo.value.useType
	const matchedRoute = router.options.routes.find(m => m.role == userType)
	if (!matchedRoute) {
		ElMessage.error('未找到对应的菜单配置')
		return { children: [] }
	}
	// 动态插入操作日志菜单项，仅role:170（医院管理员）可见
    if (userType === 170 && matchedRoute.children && !matchedRoute.children.find(m => m.path === '/operationlog')) {
        matchedRoute.children.push({
            path: '/operationlog',
            name: '操作日志',
        })
    }
	return matchedRoute
})

const editableTabsValue = ref('')
const editableTabs = ref([])
const tabIndex = ref(0)

//查找路径对应的路由
function findCompontByPath(path) {
	if (!menus.value || !menus.value.children) return null
	return menus.value.children.find(m => m.path == path)
}

//打开新窗口
function clickTag(tag) {
	if (tag && tag.paneName) {
		router.push(tag.paneName)
	}
}

function addTab(path) {
	if (!path) return

	let componet = findCompontByPath(path)
	if (!componet) {
		ElMessage.warning('未找到对应的页面组件')
		return
	}

	if (!editableTabs.value.find(t => t.name == componet.path)) {
		editableTabs.value.push({
			title: componet.name,
			name: componet.path,
			route: componet.path
		})
	}
	editableTabsValue.value = componet.path
	router.push(componet.path)
}

function handleTabsEdit(targetName, action) {
	if (action !== 'remove') return

	let tabs = editableTabs.value
	let activeName = editableTabsValue.value

	if (activeName === targetName) {
		tabs.forEach((tab, index) => {
			if (tab.name === targetName) {
				let nextTab = tabs[index + 1] || tabs[index - 1]
				if (nextTab) {
					activeName = nextTab.name
				}
			}
		})
	}

	editableTabsValue.value = activeName
	editableTabs.value = tabs.filter(tab => tab.name !== targetName)

	if (activeName) {
		router.push(activeName)
	} else {
		// 如果没有活动标签，跳转到默认页面
		const defaultPath = menus.value.children[0]?.path
		if (defaultPath) {
			router.push(defaultPath)
		}
	}
}

function handleCommand(command) {
	if (command === 'logout') {
		postReq("/user/logout").then(resp => {
			if (resp.data.result) {
				userStore.logOut()
				router.push("/login")
			}
		}).catch(error => {
			ElMessage.error('退出登录失败')
		})
	}
}

// 组件挂载时，如果没有打开的标签页，打开默认页面
onMounted(() => {
	if (editableTabs.value.length === 0 && menus.value.children.length > 0) {
		const defaultPath = menus.value.children[0].path
		addTab(defaultPath)
	}
})
</script>

<style scoped>
/* Reset browser defaults */
html,
body {
	margin: 0;
	padding: 0;
	height: 100%;
	width: 100%;
	overflow: hidden;
	/* Prevent body scrolling */
}

/* Use a more predictable box-sizing model */
* {
	box-sizing: border-box;
}

.home_container {
	height: 100vh;
	width: 100%;
}

/* Override Element Plus container behavior for our layout */
.main-container,
.content-container {
	display: block;
	height: 100%;
	width: 100%;
}

.top-header {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	height: 60px;
	padding: 0 20px;
	background-color: #20a0ff;
	color: #fff;
	display: flex;
	align-items: center;
	justify-content: space-between;
	z-index: 100;
}

.sidebar {
	position: fixed;
	top: 60px;
	left: 0;
	bottom: 0;
	width: 200px;
	background-color: #fff;
	border-right: 1px solid #e6e6e6;
	overflow-y: hidden;
	/* This will hide the scrollbar */
	z-index: 99;
}

.el-menu-vertical {
	height: 100%;
	/* Make menu fill the sidebar */
	border-right: none;
}

.main-content {
	margin-top: 60px;
	margin-left: 200px;
	padding: 20px;
	height: calc(100vh - 60px);
	/* Full height minus header */
	overflow-y: auto;
	/* Allow main content to scroll */
	width: calc(100% - 200px);
}

.home_title {
	color: #fff;
	font-size: 22px;
	font-weight: bold;
}

.home_userinfoContainer {
	display: flex;
	align-items: center;
}

/* Responsive adjustments */
@media (max-width: 800px) {
	.sidebar {
		width: 80px;
	}

	.main-content {
		margin-left: 80px;
		width: calc(100% - 80px);
	}

	.home_title {
		font-size: 18px;
	}
}
</style>