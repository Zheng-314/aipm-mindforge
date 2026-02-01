<template>
  <div class="home-container">
    <!-- 头部区域 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="title-icon">🧠</span>
          AIPM MindForge
        </h1>
        <p class="hero-subtitle">AI产品经理智能学习平台</p>
        <p class="hero-description">体系化学习AI产品经理知识，构建个人知识图谱</p>

        <div class="hero-actions">
          <el-button type="primary" size="large" @click="$router.push('/notes')">
            <el-icon><Edit /></el-icon> 开始记录
          </el-button>
          <el-button type="success" size="large" @click="testBackendConnection">
            <el-icon><Connection /></el-icon> 测试连接
          </el-button>
        </div>
      </div>
    </div>

    <!-- 学习数据统计 -->
    <div class="stats-section">
      <div class="container">
        <h2 class="section-title">📊 学习数据总览</h2>

        <div class="stats-grid">
          <!-- 打卡天数 -->
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon checkin">📅</div>
              <div class="stat-info">
                <h3>连续打卡</h3>
                <p class="stat-value">{{ checkinDays }} 天</p>
                <div class="stat-progress">
                  <el-progress
                    :percentage="checkinProgress"
                    :show-text="false"
                    stroke-linecap="round"
                  />
                  <span class="progress-text">{{ checkinDays }}/30 天</span>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 学习进度 -->
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon progress">📈</div>
              <div class="stat-info">
                <h3>学习进度</h3>
                <p class="stat-value">{{ studyProgress }}%</p>
                <div class="stat-progress">
                  <el-progress
                    :percentage="studyProgress"
                    :stroke-width="10"
                    stroke-linecap="round"
                    :color="progressColor"
                  />
                  <span class="progress-text">{{ masteredNodes }}/{{ totalNodes }} 个节点</span>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 学习时长 -->
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon time">⏱️</div>
              <div class="stat-info">
                <h3>学习时长</h3>
                <p class="stat-value">{{ totalStudyHours }} 小时</p>
                <div class="stat-detail">
                  <p>本周: {{ weeklyHours }} 小时</p>
                  <p>日均: {{ dailyAverage }} 小时</p>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 笔记数量 -->
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon notes">📝</div>
              <div class="stat-info">
                <h3>学习笔记</h3>
                <p class="stat-value">{{ noteCount }} 篇</p>
                <div class="stat-tags">
                  <el-tag size="small" type="success">已掌握 {{ masteredNotes }} 篇</el-tag>
                  <el-tag size="small" type="warning">学习中 {{ learningNotes }} 篇</el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 打卡日历 -->
    <div class="calendar-section">
      <div class="container">
        <h2 class="section-title">📅 学习打卡日历</h2>

        <el-card class="calendar-card" shadow="never">
          <div class="calendar-header">
            <h3>{{ currentMonth }}月打卡记录</h3>
            <el-button type="text" @click="handleCheckin">
              <el-icon><CircleCheck /></el-icon>
              今日打卡
            </el-button>
          </div>

          <div class="calendar-grid">
            <div v-for="day in calendarDays" :key="day.date"
                 class="calendar-day"
                 :class="{
                   'checked': day.checked,
                   'today': day.isToday,
                   'future': day.isFuture
                 }"
                 @click="toggleCheckin(day)">
              <div class="day-number">{{ day.day }}</div>
              <div class="day-status">
                <el-icon v-if="day.checked" color="#67C23A"><CircleCheckFilled /></el-icon>
                <span v-else class="empty-dot"></span>
              </div>
              <div v-if="day.isToday" class="today-label">今天</div>
            </div>
          </div>

          <div class="calendar-footer">
            <div class="checkin-streak">
              <span class="streak-icon">🔥</span>
              <span>已连续学习 {{ streakDays }} 天</span>
            </div>
            <div class="checkin-total">
              本月累计学习 {{ monthlyCheckedDays }} 天
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 功能导航 -->
    <div class="features-section">
      <div class="container">
        <h2 class="section-title">🚀 核心功能</h2>

        <div class="features-grid">
          <el-card class="feature-card" shadow="hover" @click="$router.push('/knowledge')">
            <div class="feature-icon">🌲</div>
            <h3>知识图谱</h3>
            <p>可视化AI产品经理知识体系，清晰掌握学习路径</p>
            <el-button class="feature-btn" type="primary" text>立即探索</el-button>
          </el-card>

          <el-card class="feature-card" shadow="hover" @click="$router.push('/notes')">
            <div class="feature-icon">📝</div>
            <h3>智能笔记</h3>
            <p>AI辅助记录、整理和分析学习内容</p>
            <el-button class="feature-btn" type="primary" text>开始记录</el-button>
          </el-card>

          <el-card class="feature-card" shadow="hover" @click="$router.push('/profile')">
            <div class="feature-icon">📊</div>
            <h3>学习分析</h3>
            <p>详细的学习数据统计和进度分析</p>
            <el-button class="feature-btn" type="primary" text>查看分析</el-button>
          </el-card>

          <el-card class="feature-card" shadow="hover" @click="showAIFeatures">
            <div class="feature-icon">🤖</div>
            <h3>AI助手</h3>
            <p>智能问答、内容总结和学习建议</p>
            <el-button class="feature-btn" type="primary" text>体验AI</el-button>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 今日学习建议 -->
    <div class="suggestion-section" v-if="dailySuggestion">
      <div class="container">
        <el-alert :title="dailySuggestion" type="info" show-icon>
          <template #default>
            <el-button type="primary" text @click="acceptSuggestion">开始学习</el-button>
          </template>
        </el-alert>
      </div>
    </div>

    <!-- 连接状态 -->
    <div v-if="connectionStatus" class="connection-status">
      <el-alert
        :title="connectionStatus"
        :type="connectionStatus.includes('成功') ? 'success' : 'error'"
        show-icon
        closable
        @close="connectionStatus = ''"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Edit,
  Connection,
  CircleCheck,
  CircleCheckFilled
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 学习数据
const checkinDays = ref(7)
const totalNodes = ref(21)
const masteredNodes = ref(8)
const noteCount = ref(15)
const masteredNotes = ref(10)
const learningNotes = ref(5)
const totalStudyHours = ref(42)
const weeklyHours = ref(12)
const streakDays = ref(7)

