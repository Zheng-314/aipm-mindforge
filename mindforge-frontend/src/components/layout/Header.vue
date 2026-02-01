<template>
  <el-header class="header">
    <div class="header-content">
      <div class="logo">
        <router-link to="/">
          <span class="logo-text">AIPM MindForge</span>
        </router-link>
      </div>

      <el-menu
        :default-active="activeIndex"
        class="nav-menu"
        mode="horizontal"
        :router="true"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/knowledge">知识图谱</el-menu-item>
        <el-menu-item index="/notes">智能笔记</el-menu-item>
        <el-menu-item index="/profile">学习进度</el-menu-item>
      </el-menu>

      <div class="user-info" v-if="isLoggedIn">
        <el-dropdown @command="handleCommand">
          <div class="user-dropdown">
            <el-avatar :size="36" :src="userAvatar" @click="showAvatarUpload = true">
              {{ userInitials }}
            </el-avatar>
            <span class="username">{{ username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="uploadAvatar">
                <el-icon><Upload /></el-icon>更换头像
              </el-dropdown-item>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <div class="auth-buttons" v-else>
        <el-button type="text" @click="$router.push('/login')">登录</el-button>
        <el-button type="primary" @click="$router.push('/login?mode=register')">
          注册
        </el-button>
      </div>
    </div>

    <!-- 头像上传对话框 -->
    <el-dialog
      v-model="showAvatarUpload"
      title="上传头像"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="avatar-upload-container">
        <!-- 预览区域 -->
        <div class="preview-section">
          <div class="preview-title">头像预览</div>
          <div class="avatar-preview">
            <img
              v-if="previewUrl"
              :src="previewUrl"
              alt="头像预览"
              class="preview-image"
            />
            <div v-else class="preview-placeholder">
              <el-icon size="60" color="#909399"><UserFilled /></el-icon>
              <div class="placeholder-text">暂无图片</div>
            </div>
          </div>
        </div>

        <!-- 上传区域 -->
        <div class="upload-section">
          <div class="upload-title">选择图片</div>
          <el-upload
            class="avatar-uploader"
            action=""
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :http-request="uploadAvatar"
            accept=".jpg,.jpeg,.png,.gif,.webp"
          >
            <div class="upload-area">
              <el-icon size="40" color="#409EFF"><Plus /></el-icon>
              <div class="upload-text">
                <div>点击上传</div>
                <div class="upload-subtext">或将文件拖拽到此处</div>
              </div>
            </div>
          </el-upload>
        </div>

        <!-- 提示信息 -->
        <div class="upload-tips">
          <div class="tip-item">
            <el-icon color="#67C23A"><CircleCheck /></el-icon>
            <span>支持 JPG、PNG、GIF、WebP 格式</span>
          </div>
          <div class="tip-item">
            <el-icon color="#E6A23C"><InfoFilled /></el-icon>
            <span>文件大小不超过 5MB</span>
          </div>
          <div class="tip-item">
            <el-icon color="#909399"><View /></el-icon>
            <span>建议尺寸 200x200 像素以上</span>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelUpload" :disabled="uploading">取消</el-button>
          <el-button
            type="primary"
            @click="confirmUpload"
            :loading="uploading"
            :disabled="!selectedFile"
          >
            {{ uploading ? '上传中...' : '确认上传' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </el-header>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  User,
  SwitchButton,
  Upload,
  Plus,
  UserFilled,
  CircleCheck,
  InfoFilled,
  View
} from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const activeIndex = ref('/')
const username = ref('')
const userAvatar = ref('')

// 头像上传相关状态
const showAvatarUpload = ref(false)
const selectedFile = ref<File | null>(null)
const previewUrl = ref('')
const uploading = ref(false)

const isLoggedIn = computed(() => {
  return !!localStorage.getItem('token')
})

const userInitials = computed(() => {
  if (!username.value) return 'U'
  return username.value.charAt(0).toUpperCase()
})

// 获取用户信息
const fetchUserInfo = async () => {
  const token = localStorage.getItem('token')
  if (!token) return

  try {
    const response = await axios.get('/auth/me')
    username.value = response.data.username
    userAvatar.value = response.data.avatar || ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 上传前验证
const beforeAvatarUpload = (file: File) => {
  const isImage = /\.(jpg|jpeg|png|gif|webp)$/i.test(file.name)
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片格式!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }

  // 预览图片
  selectedFile.value = file
  previewUrl.value = URL.createObjectURL(file)
  return false // 阻止默认上传
}

const uploadAvatar = async () => {
  if (!selectedFile.value) {
    ElMessage.error('请先选择图片')
    return
  }

  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录')
    return
  }

  const formData = new FormData()
  formData.append('file', selectedFile.value)  // 注意：这里必须是 'file'

  try {
    uploading.value = true

    // 关键修改：使用正确的URL
    const response = await axios.post('http://localhost:8080/api/upload/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${token}`
      }
    })

    if (response.data.success) {
      ElMessage.success('头像上传成功！')

      // 更新头像
      userAvatar.value = response.data.avatarUrl
      localStorage.setItem('avatar', response.data.avatarUrl)

      showAvatarUpload.value = false
      resetUploadState()
    } else {
      ElMessage.error(response.data.message || '上传失败')
    }
  } catch (error: any) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败: ' + (error.response?.data?.message || error.message))
  } finally {
    uploading.value = false
  }
}

