package com.neuedu.hisweb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${ollama.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 使用 RestTemplate 调用 Ollama API 进行聊天
     */
    public String chat(String message) {
        try {
            String url = ollamaBaseUrl + "/api/chat";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建消息体
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你现在是一名专业的医院导诊助手。");

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(systemMessage, userMessage));
            requestBody.put("stream", false); // 非流式响应

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String response = restTemplate.postForObject(url, entity, String.class);

            // 解析响应
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode messageNode = rootNode.path("message").path("content");
            return messageNode.asText();

        } catch (Exception e) {
            // 简单错误处理
            e.printStackTrace();
            return "抱歉，AI 服务暂时不可用: " + e.getMessage();
        }
    }

    /**
     * 生成文本
     */
    public String generateText(String prompt) {
        try {
            String url = ollamaBaseUrl + "/api/generate";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("prompt", "现在你不是deepseek,而是你是一名医院的助手。\n\n" + prompt);
            requestBody.put("stream", false);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            String response = restTemplate.postForObject(url, request, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            return jsonNode.get("response").asText();
        } catch (Exception e) {
            return "抱歉，AI 服务暂时不可用: " + e.getMessage();
        }
    }

    /**
     * 获取可用模型
     */
    public List<String> getAvailableModels() {
         return Collections.singletonList("当前配置模型: " + model);
    }

    /**
     * 检查模型是否可用
     */
    public boolean isModelAvailable(String modelName) {
        try {
            List<String> models = getAvailableModels();
            return models.contains(modelName);
        } catch (Exception e) {
            return false;
        }
    }
} 