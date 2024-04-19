package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

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
    public List<ViewStats> getStats(@RequestParam(required = false) String start,
                                    @RequestParam(required = false) String end,
                                    @RequestParam(/*value = "uris", */required = false) /*String[]*/List<String> uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        return service.getStats(start, end, uris, unique);
    }
}