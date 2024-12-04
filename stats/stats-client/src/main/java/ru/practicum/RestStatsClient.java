package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class RestStatsClient implements StatsClient {
    RestClient restClient;
    String serverUrl;

    RestStatsClient(String serverUrl, RestClient restClient) {
        this.serverUrl = serverUrl;
        this.restClient = restClient;
    }

    @Override
    public void hit(ParamHitDto paramHitDto) {
        restClient
                .post()
                .uri(serverUrl + "/hit")
                .body(paramHitDto)
                .retrieve()
                .toEntity(String.class);
    }

    @Override
    public List<StatsDto> stats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        try {
            Optional<List<String>> optUris = uris != null ? Optional.of(Arrays.asList(uris)) : Optional.empty();
            var statsUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                    .queryParam("start", start)
                    .queryParam("end", end)
                    .queryParamIfPresent("uris", optUris)
                    .queryParam("unique", unique)
                    .build()
                    .toUri();
            return restClient.get()
                    .uri(statsUri)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Throwable ex) {
            log.warn("Ошибка при вызове метода stats", ex);
        }
        return List.of();
    }
}
