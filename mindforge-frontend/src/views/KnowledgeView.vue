<template>
  <div class="knowledge">
    <div class="container">
      <div class="page-header">
        <h1><span class="icon">🌲</span> 知识图谱</h1>
        <p class="subtitle">AI产品经理核心能力体系可视化</p>
      </div>

      <!-- 控制面板 -->
      <div class="control-panel">
        <div class="left-controls">
          <el-button type="primary" @click="loadKnowledgeTree">
            <el-icon><Refresh /></el-icon> 加载知识树
          </el-button>
          <el-button @click="showAllCategories">
            <el-icon><View /></el-icon> 查看所有分类
          </el-button>
        </div>
        <div class="right-controls">
          <el-select v-model="selectedCategory" placeholder="选择分类" style="width: 200px">
            <el-option label="全部" value=""></el-option>
            <el-option label="战略与市场分析" value="战略与市场分析"></el-option>
            <el-option label="用户研究与体验设计" value="用户研究与体验设计"></el-option>
            <el-option label="技术理解与架构设计" value="技术理解与架构设计"></el-option>
            <el-option label="数据与算法应用" value="数据与算法应用"></el-option>
            <el-option label="商业运营与产品增长" value="商业运营与产品增长"></el-option>
          </el-select>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <p>正在加载知识树...</p>
      </div>

      <!-- 知识树展示 -->
      <div v-if="!loading && knowledgeTree.length > 0" class="knowledge-tree">
        <div class="tree-container">
          <div v-for="rootNode in knowledgeTree" :key="rootNode.id" class="root-node">
            <!-- 根节点 -->
            <div class="node-card root" :style="{ borderColor: rootNode.color }">
              <div class="node-header">
                <span class="node-icon">{{ rootNode.icon }}</span>
                <span class="node-title">{{ rootNode.name }}</span>
                <el-tag type="primary" size="small">根节点</el-tag>
              </div>
              <p class="node-description">{{ rootNode.description }}</p>

              <!-- 子节点（一级） -->
              <div v-if="rootNode.children && rootNode.children.length > 0" class="children-container">
                <div v-for="child in rootNode.children" :key="child.id" class="child-node">
                  <div class="node-card child" :style="{ borderLeftColor: child.color }">
                    <div class="node-header">
                      <span class="node-icon">{{ child.icon }}</span>
                      <span class="node-title">{{ child.name }}</span>
                      <span class="node-category">{{ child.category }}</span>
                    </div>
                    <p class="node-description">{{ child.description }}</p>

                    <!-- 孙子节点（二级） -->
                    <div v-if="child.children && child.children.length > 0" class="grandchildren-container">
                      <el-tag
                        v-for="grandchild in child.children"
                        :key="grandchild.id"
                        class="grandchild-tag"
                        :color="child.color + '20'"
                        :style="{ color: child.color, borderColor: child.color }"
                      >
                        {{ grandchild.name }}
                      </el-tag>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && knowledgeTree.length === 0" class="empty-state">
        <div class="empty-icon">📚</div>
        <h3>暂无知识数据</h3>
        <p>点击"加载知识树"按钮获取AI产品经理知识体系</p>
        <el-button type="primary" @click="loadKnowledgeTree">加载知识树</el-button>
      </div>

      <!-- 统计信息 -->
      <div v-if="knowledgeTree.length > 0" class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">🌳</div>
                <div class="stat-info">
                  <h3>总节点数</h3>
                  <p class="stat-value">{{ totalNodes }}</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">📂</div>
                <div class="stat-info">
                  <h3>分类数</h3>
                  <p class="stat-value">{{ categories.length }}</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">⏱️</div>
                <div class="stat-info">
                  <h3>预估学习时长</h3>
                  <p class="stat-value">{{ totalHours }}h</p>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">🎯</div>
                <div class="stat-info">
                  <h3>掌握节点</h3>
                  <p class="stat-value">0</p>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, View, Loading } from '@element-plus/icons-vue'
import axios from 'axios'

const loading = ref(false)
const knowledgeTree = ref<any[]>([])
const selectedCategory = ref('')

