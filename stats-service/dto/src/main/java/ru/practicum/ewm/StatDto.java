package ru.practicum.ewm;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public final class StatDto {
    private final Long id;
    private final String app;
    private final String uri;
    private final String ip;
    private final String timestamp;
}