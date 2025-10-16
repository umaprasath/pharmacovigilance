package com.pharmacovigilance.mcpagent.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class OpenAiConfig {
    
    @Value("${openai.api-key}")
    private String apiKey;
    
    @Value("${openai.timeout:60s}")
    private Duration timeout;
    
    @Bean
    public OpenAiService openAiService() {
        log.info("Initializing OpenAI service with timeout: {}", timeout);
        return new OpenAiService(apiKey, timeout);
    }
}


