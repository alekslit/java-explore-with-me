package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserShortDto {
    private final Long id;
    private final String name;
}