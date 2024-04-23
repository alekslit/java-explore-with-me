package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public final class ViewStats {
    // название сервиса:
    private final String app;

    // URI сервиса:
    private final String uri;

    // количество просмотров:
    private final Long hits;
}