package org.copyria.orderapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean
    public RestClient privatBankRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5")
                .build();
    }
}
