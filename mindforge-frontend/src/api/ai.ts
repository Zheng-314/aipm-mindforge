// src/api/ai.ts
import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080/api', // 根据你的后端端口调整
  timeout: 30000, // 30秒超时，AI调用可能需要更长时间
});

// AI相关API
export const aiApi = {
  // 测试AI连接
  testConnection: () => {
    return api.get('/ai/test');
  },

  // 分析笔记内容
  analyzeNote: (content: string) => {
    // 注意：这里直接发送字符串，不是JSON对象
    return api.post('/ai/analyze-note', content, {
      headers: {
        'Content-Type': 'text/plain' // 重要：告诉后端这是纯文本
      }
    });
  }
};