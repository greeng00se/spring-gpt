package com.example.gpttest.config;

import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ChatGPTConfig {

    @Value("${gpt.token}")
    private String token;

    @Bean
    public OpenAiService openAiService() {
        log.info("token : {}을 활용한 OpenAiService 을 생성합니다.", token);
        return new OpenAiService(token, Duration.ofSeconds(60));
    }
}
