package ru.practicum.ewm;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    Stat saveStat(StatDto statDto);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    List<ViewStats> getStatsByAllIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStats> getStatsByUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}