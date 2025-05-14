package com.fisa.appcard.config.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class PgClientConfig {

    @Value("${app.pg.token}")
    private String token;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }
}
