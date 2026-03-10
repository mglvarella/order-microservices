package com.mglvarella.gatewayapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${order-api.base-url}")
    private String orderApiBaseUrl;

    @Bean
    public RestClient orderApiRestClient() {
        return RestClient.builder()
                .baseUrl(orderApiBaseUrl)
                .build();
    }
}
