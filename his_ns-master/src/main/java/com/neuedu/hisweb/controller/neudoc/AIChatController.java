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
import reactor.core.publisher.Flux;

import jakarta.servlet.http.HttpServletResponse;
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


    @GetMapping("/chat")
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

    @GetMapping(value = "/stream")
    public Flux<String> chatStream(@RequestParam String message, HttpServletResponse response) {
        // 强制设置响应头
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        logger.info("开始AI流式请求（阿里云百炼），消息: {}", message);

        String systemPrompt = "你现在是一名专业的医院教授，你需要根据医生给病人写的病历进行分析，直接分析病历的结果并予以评价，不需要思考的过程(医生没有那么多的时间看你思考)。";
        String finalPrompt = systemPrompt + message;

        Map<String, Object> input = new HashMap<>();
        input.put("prompt", finalPrompt);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("result_format", "message");
        parameters.put("stream", true);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("input", input);
        requestBody.put("parameters", parameters);

        logger.info("百炼请求体: {}", JSON.toJSONString(requestBody));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        return webClient.post()
                .uri(bailianBaseUrl)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(requestBody)
                .accept(MediaType.TEXT_EVENT_STREAM) //明确要求流式响应
                .retrieve()
                .bodyToFlux(String.class)
                .map(chunk -> "data: " + chunk + "\n\n") // 格式化为SSE
                .doOnSubscribe(subscription -> logger.info("成功订阅百炼API响应流"))
                .doOnNext(data -> logger.debug("发送给客户端的数据: {}", data))
                .doOnError(error -> logger.error("处理百炼流时出错: ", error))
                .doOnComplete(() -> logger.info("百炼流处理完成"));
    }
}