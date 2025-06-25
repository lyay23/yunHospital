package com.neuedu.hisweb.controller.neudoc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Qualifier;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@RequestMapping("/ai")
public class AIChatController {

    private static final Logger logger = LoggerFactory.getLogger(AIChatController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${bailian.model}")
    private String model;

    @Autowired
    @Qualifier("webClient")
    private WebClient webClient;

    @Value("${bailian.api-key}")
    private String apiKey;

    @Value("${bailian.base-url}")
    private String bailianBaseUrl;


    @PostMapping("/chat")
    public String chat(@RequestParam String message) {
        String url = ollamaBaseUrl + "/api/chat";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 构建请求体
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你现在是一名专业的医院教授，你需要根据医生给病人写的病历进行分析，readme字段是主诉，present字段是现病史，presentTreat字段是现病治疗情况，history字段是既往史，allergy字段是过敏史，physique字段是体格检查");

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(systemMessage, userMessage));
        requestBody.put("stream", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        String response = restTemplate.postForObject(url, entity, String.class);
        // 你可以根据 Ollama 返回的 JSON 结构进一步解析 response
        return response;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestParam String message) {
        logger.info("开始AI流式请求（阿里云百炼），消息: {}", message);
        SseEmitter emitter = new SseEmitter(300000L); // 5分钟超时

        // AI身份设定
        String systemPrompt = "你现在是一名专业的医院教授，你需要根据医生给病人写的病历进行分析，直接分析病历的结果并予以评价，不需要思考的过程(医生没有那么多的时间看你思考)。";
        String finalPrompt = systemPrompt + message;

        // 构造百炼API请求体
        Map<String, Object> input = new HashMap<>();
        input.put("prompt", finalPrompt);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("result_format", "message");
        parameters.put("stream", true);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model); // "qwen-turbo"
        requestBody.put("input", input);
        requestBody.put("parameters", parameters);

        logger.info("百炼请求体: {}", JSON.toJSONString(requestBody));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        webClient.post()
                .uri(bailianBaseUrl)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(requestBody)
                .accept(MediaType.APPLICATION_NDJSON, MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(line -> !line.trim().isEmpty())
                .doOnNext(chunk -> {
                    try {
                        logger.debug("收到百炼响应: {}", chunk);
                        emitter.send("data: " + chunk + "\n\n");
                    } catch (Exception e) {
                        logger.error("发送SSE数据失败: {}", e.getMessage());
                        emitter.completeWithError(e);
                    }
                })
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        WebClientResponseException ex = (WebClientResponseException) error;
                        logger.error("百炼API错误: 状态码={}, 响应体={}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
                    } else {
                        logger.error("百炼流式请求出错: {}", error.getMessage());
                    }
                    emitter.completeWithError(error);
                })
                .doOnComplete(() -> {
                    logger.info("百炼AI流式请求完成");
                    emitter.complete();
                })
                .subscribe();

        return emitter;
    }
}