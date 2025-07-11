import {
	createSSRApp
} from "vue";
import App from "./App.vue";
import './static/iconfont.css'

export function createApp() {
	const app = createSSRApp(App);
	
	// 添加全局错误处理
	app.config.errorHandler = (err, vm, info) => {
		console.error('Vue Error:', err, info);
	}

	// 添加全局未捕获promise错误处理
	window.addEventListener('unhandledrejection', event => {
		console.error('Unhandled Promise Rejection:', event.reason);
	});

	return {
		app,
	};
}
