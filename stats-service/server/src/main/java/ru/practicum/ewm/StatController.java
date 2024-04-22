package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.NotAvailableException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.ewm.exception.NotAvailableException.NOT_AVAILABLE_DATE_TIME_ADVICE;
import static ru.practicum.ewm.exception.NotAvailableException.NOT_AVAILABLE_DATE_TIME_MESSAGE;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService service;

    /*---------------Основные методы---------------*/
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatDto saveStat(@RequestBody StatDto statDto) {
        return StatMapper.mapToStatDto(service.saveStat(statDto));
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam String start,
                                    @RequestParam String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        checkRequestDateTime(start, end);
        return service.getStats(start, end, uris, unique);
    }

    @GetMapping("/stats/views")
    public Long getUniqueViewsByUri(@RequestParam String uri) {
        return service.getUniqueViewsByUri(uri);
    }

    /*---------------Вспомогательные методы (валидация запроса)---------------*/
    private void checkRequestDateTime(String start, String end) {
        if (start == null || end == null) {
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        if (startTime.isAfter(endTime)) {
            log.debug("{}: {}", NotAvailableException.class.getSimpleName(), NOT_AVAILABLE_DATE_TIME_MESSAGE);
            throw new NotAvailableException(NOT_AVAILABLE_DATE_TIME_MESSAGE, NOT_AVAILABLE_DATE_TIME_ADVICE);
        }
    }
}