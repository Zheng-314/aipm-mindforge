package com.aipm.mindforge.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.access-path}")
    private String accessPath;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.address:localhost}")
    private String serverAddress;

    // 允许的文件类型
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    // 最大文件大小：5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 上传头像文件
     */
    public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        // 验证文件
        validateFile(file);

        // 创建用户目录
        String userDir = uploadDir + "/avatars/user_" + userId;
        Path userPath = Paths.get(userDir);
        if (!Files.exists(userPath)) {
            Files.createDirectories(userPath);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + "." + fileExtension;

        // 保存文件
        Path filePath = userPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);

        // 返回访问URL
        return buildFileUrl("avatars/user_" + userId + "/" + newFilename);
    }

    /**
     * 验证上传的文件
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过5MB");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);

        boolean allowed = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            throw new RuntimeException("不支持的文件类型，仅支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 构建文件访问URL
     */
    private String buildFileUrl(String filePath) {
        return "http://" + serverAddress + ":" + serverPort + accessPath + "/" + filePath;
    }

    /**
     * 删除旧的头像文件 - 这个是你缺少的方法！
     */
    public void deleteOldAvatar(String oldAvatarUrl) {
        if (oldAvatarUrl == null || oldAvatarUrl.isEmpty()) {
            return;
        }

        try {
            // 从URL中提取文件路径
            String filePath = extractFilePathFromUrl(oldAvatarUrl);
            if (filePath != null) {
                Path path = Paths.get(uploadDir, filePath);
                if (Files.exists(path)) {
                    Files.delete(path);
                }
            }
        } catch (Exception e) {
            // 删除失败不影响主要功能
            System.err.println("删除旧头像失败: " + e.getMessage());
        }
    }

    /**
     * 从URL中提取文件路径
     */
    private String extractFilePathFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // 查找访问路径前缀
        String prefix = accessPath + "/";
        int index = url.indexOf(prefix);
        if (index != -1) {
            return url.substring(index + prefix.length());
        }

        return null;
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            String filePath = extractFilePathFromUrl(fileUrl);
            if (filePath != null) {
                Path path = Paths.get(uploadDir, filePath);
                return Files.exists(path);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取默认头像URL
     */
    public String getDefaultAvatarUrl() {
        // 这里可以返回一个默认头像URL
        // 或者使用用户名的首字母生成头像
        return buildFileUrl("default/default-avatar.png");
    }
}