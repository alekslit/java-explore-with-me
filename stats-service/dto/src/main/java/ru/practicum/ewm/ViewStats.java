package ru.practicum.ewm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewStats {
    // название сервиса:
    private String app;

    // URI сервиса:
    private String uri;

    // количество просмотров:
    private Long hits;
}