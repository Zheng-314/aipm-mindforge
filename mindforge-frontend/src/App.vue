<template>
  <div id="app">
    <!-- 头部导航 -->
    <Header />

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <p>© 2024 AIPM MindForge - AI产品经理学习平台</p>
        <p>深圳大学 | AI产品经理学习项目</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import Header from '@/components/layout/Header.vue'
</script>

<style>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.footer {
  background: #f5f5f5;
  padding: 20px 0;
  text-align: center;
  color: #666;
  border-top: 1px solid #e8e8e8;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
</style>