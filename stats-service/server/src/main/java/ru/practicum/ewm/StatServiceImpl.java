package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
    private final StatRepository repository;

    /*--------------------Основные методы--------------------*/
    @Override
    public Stat saveStat(StatDto statDto) {
        log.debug("Попытка сохранить новый объект Stat.");
        Stat stat = repository.save(StatMapper.mapToStat(statDto));

        return stat;
    }

    @Override
    public List<ViewStats> getStats(String start, String end, /*String[]*/List<String> uris, Boolean unique) {
        if (unique) {
            return getStatsByUniqueIp(parseToLocalDateTime(start), parseToLocalDateTime(end), /*arrayToList(*/uris/*)*/);
        } else {
            return getStatsByAllIp(parseToLocalDateTime(start), parseToLocalDateTime(end), /*arrayToList(*/uris/*)*/);
        }
    }

    @Override
    public List<ViewStats> getStatsByAllIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        log.debug("Попытка получить список ViewStats.");
        List<ViewStats> stats = repository.getStatsByAllIp(start, end, uris);

        return stats;
    }

    @Override
    public List<ViewStats> getStatsByUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        log.debug("Попытка получить список ViewStats по уникальным IP.");
        List<ViewStats> stats = repository.getStatsByUniqueIp(start, end, uris);

        return stats;
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