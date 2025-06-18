<template>
	<div class="home_container">
	    <el-container>
			
	<!--头部 start-->	
		<el-header class="top-header">
			<el-text class="home_title">东软云医院HIS系统</el-text>
			<div class="home_userinfoContainer" >
				 <el-dropdown  @command="handleCommand">
				      <el-button type="primary">
						<el-avatar size="small" style="margin-right:10px ;"
						    src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
							{{userStore.getUserInfo.value.realName}}<el-icon class="el-icon--right"><arrow-down /></el-icon>				
				      </el-button>
				      <template #dropdown>
				        <el-dropdown-menu>
				          <el-dropdown-item command="" >我的设置</el-dropdown-item>
				          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
				        </el-dropdown-menu>
				      </template>
				    </el-dropdown>  
			</div>
		</el-header>
		<!--头部 end-->	
		  
	    <el-container>
			<!--左侧边栏 start  -->  
	        <el-aside width="200px" >
				   <el-menu
				        default-active="2"
				        class="el-menu-vertical-demo"
						router  @select="addTab" style="height: 700px">
						<el-menu-item>
						  <el-icon><document /></el-icon><el-text class="mx-1" size="large">{{menus.meta.title}}</el-text>
						</el-menu-item>
				        <el-menu-item  v-for="(menu,index) in menus.children" :index="menu.path"  :key="menu.path" >
				          <el-icon><document /></el-icon>{{menu.name}}
						</el-menu-item>
				    </el-menu>
			</el-aside>
			<!--左侧边栏 end  --> 
			
		
			<el-container>
	          <el-main class="main">
					<el-tabs v-model="editableTabsValue" type="card" editable 
					 @edit="handleTabsEdit" @tab-click="clickTag" >
						<el-tab-pane
							align="center"
						    :key="item.name"
						    v-for="(item, index) in editableTabs"
						    :label="item.title"
						    :name="item.name"
						    :route="item.route"
							>
						<router-view></router-view> 
						</el-tab-pane>
					</el-tabs>	
			  </el-main>	
			</el-container>	
	      </el-container>
	    </el-container>
	  </div>
</template>

<script setup>
import {computed, ref} from 'vue'
import { useRouter } from 'vue-router'
import { postReq } from '../utils/api'
import { useUserStore } from '../store/user.js'
//路由
const router = useRouter()
//获取用户仓库对象
const userStore=useUserStore()
const menus=router.options.routes.find(m=>m.role==userStore.getUserInfo.value.useType);
const editableTabsValue=ref('')
const editableTabs=ref([])
const tabIndex=ref(0)

//查找路径对应的路由
function findCompontByPath(path){
	let a=menus.children.find(m=>m.path==path);
	if (a) {
		return a;
	}
	return null;
}

//打开新窗口
function clickTag(tag,e){
	//console.log(tag.paneName)
	router.push(tag.paneName)
}
function addTab(path){
	if(path){
		let componet=findCompontByPath(path)
		if (componet) {
		  if (!editableTabs.value.find(t => t.name == componet.path)) {
			editableTabs.value.push({
				title: componet.name,
				name: componet.path,
				route: componet.path
			});
		  }
		  editableTabsValue.value = componet.path;
		  router.push(componet.path);
		}

	}	
}

function handleTabsEdit(targetName, action) {
  if (action === 'remove') {
	let tabs = editableTabs.value;
	let activeName = editableTabsValue.value;
	if (activeName === targetName) {
	  tabs.forEach((tab, index) => {
		// console.log(tab.name, targetName, tab.name === targetName);
		if (tab.name === targetName) {
		  let nextTab = tabs[index + 1] || tabs[index - 1];
		  if (nextTab) {
			activeName = nextTab.name;
		  }
		}
	  });
	}
	editableTabsValue.value = activeName;
	editableTabs.value = tabs.filter(tab => tab.name !== targetName);
	router.push(activeName);
  }
}

function handleCommand(command){
	postReq("/user/logout").then(resp=>{
		if(resp.data.result){
			userStore.logOut();
			router.push("/login")
		}
	})
	

}

  	
</script>

<style>
.home_container {
	height: 100%;
	position: absolute;
	top: 0px;
	left: 0px;
	width: 100%;
}
.top-header{
	background-color: #20a0ff;
	color: #333;
	text-align: center;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.left-aside{
	background-color: #ECECEC;
}

.main{
	width: 100%;
	height: 100%;
	background-color: #fff;
	color: #000;
	text-align: left;
}
.home_title {
	color: #fff;
	font-size: 22px;
	display: inline;
}

.home_userinfo {
	color: #fff;
	cursor: pointer;
}

.home_userinfoContainer {
	display: inline;
	margin-right: 20px;
	text-align: right;
}
</style>