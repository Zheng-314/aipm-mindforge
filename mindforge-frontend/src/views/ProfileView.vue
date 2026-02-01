<template>
  <div class="profile-view">
    <div class="profile-header">
      <h1>学习进度</h1>
      <p>跟踪你的AI产品经理学习进展</p>
    </div>

    <div class="profile-content">
      <!-- 用户信息卡片 -->
      <el-card class="user-card">
        <div class="user-info">
          <el-avatar :size="80" :src="userInfo.avatar" class="user-avatar">
            {{ userInitials }}
          </el-avatar>
          <div class="user-details">
            <h2>{{ userInfo.username }}</h2>
            <p class="user-email">{{ userInfo.email }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-number">{{ noteStats.total || 0 }}</span>
                <span class="stat-label">笔记数量</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ knowledgeStats.learned || 0 }}</span>
                <span class="stat-label">已学节点</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.level || '初级' }}</span>
                <span class="stat-label">当前等级</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 学习进度 -->
      <div class="progress-section">
        <h3>学习进度概览</h3>
        <div class="progress-grid">
          <el-card class="progress-card">
            <div class="progress-header">
              <el-icon size="24" color="#409EFF"><Collection /></el-icon>
              <h4>知识图谱进度</h4>
            </div>
            <div class="progress-content">
              <el-progress
                :percentage="knowledgeProgress"
                :stroke-width="10"
                :show-text="false"
              />
              <div class="progress-text">
                <span>{{ knowledgeStats.learned || 0 }} / {{ knowledgeStats.total || 21 }} 个节点</span>
                <span>{{ Math.round(knowledgeProgress) }}%</span>
              </div>
            </div>
          </el-card>

          <el-card class="progress-card">
            <div class="progress-header">
              <el-icon size="24" color="#67C23A"><Document /></el-icon>
              <h4>笔记完成度</h4>
            </div>
            <div class="progress-content">
              <div class="note-stats">
                <div class="note-stat-item">
                  <span class="note-stat-number">{{ noteStats.total || 0 }}</span>
                  <span class="note-stat-label">总笔记</span>
                </div>
                <div class="note-stat-item">
                  <span class="note-stat-number">{{ noteStats.aiProcessed || 0 }}</span>
                  <span class="note-stat-label">AI分析</span>
                </div>
                <div class="note-stat-item">
                  <span class="note-stat-number">{{ noteStats.recent || 0 }}</span>
                  <span class="note-stat-label">最近7天</span>
                </div>
              </div>
            </div>
          </el-card>

          <el-card class="progress-card">
            <div class="progress-header">
              <el-icon size="24" color="#E6A23C"><Clock /></el-icon>
              <h4>学习专注领域</h4>
            </div>
            <div class="progress-content">
              <div class="focus-area">
                <el-tag type="primary" v-if="userInfo.studyFocus">
                  {{ userInfo.studyFocus }}
                </el-tag>
                <p v-else class="no-focus">尚未设置学习重点</p>
                <el-button
                  type="text"
                  size="small"
                  @click="showEditFocus = true"
                >
                  编辑
                </el-button>
              </div>
            </div>
          </el-card>

          <el-card class="progress-card">
            <div class="progress-header">
              <el-icon size="24" color="#F56C6C"><TrendCharts /></el-icon>
              <h4>学习建议</h4>
            </div>
            <div class="progress-content">
              <div class="recommendation">
                <p v-if="recommendation">{{ recommendation }}</p>
                <p v-else class="no-recommendation">暂无建议，继续学习吧！</p>
                <el-button
                  type="text"
                  size="small"
                  @click="getAIRecommendation"
                  :loading="loadingRecommendation"
                >
                  获取AI建议
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 最近笔记 -->
      <div class="recent-notes">
        <h3>最近笔记</h3>
        <div class="notes-list">
          <div
            v-for="note in recentNotes"
            :key="note.id"
            class="note-item"
            @click="$router.push('/notes')"
          >
            <div class="note-title">{{ note.title }}</div>
            <div class="note-preview">{{ getPreview(note.content) }}</div>
            <div class="note-time">{{ formatDate(note.updatedAt) }}</div>
          </div>
          <div v-if="recentNotes.length === 0" class="empty-notes">
            <p>还没有笔记，去创建你的第一份笔记吧！</p>
            <el-button type="primary" @click="$router.push('/notes')">
              去创建笔记
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑学习重点对话框 -->
    <el-dialog v-model="showEditFocus" title="编辑学习重点" width="400px">
      <el-form :model="editForm">
        <el-form-item label="学习重点">
          <el-select v-model="editForm.studyFocus" placeholder="选择学习重点">
            <el-option label="AI产品设计" value="AI产品设计" />
            <el-option label="机器学习基础" value="机器学习基础" />
            <el-option label="Prompt Engineering" value="Prompt Engineering" />
            <el-option label="产品管理" value="产品管理" />
            <el-option label="技术理解" value="技术理解" />
            <el-option label="商业分析" value="商业分析" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditFocus = false">取消</el-button>
        <el-button type="primary" @click="updateStudyFocus">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Collection, Document, Clock, TrendCharts } from '@element-plus/icons-vue'
import axios from 'axios'

// 用户信息
const userInfo = ref({
  username: '',
  email: '',
  avatar: '',
  level: '',
  studyFocus: ''
})

// 统计数据
const noteStats = ref({
  total: 0,
  aiProcessed: 0,
  recent: 0
})

