package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        return repository.save(StatMapper.mapToStat(statDto));
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, Boolean unique) {
        if (unique) {
            return getStatsByUniqueIp(parseToLocalDateTime(start), parseToLocalDateTime(end), uris);
        } else {
            return getStatsByAllIp(parseToLocalDateTime(start), parseToLocalDateTime(end), uris);
        }
    }

    @Override
    public List<ViewStats> getStatsByAllIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        log.debug("Попытка получить список ViewStats.");
        return repository.getStatsByAllIp(start, end, uris);
    }

    @Override
    public List<ViewStats> getStatsByUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        log.debug("Попытка получить список ViewStats по уникальным IP.");
        return repository.getStatsByUniqueIp(start, end, uris);
    }

    @Override
    public Long getUniqueViewsByUri(String uri) {
        log.debug("Попытка получить количество просмотров по уникальным IP.");
        // если не нашли, значит просмотров 0:
        return repository.getUniqueViewsByUri(uri).orElse(0L);
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
}