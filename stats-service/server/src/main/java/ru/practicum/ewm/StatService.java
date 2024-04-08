package ru.practicum.ewm;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    Stat saveStat(StatDto statDto);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris);

    List<ViewStats> getStatsByUniqueIp(LocalDateTime start, LocalDateTime end, String[] uris);
}