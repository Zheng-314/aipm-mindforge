import request from './axios'

export interface Note {
  id?: number
  userId?: number
  user?: any
  title: string
  content?: string
  sourceUrl?: string
  keywords?: string
  difficultyLevel?: number
  learningStatus?: number
  aiProcessedData?: string
  createdAt?: string
  updatedAt?: string
}

export const noteApi = {
  // 创建笔记
  createNote(note: Note) {
    return request.post('/notes', note)
  },

  // 获取用户的所有笔记
  getUserNotes(userId: number) {
    return request.get(`/notes/user/${userId}`)
  },

  // 根据ID获取笔记
  getNoteById(id: number) {
    return request.get(`/notes/${id}`)
  },

  // 更新笔记
  updateNote(id: number, note: Note) {
    return request.put(`/notes/${id}`, note)
  },

  // 删除笔记
  deleteNote(id: number) {
    return request.delete(`/notes/${id}`)
  },

  // 搜索笔记
  searchNotes(keyword: string) {
    return request.get('/notes/search', { params: { keyword } })
  },

  // AI处理笔记
  aiProcessNote(id: number) {
    return request.post(`/notes/${id}/ai-process`)
  },

  // 获取笔记统计
  getNoteStats(userId: number) {
    return request.get(`/notes/user/${userId}/stats`)
  }
}