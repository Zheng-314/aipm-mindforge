package com.aipm.mindforge.service;

import com.aipm.mindforge.dto.DeepSeekRequest;
import com.aipm.mindforge.dto.DeepSeekResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
public class AIService {

    @Autowired
    private WebClient deepSeekWebClient;

    /**
     * 分析笔记内容，提取知识点和建议
     * @param noteContent 笔记内容
     * @return AI分析结果
     */
    public String analyzeNote(String noteContent) {
        try {
            // 构建请求
            DeepSeekRequest request = new DeepSeekRequest();

            // 构建系统提示词 - 这是关键！
            String systemPrompt = "你是一个AI产品经理学习助手。请分析以下学习笔记，完成以下任务：\n" +
                    "1. 提取3-5个核心知识点\n" +
                    "2. 将这些知识点关联到AI产品经理的知识体系\n" +
                    "3. 给出下一步学习建议\n" +
                    "4. 用中文回复，格式清晰易读\n\n" +
                    "笔记内容：";

            DeepSeekRequest.Message systemMessage = new DeepSeekRequest.Message("system", systemPrompt);
            DeepSeekRequest.Message userMessage = new DeepSeekRequest.Message("user", noteContent);

            request.setMessages(Arrays.asList(systemMessage, userMessage));

            // 调用DeepSeek API
            DeepSeekResponse response = deepSeekWebClient.post()
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(DeepSeekResponse.class)
                    .block(); // 这里使用阻塞调用简化代码

            if (response != null) {
                return response.getAiResponse();
            }

            return "AI分析失败：未收到响应";

        } catch (Exception e) {
            e.printStackTrace();
            return "AI分析失败：" + e.getMessage();
        }
    }

    /**
     * 简单的测试方法 - 用于验证API连接
     */
    public String testConnection() {
        try {
            DeepSeekRequest request = new DeepSeekRequest();
            DeepSeekRequest.Message message = new DeepSeekRequest.Message("user", "请回复'AI连接成功'");
            request.setMessages(Arrays.asList(message));

            DeepSeekResponse response = deepSeekWebClient.post()
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(DeepSeekResponse.class)
                    .block();

            if (response != null) {
                return response.getAiResponse();
            }

            return "连接测试失败";
        } catch (Exception e) {
            return "连接异常：" + e.getMessage();
        }
    }
}