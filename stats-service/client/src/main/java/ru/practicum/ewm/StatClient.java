package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;

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
    public StatDto saveStat(StatDto statDto) {
        log.debug("Отправляем запрос на сохранение статистики (client).");
        return rest.postForEntity("/hit", statDto, StatDto.class).getBody();
    }

    public List<ViewStats> getStats(String start,
                                    String end,
                                    List<String> uris,
                                    Boolean unique) {
        log.debug("Отправляем запрос на получение статистики (client).");
        String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        String urisToString = uris.toString();
        urisToString = urisToString.substring(1, urisToString.length() - 1).replaceAll(" ", "");
        ResponseEntity<List<ViewStats>> response = rest.exchange(
                path,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStats>>() {
                },
                start, end, urisToString, unique
        );

        return response.getBody();
    }
}