package com.aipm.mindforge.dto;

import java.util.List;

public class DeepSeekResponse {
    private List<Choice> choices;

    // 静态内部类
    public static class Choice {
        private Message message;

        // Getter和Setter
        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
    }

    public static class Message {
        private String content;

        // Getter和Setter
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    // 获取AI回复内容的方法
    public String getAiResponse() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).getMessage().getContent();
        }
        return "未能获取AI回复";
    }

    // Getter和Setter
    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }
}