package com.bicap.farm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    // Tạo ra "trình duyệt" RestTemplate để code dùng chung
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
