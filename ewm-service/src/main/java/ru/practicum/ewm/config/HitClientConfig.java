package ru.practicum.ewm.config;

import client.HitClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HitClientConfig {
    @Value("${stats-service.url}")
    private String url;

    @Bean
    public HitClient hitClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        //comment
        return new HitClient(url, builder);
    }
}
