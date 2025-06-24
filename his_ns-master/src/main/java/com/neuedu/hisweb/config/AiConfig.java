package com.neuedu.hisweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

@Configuration
public class AiConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000) // 连接超时60秒
                .responseTimeout(Duration.ofMinutes(5)) // 响应超时5分钟
                .doOnConnected(conn -> 
                    conn.addHandlerLast(new ReadTimeoutHandler(300)) // 读取超时5分钟
                        .addHandlerLast(new WriteTimeoutHandler(60)) // 写入超时60秒
                );
        
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
