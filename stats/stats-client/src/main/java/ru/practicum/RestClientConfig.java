package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    private final String serverUrl;

    public RestClientConfig(@Value("${client.url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Bean
    public RestStatsClient itemClient() {
        return new RestStatsClient(serverUrl, RestClient.create(serverUrl));
    }

}