// 打卡日历数据
const currentMonth = ref(new Date().getMonth() + 1)
const calendarDays = ref<any[]>([])
const monthlyCheckedDays = ref(14)

// 其他状态
const connectionStatus = ref('')
const dailySuggestion = ref('今日建议：学习"机器学习基础"知识节点，预计需要2小时')

// 计算属性
const checkinProgress = computed(() => {
  return Math.round((checkinDays.value / 30) * 100)
})

const studyProgress = computed(() => {
  return Math.round((masteredNodes.value / totalNodes.value) * 100)
})

const progressColor = computed(() => {
  if (studyProgress.value < 30) return '#F56C6C'
  if (studyProgress.value < 70) return '#E6A23C'
  return '#67C23A'
})

const dailyAverage = computed(() => {
  return (totalStudyHours.value / checkinDays.value).toFixed(1)
})

// 初始化日历
const initCalendar = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = today.getMonth()
  const daysInMonth = new Date(year, month + 1, 0).getDate()

  const days = []

  for (let i = 1; i <= daysInMonth; i++) {
    const date = new Date(year, month, i)
    const isToday = date.getDate() === today.getDate() &&
                    date.getMonth() === today.getMonth()
    const isFuture = date > today

    // 模拟打卡数据（前14天已打卡）
    const checked = i <= 14 && !isFuture

    days.push({
      date: date.toISOString().split('T')[0],
      day: i,
      checked,
      isToday,
      isFuture
    })
  }

  calendarDays.value = days
}

// 打卡操作
const handleCheckin = () => {
  const today = new Date().getDate()
  const todayIndex = today - 1

  if (calendarDays.value[todayIndex]?.checked) {
    ElMessage.warning('今天已经打卡过了！')
    return
  }

  calendarDays.value[todayIndex].checked = true
  checkinDays.value++
  streakDays.value++
  monthlyCheckedDays.value++

  ElMessage.success('打卡成功！继续坚持学习！')
}

const toggleCheckin = (day: any) => {
  if (day.isFuture || day.isToday) {
    ElMessage.info('只能对过去和今天的日期进行打卡操作')
    return
  }

  day.checked = !day.checked
  monthlyCheckedDays.value += day.checked ? 1 : -1

  ElMessage.success(day.checked ? '补卡成功' : '已取消打卡')
}