// 模拟上传（如果后端API还没实现）
const mockUploadAvatar = async () => {
  if (!selectedFile.value) {
    ElMessage.error('请先选择图片')
    return
  }

  try {
    uploading.value = true
    await new Promise(resolve => setTimeout(resolve, 1500)) // 模拟上传延迟

    // 生成模拟的URL（实际项目中应该使用后端返回的URL）
    const mockAvatarUrl = `https://ui-avatars.com/api/?name=${encodeURIComponent(username.value)}&background=1890ff&color=fff&size=150`

    userAvatar.value = mockAvatarUrl
    localStorage.setItem('avatar', mockAvatarUrl)

    ElMessage.success('头像更新成功（模拟）')
    showAvatarUpload.value = false
    resetUploadState()
  } catch (error) {
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

// 确认上传
const confirmUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.error('请先选择图片')
    return
  }

  try {
    // 先尝试调用真实API，如果失败则使用模拟
    await uploadAvatar()
  } catch {
    // 如果真实API失败，使用模拟上传
    await mockUploadAvatar()
  }
}

// 取消上传
const cancelUpload = () => {
  if (uploading.value) {
    ElMessageBox.confirm('头像正在上传中，确定要取消吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      showAvatarUpload.value = false
      resetUploadState()
    })
  } else {
    showAvatarUpload.value = false
    resetUploadState()
  }
}

// 重置上传状态
const resetUploadState = () => {
  selectedFile.value = null
  previewUrl.value = ''
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
  }
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  if (command === 'uploadAvatar') {
    showAvatarUpload.value = true
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    handleLogout()
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('avatar')
    delete axios.defaults.headers.common['Authorization']
    username.value = ''
    userAvatar.value = ''
    ElMessage.success('已退出登录')
    router.push('/login')
  })
}

// 监听路由变化
router.afterEach((to) => {
  activeIndex.value = to.path
})

onMounted(() => {
  if (isLoggedIn.value) {
    // 从localStorage恢复头像
    const savedAvatar = localStorage.getItem('avatar')
    if (savedAvatar) {
      userAvatar.value = savedAvatar
    }
    fetchUserInfo()
  }
})
</script>

<style scoped>
.header {
  background: white;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  height: 64px;
  display: flex;
  align-items: center;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #409EFF;
  text-decoration: none;
}

.nav-menu {
  border-bottom: none;
  flex: 1;
  justify-content: center;
}

.nav-menu .el-menu-item {
  font-weight: 500;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.user-dropdown .el-avatar {
  margin-right: 8px;
}

.username {
  margin-right: 6px;
  font-weight: 500;
  color: #303133;
}

.auth-buttons {
  display: flex;
  align-items: center;
  gap: 12px;
}

.auth-buttons .el-button {
  font-weight: 500;
}

.el-dropdown-menu {
  margin-top: 4px;
}

.el-dropdown-menu__item {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 头像上传对话框样式 */
.avatar-upload-container {
  padding: 10px 0;
}

.preview-section,
.upload-section {
  margin-bottom: 24px;
}

.preview-title,
.upload-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  font-weight: 500;
}

.avatar-preview {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px dashed #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-placeholder {
  text-align: center;
  color: #909399;
}

.placeholder-text {
  font-size: 12px;
  margin-top: 8px;
}

.avatar-uploader {
  width: 100%;
}

.upload-area {
  width: 100%;
  padding: 40px 20px;
  border: 2px dashed #409EFF;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #f5f7fa;
}

.upload-area:hover {
  border-color: #67c23a;
  background: #f0f9eb;
}

.upload-text {
  margin-top: 12px;
}

.upload-subtext {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.upload-tips {
  background: #f6f8fa;
  border-radius: 6px;
  padding: 16px;
  margin-top: 20px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 13px;
  color: #606266;
}

.tip-item:last-child {
  margin-bottom: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>