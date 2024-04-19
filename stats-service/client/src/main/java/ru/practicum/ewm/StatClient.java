package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<StatDto> saveStat(StatDto statDto) {
        return makeAndSendPostRequest(HttpMethod.POST, "/hit", statDto);
    }

    public List<ViewStats> getStats(String start,
                                    String end,
                                    List<String> uris,
                                    Boolean unique) {
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "1.");
        String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "2.");
        String urisToString = uris.toString();
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "3.");
        urisToString = urisToString.substring(1, urisToString.length() - 1).replaceAll(" ", "");
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "4.");
        ResponseEntity<List<ViewStats>> response = rest.exchange(
                path,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStats>>() {},
                start, end, urisToString, unique
        );
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "5.");

        return response.getBody();
    }
/*    public ResponseEntity<List<ViewStats>> getStats(String start,
                                                    String end,
                                                    List<String> uris,
                                                    Boolean unique) {
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 22);
        String path = "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        String urisToString = uris.toString();
        urisToString = urisToString.substring(1, urisToString.length() - 1).replaceAll(" ", "");
        Map<String, Object> parameters = new HashMap<>();
        log.debug("\n\n\n!!!!\n\n\n{}{}\n\n\n!!!!\n\n\n", 2.1, urisToString);
        parameters.put("start", start);
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 2.2);
        parameters.put("end", end);
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 2.3);
        parameters.put("uris", urisToString);
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 2.4);
        parameters.put("unique", unique);
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 2.5);
*//*        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique
        );*//*
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 333);
        return makeAndSendGetRequest(HttpMethod.GET, path, parameters);
    }*/

    /*---------------Вспомогательные методы---------------*/
    private ResponseEntity<StatDto> makeAndSendPostRequest(HttpMethod method,
                                                           String path,
                                                           StatDto body) {
        HttpEntity<StatDto> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<StatDto> ewmServerResponse;
        try {
            ewmServerResponse = rest.exchange(path, method, requestEntity, StatDto.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(body);
        }

        return preparePostResponse(ewmServerResponse);
    }

    private ResponseEntity<List<ViewStats>> makeAndSendGetRequest(HttpMethod method,
                                                                  String path,
                                                                  Map<String, Object> parameters) {
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 4444);
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, defaultHeaders());
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 55555);

        ResponseEntity<List<ViewStats>> ewmServerResponse;
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 666666);

        try {
            ewmServerResponse = rest.exchange(path, method, requestEntity,
                    new ParameterizedTypeReference<List<ViewStats>>() {}, parameters);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        }
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 7777777);

        return prepareGetResponse(ewmServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        return headers;
    }

    private static ResponseEntity<StatDto> preparePostResponse(ResponseEntity<StatDto> response) {
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 88888888);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", 999999999);

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "10");

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "11 11");

        return responseBuilder.build();
    }

    private static ResponseEntity<List<ViewStats>> prepareGetResponse(ResponseEntity<List<ViewStats>> response) {
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "12 12 12");

        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "13 13 13 13");

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "14 14 14 14 14");

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        log.debug("\n\n\n!!!!\n\n\n{}\n\n\n!!!!\n\n\n", "15 15 15 15 15 15");

        return responseBuilder.build();
    }
}