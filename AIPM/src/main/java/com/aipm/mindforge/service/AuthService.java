package com.aipm.mindforge.service;

import com.aipm.mindforge.entity.User;
import com.aipm.mindforge.repository.UserRepository;
import com.aipm.mindforge.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 注册用户
    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已注册
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已注册");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // 登录
    public String login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成JWT token
        return jwtUtil.generateToken(user.getUsername());
    }

    // 获取当前用户
    public User getCurrentUser(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null) {
            throw new RuntimeException("Token无效");
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        return userOpt.get();
    }
}