// 测试后端连接
const testBackendConnection = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/test/hello')
    connectionStatus.value = `✅ 后端连接成功: ${response.data}`
    ElMessage.success('后端服务正常运行！')
  } catch (error) {
    connectionStatus.value = '❌ 后端连接失败，请确保Spring Boot服务正在运行'
    ElMessage.error('后端连接失败！')
  }
}

// AI功能
const showAIFeatures = () => {
  ElMessage.info('AI功能正在开发中，敬请期待！')
}

const acceptSuggestion = () => {
  router.push('/knowledge')
  ElMessage.success('已跳转到知识图谱，开始学习吧！')
}

// 生命周期
onMounted(() => {
  initCalendar()
  // 尝试自动连接后端
  testBackendConnection()
})
</script>

<style scoped>
.home-container {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  background: linear-gradient(180deg, #f5f7fa 0%, #ffffff 100%);
}

/* 头部区域 */
.hero-section {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
  color: white;
  padding: 80px 0;
  text-align: center;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.hero-title {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.title-icon {
  font-size: 56px;
}

.hero-subtitle {
  font-size: 24px;
  opacity: 0.9;
  margin-bottom: 12px;
}

.hero-description {
  font-size: 18px;
  opacity: 0.8;
  max-width: 600px;
  margin: 0 auto 40px;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
}

/* 统计区域 */
.stats-section {
  padding: 60px 0;
  background: white;
}

.section-title {
  font-size: 28px;
  color: #303133;
  margin-bottom: 40px;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.stat-card {
  border-radius: 16px;
  border: none;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-8px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  font-size: 48px;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.stat-icon.checkin { background: #fff7e6; color: #fa8c16; }
.stat-icon.progress { background: #f6ffed; color: #52c41a; }
.stat-icon.time { background: #e6f7ff; color: #1890ff; }
.stat-icon.notes { background: #f9f0ff; color: #722ed1; }

.stat-info h3 {
  margin: 0;
  color: #606266;
  font-size: 16px;
  font-weight: 500;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin: 8px 0;
}

.stat-progress {
  margin-top: 12px;
}

.progress-text {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}

.stat-detail p {
  margin: 4px 0;
  font-size: 14px;
  color: #606266;
}

.stat-tags {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

/* 日历区域 */
.calendar-section {
  padding: 60px 0;
  background: #f5f7fa;
}

.calendar-card {
  border-radius: 16px;
  border: 1px solid #ebeef5;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.calendar-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 12px;
}

.calendar-day {
  aspect-ratio: 1;
  background: white;
  border: 2px solid #ebeef5;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.calendar-day:hover {
  border-color: #1890ff;
  transform: scale(1.05);
}

.calendar-day.checked {
  background: #f6ffed;
  border-color: #b7eb8f;
}

.calendar-day.today {
  background: #e6f7ff;
  border-color: #91d5ff;
}

.calendar-day.future {
  opacity: 0.5;
  cursor: not-allowed;
}

.day-number {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.day-status {
  margin-top: 8px;
}

.empty-dot {
  width: 8px;
  height: 8px;
  background: #dcdfe6;
  border-radius: 50%;
  display: inline-block;
}

.today-label {
  position: absolute;
  top: 4px;
  right: 4px;
  font-size: 10px;
  color: #1890ff;
  font-weight: bold;
}

.calendar-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.checkin-streak {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fa8c16;
  font-weight: 500;
}

.streak-icon {
  font-size: 20px;
}

.checkin-total {
  color: #606266;
  font-size: 14px;
}

/* 功能区域 */
.features-section {
  padding: 60px 0;
  background: white;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.feature-card {
  border-radius: 16px;
  text-align: center;
  padding: 32px 24px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.feature-card:hover {
  border-color: #1890ff;
  transform: translateY(-8px);
}

.feature-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 12px;
}

.feature-card p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 20px;
}

.feature-btn {
  font-weight: 500;
}

/* 建议区域 */
.suggestion-section {
  padding: 40px 0;
  background: #f5f7fa;
}

/* 连接状态 */
.connection-status {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 90%;
  max-width: 600px;
  z-index: 1000;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 36px;
    flex-direction: column;
    gap: 8px;
  }

  .hero-subtitle {
    font-size: 20px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .stats-grid,
  .features-grid {
    grid-template-columns: 1fr;
  }

  .calendar-grid {
    gap: 8px;
  }

  .calendar-day {
    border-radius: 8px;
  }
}
</style>