<template>
  <div class="notes-view">
    <el-container>
      <!-- 左侧：笔记列表 -->
      <el-aside width="300px" class="notes-sidebar">
        <div class="sidebar-header">
          <h2>我的笔记</h2>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon> 新建笔记
          </el-button>
        </div>

        <!-- 笔记列表 -->
        <div class="notes-list">
          <div
            v-for="note in notes"
            :key="note.id"
            class="note-item"
            :class="{ 'active': selectedNote?.id === note.id }"
            @click="selectNote(note)"
          >
            <div class="note-title">{{ note.title }}</div>
            <div class="note-preview">{{ getPreview(note.content) }}</div>
            <div class="note-time">{{ formatDate(note.updatedAt) }}</div>
          </div>
        </div>
      </el-aside>

      <!-- 中间：笔记编辑器 -->
      <el-main class="notes-main">
        <div v-if="selectedNote" class="note-editor-container">
          <div class="editor-header">
            <h2>{{ selectedNote.title }}</h2>
            <div class="action-buttons">
              <el-button @click="analyzeNote" :loading="isAnalyzing">
                <el-icon><MagicStick /></el-icon>
                {{ isAnalyzing ? 'AI分析中...' : 'AI分析笔记' }}
              </el-button>
              <el-button type="primary" @click="saveNote">
                保存
              </el-button>
            </div>
          </div>

          <!-- 笔记内容编辑器 -->
          <el-input
            v-model="selectedNote.content"
            type="textarea"
            :rows="15"
            placeholder="输入笔记内容..."
            class="note-textarea"
          />
        </div>

        <!-- 如果没有选择笔记，显示提示 -->
        <div v-else class="empty-state">
          <el-icon size="60"><Document /></el-icon>
          <h3>选择或创建笔记</h3>
          <p>左侧选择笔记，或点击"新建笔记"开始记录</p>
        </div>
      </el-main>

      <!-- 右侧：AI分析结果（可收起） -->
      <el-aside :width="aiPanelWidth" class="ai-sidebar" v-if="selectedNote">
        <div class="ai-sidebar-header">
          <h3>AI分析结果</h3>
          <div class="ai-sidebar-actions">
            <el-button
              type="text"
              @click="toggleAiPanel"
              :icon="aiPanelCollapsed ? 'ArrowLeft' : 'ArrowRight'"
              circle
            />
            <el-button
              v-if="aiResult && !aiPanelCollapsed"
              type="text"
              @click="clearAiResult"
              icon="Close"
              circle
            />
          </div>
        </div>

        <div class="ai-result-content" v-if="!aiPanelCollapsed">
          <div v-if="!aiResult" class="ai-empty-state">
            <el-icon size="40"><MagicStick /></el-icon>
            <p>点击"AI分析笔记"按钮<br>获取智能分析结果</p>
          </div>
          <div v-else v-html="formatAiResult(aiResult)" class="ai-result-html"></div>
        </div>

        <div v-else class="ai-collapsed-hint" @click="toggleAiPanel">
          <el-icon><MagicStick /></el-icon>
          <span>AI分析</span>
        </div>
      </el-aside>
    </el-container>

    <!-- 新建笔记对话框 -->
    <el-dialog v-model="showCreateDialog" title="新建笔记" width="500px">
      <el-form :model="newNote">
        <el-form-item label="标题">
          <el-input v-model="newNote.title" placeholder="输入笔记标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="newNote.content"
            type="textarea"
            :rows="5"
            placeholder="输入笔记内容..."
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="createNote">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { Plus, MagicStick, Document, ArrowLeft, ArrowRight, Close } from '@element-plus/icons-vue';

// API配置
const API_BASE = 'http://localhost:8080/api';

