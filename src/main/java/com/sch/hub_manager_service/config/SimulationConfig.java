package com.sch.hub_manager_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SimulationConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
