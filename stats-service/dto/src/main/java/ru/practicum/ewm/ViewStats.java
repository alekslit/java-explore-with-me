package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewStats {
    // название сервиса:
    private final String app;

    // URI сервиса:
    private final String uri;

    // количество просмотров:
    private final Long hits;
}