// 笔记类型定义
interface Note {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

// 响应式数据
const notes = ref<Note[]>([]);
const selectedNote = ref<Note | null>(null);
const showCreateDialog = ref(false);
const isAnalyzing = ref(false);
const aiResult = ref('');
const aiPanelCollapsed = ref(false);

// 计算属性：AI面板宽度
const aiPanelWidth = computed(() => {
  return aiPanelCollapsed.value ? '60px' : '400px';
});

// 新建笔记的数据
const newNote = ref({
  title: '',
  content: ''
});

// 切换AI面板
const toggleAiPanel = () => {
  aiPanelCollapsed.value = !aiPanelCollapsed.value;
};

// 清空AI结果
const clearAiResult = () => {
  aiResult.value = '';
  ElMessage.info('已清空AI分析结果');
};

// 加载笔记列表
const loadNotes = async () => {
  try {
    console.log('开始加载笔记...');
    const response = await axios.get(`${API_BASE}/notes`);
    console.log('笔记数据:', response.data);

    // 过滤掉无效数据
    notes.value = response.data.filter((note: any) => note.id !== null);

    if (notes.value.length === 0) {
      console.log('没有有效的笔记数据');
    }
  } catch (error: any) {
    console.error('加载笔记失败:', error);
    ElMessage.error('加载笔记失败: ' + error.message);
  }
};

// 创建笔记
const createNote = async () => {
  if (!newNote.value.title.trim()) {
    ElMessage.warning('请输入标题');
    return;
  }

  console.log('创建笔记:', newNote.value);

  try {
    // 只发送必要的字段
    const noteData = {
      title: newNote.value.title,
      content: newNote.value.content || ''
    };

    console.log('发送的数据:', noteData);

    const response = await axios.post(`${API_BASE}/notes`, noteData);
    console.log('创建成功:', response.data);

    // 重新加载笔记列表
    await loadNotes();

    showCreateDialog.value = false;
    newNote.value = { title: '', content: '' };
    ElMessage.success('笔记创建成功');
  } catch (error: any) {
    console.error('创建笔记失败详情:', error);
    console.error('错误状态:', error.response?.status);
    console.error('错误数据:', error.response?.data);
    ElMessage.error('创建笔记失败: ' + (error.response?.data || error.message));
  }
};

// 选择笔记
const selectNote = (note: Note) => {
  // 深拷贝，避免直接修改原数据
  selectedNote.value = JSON.parse(JSON.stringify(note));
  aiResult.value = ''; // 清空之前的AI结果
  aiPanelCollapsed.value = false; // 展开AI面板
};

// 保存笔记
const saveNote = async () => {
  if (!selectedNote.value) return;

  console.log('保存笔记:', selectedNote.value);

  try {
    const response = await axios.put(
      `${API_BASE}/notes/${selectedNote.value.id}`,
      selectedNote.value
    );

    // 更新列表中的笔记
    const index = notes.value.findIndex(n => n.id === selectedNote.value!.id);
    if (index !== -1) {
      notes.value[index] = { ...response.data };
    }

    ElMessage.success('保存成功');
  } catch (error: any) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败: ' + error.message);
  }
};

// AI分析笔记
const analyzeNote = async () => {
  if (!selectedNote.value || !selectedNote.value.content.trim()) {
    ElMessage.warning('请先选择笔记并输入内容');
    return;
  }

  isAnalyzing.value = true;
  aiResult.value = '';
  aiPanelCollapsed.value = false; // 确保AI面板展开

  try {
    const response = await axios.post(
      `${API_BASE}/ai/analyze-note`,
      selectedNote.value.content,
      {
        headers: { 'Content-Type': 'text/plain' }
      }
    );

    aiResult.value = response.data;
    ElMessage.success('AI分析完成');
  } catch (error: any) {
    console.error('AI分析失败:', error);

    // 如果API调用失败，显示示例结果
    aiResult.value = `## 🎯 核心知识点提取
1. **需求分析**：识别用户痛点和市场机会
2. **产品设计**：设计AI产品功能和交互流程
3. **技术理解**：了解机器学习基本原理和限制

## 🔗 知识图谱关联
- 关联到"AI产品设计基础"节点
- 建议学习"Prompt Engineering"相关知识
- 属于"产品经理核心能力"分类

## 📚 下一步学习建议
1. 深入学习《AI产品经理的实践指南》
2. 练习编写产品需求文档
3. 了解大语言模型的最新进展`;

    ElMessage.warning('使用示例结果，如需真实AI分析请配置API密钥');
  } finally {
    isAnalyzing.value = false;
  }
};

// 工具函数：获取内容预览
const getPreview = (content: string) => {
  if (!content) return '无内容';
  return content.length > 50 ? content.substring(0, 50) + '...' : content;
};

// 工具函数：格式化日期
const formatDate = (dateString: string) => {
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN');
  } catch {
    return dateString;
  }
};

