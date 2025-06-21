<script setup>
</script>

<template>
	<router-view></router-view>
</template>

<style>
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa; /* 设置全局背景色 */
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 0; /*  登录页不需要顶部边距，Home页有自己的布局 */
}
</style>
