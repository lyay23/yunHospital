import {createRouter, createWebHistory, createWebHashHistory} from 'vue-router'
import { defineAsyncComponent } from 'vue'
import { useUserStore } from '../store/user.js'

const router = createRouter({ 
  history: createWebHistory(),  // history 模式
  routes: [
	{
	  path: '/',
	  redirect: '/home'
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
		component:defineAsyncComponent(() => import(`../components/Home.vue`)),
		role:170,
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
			},
            {
                path:'/operationlog',
                name:'操作日志',
                component:defineAsyncComponent(() => import(`../components/neusys/OperationLog.vue`)),
            },
		]
	},
	{
	  path: '/home',
	  role:171,
	  component: defineAsyncComponent(() => import(`../components/Home.vue`)),
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
	  role:172,
	  component: defineAsyncComponent(() => import(`../components/Home.vue`)),
	  children:[
		  {
			path:'/docHome',
			name:'门诊病历',
			component:defineAsyncComponent(() => import(`../components/neudoc/DocHome.vue`)),
		  }
	  
	  ]
	},
	{
	  path: '/home',
	  role:173,
	  component: defineAsyncComponent(() => import(`../components/Home.vue`)),
	  children:[
		  {
			path:'/tech01',
			name:'医技检查',
			component:defineAsyncComponent(() => import(`../components/neuski/tech01.vue`)),
		  },
		  {
			path:'/tech02',
			name:'医技检验',
			component:defineAsyncComponent(() => import(`../components/neuski/tech02.vue`)),
		  },
		  {
			path:'/tech03',
			name:'处置管理',
			component:defineAsyncComponent(() => import(`../components/neuski/tech03.vue`)),
		  },
		  {
			path:'/tech04',
			name:'医技管理',
			component:defineAsyncComponent(() => import(`../components/neuski/tech04.vue`)),
		  }
	  ]
	},

  ]
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
	const userStore = useUserStore()
	const isAuth = userStore.isAuth
	const user = userStore.userInfo

	if (to.path === '/login') {
		if (isAuth) {
			next('/home');
		} else {
			next();
		}
	} else {
		if (isAuth) {
			if(to.path==='/home'){
				if (user && user.userType) {
					// 根据 userType 重定向到特定页面
					if (user.userType === 170) {
						next('/constant');
					} else if (user.userType === 171) {
						next('/customer');
					} else if (user.userType === 172) {
						next('/docHome');
					} else if (user.userType === 173) {
						next('/tech01');
					} else {
						next();
					}
				} else {
					next();
				}
			}else{
				next()
			}
		} else {
			next('/login');
		}
	}
})


router.afterEach((to, from)=>{
  // console.log(to, from)
  //console.log('afterEach')
})

export default router