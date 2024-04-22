package ru.practicum.ewm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatDto {
    private Long id;
    private final String app;
    private final String uri;
    private final String ip;
    private final String timestamp;
}