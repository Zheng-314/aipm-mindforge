import request from './axios'

export interface KnowledgeNode {
  id?: number
  name: string
  description?: string
  parent?: KnowledgeNode
  level: number
  icon?: string
  color?: string
  category?: string
  sortOrder?: number
  estimatedHours?: number
  children?: KnowledgeNode[]
}

export const knowledgeApi = {
  // 获取知识树
  getTree() {
    return request.get('/knowledge/tree')
  },

  // 获取所有节点
  getNodes() {
    return request.get('/knowledge/nodes')
  },

  // 根据ID获取节点
  getNodeById(id: number) {
    return request.get(`/knowledge/nodes/${id}`)
  },

  // 根据分类获取节点
  getNodesByCategory(category: string) {
    return request.get(`/knowledge/category/${category}`)
  },

  // 创建节点
  createNode(node: KnowledgeNode) {
    return request.post('/knowledge/nodes', node)
  },

  // 更新节点
  updateNode(id: number, node: KnowledgeNode) {
    return request.put(`/knowledge/nodes/${id}`, node)
  },

  // 删除节点
  deleteNode(id: number) {
    return request.delete(`/knowledge/nodes/${id}`)
  }
}