// 模拟知识树数据
const mockKnowledgeTree = [
  {
    id: 1,
    name: 'AI产品经理核心能力体系',
    description: 'AI产品经理需要掌握的五大核心能力维度',
    level: 0,
    icon: '🏆',
    color: '#1890ff',
    children: [
      {
        id: 2,
        name: '战略与市场分析',
        description: 'AI产品趋势洞察、市场分析、竞品研究、商业模式设计',
        level: 1,
        icon: '🎯',
        color: '#52c41a',
        category: '战略与市场分析',
        children: [
          { id: 3, name: 'AI行业趋势分析' },
          { id: 4, name: '主流AI产品分析' },
          { id: 5, name: '技术成熟度曲线' },
          { id: 6, name: '商业模式创新' }
        ]
      },
      {
        id: 7,
        name: '用户研究与体验设计',
        description: '用户需求分析、用户体验设计、用户测试、交互设计',
        level: 1,
        icon: '👥',
        color: '#fa8c16',
        category: '用户研究与体验设计',
        children: [
          { id: 8, name: 'AI用户画像' },
          { id: 9, name: 'Prompt工程' },
          { id: 10, name: 'AI交互设计' },
          { id: 11, name: '用户体验度量' }
        ]
      }
    ]
  }
]

// 计算属性
const totalNodes = computed(() => {
  let count = 0
  knowledgeTree.value.forEach(root => {
    count++ // 根节点
    if (root.children) {
      count += root.children.length
      root.children.forEach(child => {
        if (child.children) {
          count += child.children.length
        }
      })
    }
  })
  return count
})

const categories = computed(() => {
  const cats = new Set<string>()
  knowledgeTree.value.forEach(root => {
    if (root.children) {
      root.children.forEach(child => {
        if (child.category) {
          cats.add(child.category)
        }
      })
    }
  })
  return Array.from(cats)
})

const totalHours = computed(() => {
  return totalNodes.value * 10 // 假设每个节点10小时
})

// 方法
const loadKnowledgeTree = async () => {
  loading.value = true
  try {
    // 先尝试从后端获取
    const response = await axios.get('http://localhost:8080/api/knowledge/tree')
    if (response.data.success) {
      knowledgeTree.value = response.data.data || []
      ElMessage.success('知识树加载成功！')
    } else {
      // 如果后端没有数据，使用模拟数据
      knowledgeTree.value = mockKnowledgeTree
      ElMessage.info('使用演示数据')
    }
  } catch (error) {
    // 后端连接失败，使用模拟数据
    knowledgeTree.value = mockKnowledgeTree
    ElMessage.info('后端连接失败，使用演示数据')
  } finally {
    loading.value = false
  }
}

const showAllCategories = () => {
  ElMessage.info(`当前有 ${categories.value.length} 个分类：${categories.value.join(', ')}`)
}
</script>

<style scoped>
.knowledge {
  padding: 40px 0;
  background: #f5f5f5;
  min-height: calc(100vh - 64px - 60px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-header .icon {
  font-size: 36px;
}

.subtitle {
  color: #666;
  font-size: 16px;
}

.control-panel {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.left-controls {
  display: flex;
  gap: 12px;
}

.right-controls {
  display: flex;
  gap: 12px;
}

.loading-state {
  text-align: center;
  padding: 60px 0;
}

.loading-icon {
  font-size: 48px;
  color: #1890ff;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.knowledge-tree {
  margin-top: 30px;
}

.tree-container {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.node-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.node-card.root {
  border-left: 6px solid #1890ff;
}

.node-card.child {
  border-left: 4px solid;
}

.node-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.node-icon {
  font-size: 24px;
}

.node-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  flex: 1;
}

.node-category {
  background: #f0f0f0;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  color: #666;
}

.node-description {
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
}

.children-container {
  margin-top: 20px;
  padding-left: 20px;
  border-left: 2px dashed #e8e8e8;
}

.child-node {
  margin-bottom: 20px;
}

.grandchildren-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.grandchild-tag {
  margin: 4px;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 20px;
  color: #333;
  margin-bottom: 8px;
}

.empty-state p {
  color: #666;
  margin-bottom: 24px;
}

.stats-section {
  margin-top: 40px;
}

.stat-card {
  border-radius: 12px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  font-size: 36px;
}

.stat-info h3 {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.stat-value {
  margin: 8px 0 0;
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
}
</style>