package ru.practicum.ewm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {
    @Query("SELECT new ru.practicum.ewm.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stat AS s " +
            "WHERE (COALESCE(:uris) IS NULL OR s.uri IN :uris) " +
            "  AND (CAST(:start AS timestamp) IS NULL OR s.timestamp >= :start) " +
            "  AND (CAST(:end AS timestamp) IS NULL OR s.timestamp <= :end) " +
            "GROUP BY s.app, " +
            "         s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getStatsByAllIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat AS s " +
            "WHERE (COALESCE(:uris) IS NULL OR s.uri IN :uris) " +
            "  AND (CAST(:start AS timestamp) IS NULL OR s.timestamp >= :start) " +
            "  AND (CAST(:end AS timestamp) IS NULL OR s.timestamp <= :end) " +
            "GROUP BY s.app, " +
            "         s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getStatsByUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}