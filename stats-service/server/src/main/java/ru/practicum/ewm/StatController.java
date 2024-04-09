package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {
    private final StatService service;

    /*---------------Основные методы---------------*/
    @PostMapping("/hit")
    public StatDto saveStat(@RequestBody StatDto statDto) {
        return StatMapper.mapToStatDto(service.saveStat(statDto));
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam(required = false) String start,
                                    @RequestParam(required = false) String end,
                                    @RequestParam(value = "uris", required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        if (unique) {
            return service.getStatsByUniqueIp(parseToLocalDateTime(start),
                    parseToLocalDateTime(end), arrayToList(uris));
        } else {
            return service.getStats(parseToLocalDateTime(start),
                    parseToLocalDateTime(end), arrayToList(uris));
        }
    }

    /*---------------Вспомогательные методы---------------*/
    private LocalDateTime parseToLocalDateTime(String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (time != null) {
            return LocalDateTime.parse(time, formatter);
        } else {
            return null;
        }
    }

    private List<String> arrayToList(String[] uris) {
        if (uris == null) {
            return null;
        }
        List<String> urisList = Arrays.asList(uris);

        return urisList;
    }
}