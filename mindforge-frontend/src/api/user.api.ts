import request from './axios'

export interface User {
  id?: number
  email: string
  username: string
  avatar?: string
  createdAt?: string
}

export interface LoginData {
  email: string
  password: string
}

export const userApi = {
  // 注册用户
  register(user: User) {
    return request.post('/users/register', user)
  },

  // 获取所有用户
  getUsers() {
    return request.get('/users')
  },

  // 根据ID获取用户
  getUserById(id: number) {
    return request.get(`/users/${id}`)
  },

  // 创建示例用户
  initSampleUsers() {
    return request.post('/users/init-sample')
  }
}