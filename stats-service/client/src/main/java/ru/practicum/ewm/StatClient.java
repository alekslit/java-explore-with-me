package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
@Slf4j
public class StatClient {
    private final RestTemplate rest;

    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    /*---------------Основные методы---------------*/
    public void saveStat(StatDto statDto) {
        log.debug("Отправляем запрос на сохранение статистики (client).");
        rest.postForEntity("/hit", statDto, StatDto.class).getBody();
    }

    public Long getUniqueViewsByUri(String uri) {
        log.debug("Отправляем запрос на получение количества просмотров (client).");
        String path = "/stats/views?uri={uri}";
        ResponseEntity<Long> response = rest
                .exchange(path, HttpMethod.GET, null, Long.class, uri);

        return response.getBody();
    }
}