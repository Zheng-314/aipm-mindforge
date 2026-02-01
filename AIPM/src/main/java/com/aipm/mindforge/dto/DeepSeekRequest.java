package com.aipm.mindforge.dto;

import java.util.List;

public class DeepSeekRequest {
    private String model = "deepseek-chat";
    private List<Message> messages;
    private double temperature = 0.7;
    private int max_tokens = 2000;

    // 静态内部类
    public static class Message {
        private String role;
        private String content;

        // 构造方法
        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        // Getter和Setter
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    // Getter和Setter
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public int getMax_tokens() { return max_tokens; }
    public void setMax_tokens(int max_tokens) { this.max_tokens = max_tokens; }
}