// 工具函数：格式化AI结果
const formatAiResult = (text: string) => {
  let formatted = text.replace(/\n/g, '<br>');
  formatted = formatted.replace(/##\s+(.+?)<br>/g, '<h3 style="color: #409EFF;">$1</h3>');
  formatted = formatted.replace(/#\s+(.+?)<br>/g, '<h2 style="color: #303133;">$1</h2>');
  formatted = formatted.replace(/\d+\.\s+(.+?)<br>/g, '<li style="margin: 8px 0;">$1</li>');
  formatted = formatted.replace(/- (.+?)<br>/g, '<li style="margin: 8px 0;">$1</li>');

  formatted = formatted.replace(/🎯/g, '<span style="color: #F56C6C;">🎯</span>');
  formatted = formatted.replace(/🔗/g, '<span style="color: #67C23A;">🔗</span>');
  formatted = formatted.replace(/📚/g, '<span style="color: #E6A23C;">📚</span>');

  return formatted;
};

// 组件挂载时加载笔记
onMounted(() => {
  loadNotes();
});
</script>

<style scoped>
.notes-view {
  height: calc(100vh - 100px);
}

.notes-sidebar {
  border-right: 1px solid #e4e7ed;
  background-color: #f8f9fa;
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.notes-list {
  overflow-y: auto;
  max-height: calc(100% - 80px);
}

.note-item {
  padding: 12px 15px;
  border-radius: 8px;
  margin-bottom: 10px;
  background-color: white;
  border: 1px solid #e4e7ed;
  cursor: pointer;
  transition: all 0.3s ease;
}

.note-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.note-item.active {
  border-color: #409EFF;
  background-color: #ecf5ff;
}

.note-title {
  font-weight: 600;
  margin-bottom: 5px;
  color: #303133;
}

.note-preview {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
  line-height: 1.4;
}

.note-time {
  font-size: 11px;
  color: #c0c4cc;
}

.notes-main {
  padding: 20px;
  background-color: white;
  height: 100%;
  overflow-y: auto;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.editor-header h2 {
  margin: 0;
  color: #303133;
  font-size: 20px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.note-textarea {
  font-family: 'Monaco', 'Menlo', 'Consolas', monospace;
  font-size: 14px;
  line-height: 1.6;
  min-height: 300px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
  color: #909399;
}

.empty-state h3 {
  margin: 20px 0 10px 0;
  color: #606266;
}

.empty-state p {
  margin: 0;
}

/* AI侧边栏样式 */
.ai-sidebar {
  border-left: 1px solid #e4e7ed;
  background-color: #f8f9fa;
  padding: 0;
  height: 100%;
  overflow: hidden;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
}

.ai-sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e4e7ed;
  background-color: white;
  flex-shrink: 0; /* 防止header被压缩 */
}

.ai-sidebar-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.ai-sidebar-actions {
  display: flex;
  gap: 5px;
}

/* AI结果内容区域 - 修复滚动问题 */
.ai-result-content {
  padding: 0;
  flex: 1; /* 占据剩余空间 */
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ai-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
  text-align: center;
  padding: 20px;
}

.ai-empty-state p {
  margin-top: 15px;
  line-height: 1.5;
}

/* AI结果HTML内容 - 可滚动 */
.ai-result-html {
  line-height: 1.8;
  color: #606266;
  padding: 20px;
  overflow-y: auto; /* 添加垂直滚动条 */
  height: 100%; /* 占据父容器高度 */
  max-height: calc(100vh - 200px); /* 限制最大高度 */
}

.ai-result-html::-webkit-scrollbar {
  width: 6px;
}

.ai-result-html::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.ai-result-html::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.ai-result-html::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.ai-result-html h2 {
  font-size: 18px;
  margin: 15px 0 10px 0;
  color: #303133;
}

.ai-result-html h3 {
  font-size: 16px;
  margin: 12px 0 8px 0;
  color: #409EFF;
}

.ai-result-html ul {
  padding-left: 20px;
  margin: 10px 0;
}

.ai-result-html li {
  margin: 6px 0;
}

.ai-collapsed-hint {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #409EFF;
  cursor: pointer;
  transition: background-color 0.3s;
  flex-shrink: 0;
}

.ai-collapsed-hint:hover {
  background-color: #ecf5ff;
}

.ai-collapsed-hint .el-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.ai-collapsed-hint span {
  font-size: 12px;
  writing-mode: vertical-rl;
  text-orientation: mixed;
}

/* 确保整体布局不会溢出 */
.notes-view {
  height: calc(100vh - 100px);
  overflow: hidden;
}

.notes-main {
  padding: 20px;
  background-color: white;
  height: 100%;
  overflow-y: auto;
}

.note-editor-container {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.note-textarea {
  flex: 1;
  min-height: 300px;
}
</style>