import {createRouter, createWebHistory, createWebHashHistory} from 'vue-router'
import { defineAsyncComponent } from 'vue'
import { useUserStore } from '../store/user.js'

const router = createRouter({ 
  history: createWebHistory(),  // history 模式
  routes: [
	{
	  path: '/',
	  name: 'Login',
	  role:0,
	  component: defineAsyncComponent(() => import(`../components/Login.vue`)),
	  meta: {title: 'Login'}
	},
	{
	  path: '/login',
	  name: 'Login',
	  role:0,
	  component: defineAsyncComponent(() => import(`../components/Login.vue`)),
	  meta: {title: 'Login'}
	},
	{
		path:'/home',
		name:'系统信息',
		component:defineAsyncComponent(() => import(`../components/home.vue`)),
		role:170,
		meta: {title: '系统信息'},
		children:[
			{
				path:'/constant',
				name:'常数类别管理',
				component:defineAsyncComponent(() => import(`../components/neusys/Constant.vue`)),
			},
			{
				path:'/constantitem',
				name:'常数项管理',
				component:defineAsyncComponent(() => import(`../components/neusys/ConstantItem.vue`)),
			},
			{
				path:'/department',
				name:'科室管理',
				component:defineAsyncComponent(() => import(`../components/neusys/Department.vue`)),
			},
			{
				path:'/user',
				name:'账户管理',
				component:defineAsyncComponent(() => import(`../components/neusys/User.vue`)),
			},
			{
				path:'/registlevel',
				name:'挂号级别管理',
				component:defineAsyncComponent(() => import(`../components/neusys/RegistLevel.vue`)),
			},
			{
				path:'/settlecategory',
				name:'结算级别管理',
				component:defineAsyncComponent(() => import(`../components/neusys/SettleCategory.vue`)),
			},
			{
				path:'/disecategory',
				name:'疾病分类管理',
				component:defineAsyncComponent(() => import(`../components/neusys/DiseCategory.vue`)),
			},
			{
				path:'/disease',
				name:'诊断目录管理',
				component:defineAsyncComponent(() => import(`../components/neusys/Disease.vue`)),
			},
			{
				path:'/expense',
				name:'费用科目表',
				component:defineAsyncComponent(() => import(`../components/neusys/ExpenseClass.vue`)),
			},
			{
				path:'/fmeditem',
				name:'非药品收费项目',
				component:defineAsyncComponent(() => import(`../components/neusys/Fmeditem.vue`)),
			},
			{
				path:'/rule',
				name:'排班规则',
				component:defineAsyncComponent(() => import(`../components/neusys/Rule.vue`)),
			},
			{
				path:'/scheduling',
				name:'排班计划',
				component:defineAsyncComponent(() => import(`../components/neusys/Scheduling.vue`)),
			}
		]
	},
	{
	  path: '/home',
	  name: '挂号收费',
	  role:171,
	  component: defineAsyncComponent(() => import(`../components/home.vue`)),
	  meta: {title: '挂号收费'},
	  children:[
		  {
			path:'/customer',
			name:'用户管理',
			component:defineAsyncComponent(() => import(`../components/neureg/Customer.vue`)),
		  },
		  {
			path:'/medicalcard',
			name:'电子就诊卡',
			component:defineAsyncComponent(() => import(`../components/neureg/MedicalCard.vue`)),
		  },
		  {
			path:'/register',
			name:'现场挂号',
			component:defineAsyncComponent(() => import(`../components/neureg/Register.vue`)),
		  },
		  {
			path:'/refund',
			name:'退号',
			component:defineAsyncComponent(() => import(`../components/neureg/Refund.vue`)),
		  }
	  
	  ]
	},
	{
	  path: '/home',
	  name: '门诊医生',
	  role:172,
	  component: defineAsyncComponent(() => import(`../components/home.vue`)),
	  meta: {title: '门诊医生'},
	  children:[
		  {
			path:'/docHome',
			name:'门诊病历',
			component:defineAsyncComponent(() => import(`../components/neudoc/DocHome.vue`)),
		  }
	  
	  ]
	},
   
  ]
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
	// let userLogin =  sessionStorage.getItem("userLogin");
	const userStore = useUserStore()
	// let isAuth=userStore.getUserInfo.value.isAuth;
	let isAuth=userStore.getUserInfo.value.isAuth;
	//判断路由的别名不是登录且未进行登录认证，就跳转去登录
	if(to.name !== 'Login' && !isAuth){
	  next({ name: 'Login' })
	}else if(to.name=="Login" && isAuth){
	//已登录，不允许退回到登录页面
	   next({ path: '/home' })
	}
	else{
	  next()
	}
})


router.afterEach((to, from)=>{
  // console.log(to, from)
  //console.log('afterEach')
})

export default router