const knowledgeStats = ref({
  total: 21,
  learned: 0
})

// 学习建议
const recommendation = ref('')
const loadingRecommendation = ref(false)

// 最近笔记
const recentNotes = ref<any[]>([])

// 对话框控制
const showEditFocus = ref(false)
const editForm = ref({
  studyFocus: ''
})

// 计算属性
const userInitials = computed(() => {
  if (!userInfo.value.username) return 'U'
  return userInfo.value.username.charAt(0).toUpperCase()
})

const knowledgeProgress = computed(() => {
  if (!knowledgeStats.value.total) return 0
  return (knowledgeStats.value.learned / knowledgeStats.value.total) * 100
})

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await axios.get('/auth/me')
    userInfo.value = response.data
    editForm.value.studyFocus = userInfo.value.studyFocus || ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取笔记统计
const fetchNoteStats = async () => {
  try {
    const response = await axios.get('/notes')
    const notes = response.data

    noteStats.value.total = notes.length
    noteStats.value.aiProcessed = notes.filter((note: any) =>
      note.aiProcessedData && note.aiProcessedData.length > 0
    ).length

    // 计算最近7天的笔记
    const sevenDaysAgo = new Date()
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7)

    noteStats.value.recent = notes.filter((note: any) => {
      const noteDate = new Date(note.updatedAt)
      return noteDate > sevenDaysAgo
    }).length

    // 获取最近5条笔记
    recentNotes.value = notes
      .sort((a: any, b: any) => new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime())
      .slice(0, 5)
  } catch (error) {
    console.error('获取笔记统计失败:', error)
  }
}

// 获取知识节点统计（模拟数据）
const fetchKnowledgeStats = () => {
  // 这里可以调用后端API获取真实数据
  knowledgeStats.value.learned = Math.min(Math.floor(Math.random() * 8), 21)
}

// 获取AI学习建议
const getAIRecommendation = async () => {
  loadingRecommendation.value = true
  try {
    // 这里可以调用后端API获取AI建议
    // 暂时使用模拟数据
    const recommendations = [
      '建议深入学习"AI产品设计基础"，这是AI产品经理的核心能力',
      '你的笔记中缺少技术理解相关的内容，建议补充机器学习基础知识',
      '可以尝试创建更多关于Prompt Engineering的实践笔记',
      '根据你的学习进度，建议下一步学习"商业化思维与变现策略"',
      '你的学习记录显示产品设计能力较强，可以挑战更复杂的AI产品设计'
    ]

    recommendation.value = recommendations[Math.floor(Math.random() * recommendations.length)]
    ElMessage.success('已获取AI学习建议')
  } catch (error) {
    console.error('获取AI建议失败:', error)
  } finally {
    loadingRecommendation.value = false
  }
}

// 更新学习重点
const updateStudyFocus = async () => {
  try {
    // 这里应该调用后端API更新用户信息
    userInfo.value.studyFocus = editForm.value.studyFocus
    showEditFocus.value = false
    ElMessage.success('学习重点已更新')
  } catch (error) {
    console.error('更新学习重点失败:', error)
  }
}

// 工具函数
const getPreview = (content: string) => {
  if (!content) return '无内容'
  return content.length > 50 ? content.substring(0, 50) + '...' : content
}

const formatDate = (dateString: string) => {
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-CN')
  } catch {
    return dateString
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchUserInfo()
  fetchNoteStats()
  fetchKnowledgeStats()
})
</script>

<style scoped>
.profile-view {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  margin-bottom: 32px;
}

.profile-header h1 {
  font-size: 28px;
  color: #303133;
  margin: 0 0 8px 0;
}

.profile-header p {
  color: #909399;
  margin: 0;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.user-card {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 24px;
}

.user-avatar {
  flex-shrink: 0;
}

.user-details {
  flex: 1;
}

.user-details h2 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 8px 0;
}

.user-email {
  color: #909399;
  margin: 0 0 20px 0;
}

.user-stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #409EFF;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.progress-section h3 {
  font-size: 20px;
  color: #303133;
  margin: 0 0 20px 0;
}

.progress-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.progress-card {
  height: 100%;
}

.progress-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.progress-header h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.progress-content {
  padding: 8px 0;
}

.progress-text {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  font-size: 14px;
  color: #606266;
}

.note-stats {
  display: flex;
  justify-content: space-around;
}

.note-stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.note-stat-number {
  font-size: 20px;
  font-weight: 600;
  color: #67C23A;
  margin-bottom: 4px;
}

.note-stat-label {
  font-size: 12px;
  color: #909399;
}

.focus-area {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.no-focus {
  color: #909399;
  font-style: italic;
  margin: 0;
}

.recommendation {
  min-height: 80px;
}

.recommendation p {
  margin: 0 0 12px 0;
  line-height: 1.6;
  color: #606266;
}

.no-recommendation {
  color: #909399;
  font-style: italic;
  margin: 0 0 12px 0;
}

.recent-notes h3 {
  font-size: 20px;
  color: #303133;
  margin: 0 0 20px 0;
}

.notes-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.note-item {
  padding: 16px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.note-item:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
}

.note-title {
  font-weight: 600;
  margin-bottom: 8px;
  color: #303133;
}

.note-preview {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
  line-height: 1.4;
}

.note-time {
  font-size: 12px;
  color: #c0c4cc;
}

.empty-notes {
  text-align: center;
  padding: 40px;
  color: #909399;
}

.empty-notes p {
  margin: 0 0 20px 0;
}
</style>