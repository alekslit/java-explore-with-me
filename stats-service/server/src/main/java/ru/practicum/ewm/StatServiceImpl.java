package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return getStatsByUniqueIp(start, end, uris);
        } else {
            return getStatsByAllIp(start, end, uris);
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
}