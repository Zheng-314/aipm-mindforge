package com.aipm.mindforge.controller;

import com.aipm.mindforge.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // 允许前端访问
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * 测试AI连接
     */
    @GetMapping("/test")
    public String testAIConnection() {
        return aiService.testConnection();
    }
    @CrossOrigin(origins = "http://localhost:5173")  // 添加这行
    /**
     * 分析笔记内容
     */
    @PostMapping("/analyze-note")
    public String analyzeNote(@RequestBody String noteContent) {
        // 简单验证
        if (noteContent == null || noteContent.trim().isEmpty()) {
            return "请输入笔记内容";
        }

        // 限制内容长度，避免API消耗过大
        if (noteContent.length() > 5000) {
            noteContent = noteContent.substring(0, 5000) + "...(内容过长，已截断)";
        }

        return aiService.analyzeNote(noteContent);
    }
}