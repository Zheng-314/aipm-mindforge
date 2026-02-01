package com.aipm.mindforge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "🚀 AIPM MindForge 后端启动成功！";
    }

    @GetMapping("/status")
    public String status() {
        return "✅ 服务器运行正常，时间：" + new java.util.Date();
    }
}