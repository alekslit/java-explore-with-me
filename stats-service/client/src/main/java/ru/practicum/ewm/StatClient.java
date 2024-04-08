package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;

@Service
public class StatClient {
    private final RestTemplate rest;

    @Autowired
    public StatClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        this.rest = builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    /*---------------Основные методы---------------*/
    public ResponseEntity<StatDto> saveStat(StatDto statDto) {
        return makeAndSendRequest(HttpMethod.POST, "/hit", statDto);
    }

    /*---------------Вспомогательные методы---------------*/
    private ResponseEntity<StatDto> makeAndSendRequest(HttpMethod method, String path, StatDto body) {
        HttpEntity<StatDto> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<StatDto> ewmServerResponse;
        try {
            ewmServerResponse = rest.exchange(path, method, requestEntity, StatDto.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(body);
        }

        return prepareGatewayResponse(ewmServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private static ResponseEntity<StatDto> prepareGatewayResponse(ResponseEntity<StatDto> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}