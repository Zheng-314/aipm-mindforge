package com.aipm.mindforge.controller;

import com.aipm.mindforge.entity.User;
import com.aipm.mindforge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")  // 添加这行
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 1. 创建用户
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("邮箱已被注册");
        }

        // 设置创建时间
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    // 2. 获取所有用户
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 3. 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. 测试用：创建几个示例用户
    @PostMapping("/init-sample")
    public String initSampleUsers() {
        User user1 = new User();
        user1.setEmail("aipm@example.com");
        user1.setUsername("AI产品经理学员");
        user1.setAvatar("https://example.com/avatar1.jpg");

        User user2 = new User();
        user2.setEmail("pm@example.com");
        user2.setUsername("转型产品经理");
        user2.setAvatar("https://example.com/avatar2.jpg");

        userRepository.save(user1);
        userRepository.save(user2);

        return "创建了2个示例用户";
    }
}