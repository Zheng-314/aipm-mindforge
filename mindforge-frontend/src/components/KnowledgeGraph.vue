<!-- src/components/KnowledgeGraph.vue -->
<template>
  <div ref="chartRef" class="knowledge-graph" style="width: 100%; height: 600px;"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

const initChart = async () => {
  if (!chartRef.value) return

  // 获取知识节点数据
  try {
    const response = await axios.get('/knowledge')
    const nodes = response.data

    // 构建树形数据
    const treeData = buildTreeData(nodes)

    // 初始化图表
    chartInstance = echarts.init(chartRef.value)

    const option = {
      tooltip: {
        trigger: 'item',
        triggerOn: 'mousemove',
        formatter: function(params: any) {
          return `
            <div style="padding: 8px;">
              <strong>${params.data.name}</strong><br/>
              ${params.data.description || ''}<br/>
              <span style="color: #666">难度: ${getLevelText(params.data.level)}</span>
            </div>
          `
        }
      },
      series: [
        {
          type: 'tree',
          data: [treeData],
          top: '1%',
          left: '7%',
          bottom: '1%',
          right: '20%',
          symbolSize: 10,
          label: {
            position: 'left',
            verticalAlign: 'middle',
            align: 'right',
            fontSize: 14,
            fontWeight: 'bold'
          },
          leaves: {
            label: {
              position: 'right',
              verticalAlign: 'middle',
              align: 'left'
            }
          },
          emphasis: {
            focus: 'descendant'
          },
          expandAndCollapse: true,
          animationDuration: 550,
          animationDurationUpdate: 750,
          lineStyle: {
            color: '#ccc',
            width: 2
          },
          itemStyle: {
            color: function(params: any) {
              const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399']
              return colors[params.data.level % colors.length]
            },
            borderColor: '#fff',
            borderWidth: 2
          }
        }
      ]
    }

    chartInstance.setOption(option)

    // 窗口大小变化时重绘
    window.addEventListener('resize', handleResize)
  } catch (error) {
    console.error('加载知识图谱失败:', error)
  }
}

const buildTreeData = (nodes: any[]) => {
  // 找到根节点
  const rootNode = nodes.find((node: any) => node.parentId == null)
  if (!rootNode) return { name: '知识图谱' }

  const buildNode = (node: any) => {
    const children = nodes.filter((n: any) => n.parentId === node.id)
    return {
      name: node.name,
      description: node.description,
      level: node.level,
      children: children.map(buildNode)
    }
  }

  return buildNode(rootNode)
}

const getLevelText = (level: number) => {
  switch(level) {
    case 1: return '入门'
    case 2: return '基础'
    case 3: return '进阶'
    case 4: return '高级'
    default: return '未知'
  }
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  initChart()
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
    window.removeEventListener('resize', handleResize)
  }
})
</script>

<style scoped>
.knowledge-graph {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
</style>