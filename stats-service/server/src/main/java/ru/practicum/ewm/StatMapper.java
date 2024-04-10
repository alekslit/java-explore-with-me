package ru.practicum.ewm;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StatMapper {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /*--------------Основные методы--------------*/
    public static Stat mapToStat(StatDto statDto) {
        Stat stat = Stat.builder()
                .app(statDto.getApp())
                .uri(statDto.getUri())
                .ip(statDto.getIp())
                .timestamp(LocalDateTime.parse(statDto.getTimestamp(), formatter))
                .build();

        return stat;
    }

    public static StatDto mapToStatDto(Stat stat) {
        StatDto statDto = StatDto.builder()
                .id(stat.getId())
                .app(stat.getApp())
                .uri(stat.getUri())
                .ip(stat.getIp())
                .timestamp(formatter.format(stat.getTimestamp()))
                .build();

        return statDto;
    }
}