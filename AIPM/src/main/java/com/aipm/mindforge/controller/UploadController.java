package com.aipm.mindforge.controller;

import com.aipm.mindforge.entity.User;
import com.aipm.mindforge.repository.UserRepository;
import com.aipm.mindforge.service.AuthService;
import com.aipm.mindforge.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:5173")
public class UploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {

        try {
            // 验证token并获取用户
            if (!token.startsWith("Bearer ")) {
                throw new RuntimeException("无效的token格式");
            }

            String jwtToken = token.substring(7);
            User currentUser = authService.getCurrentUser(jwtToken);

            // 删除旧头像
            fileUploadService.deleteOldAvatar(currentUser.getAvatarUrl());

            // 上传新头像
            String avatarUrl = fileUploadService.uploadAvatar(file, currentUser.getId());

            // 更新用户头像URL
            currentUser.setAvatarUrl(avatarUrl);
            userRepository.save(currentUser);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "头像上传成功");
            response.put("avatarUrl", avatarUrl);
            response.put("user", currentUser);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "头像上传失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}