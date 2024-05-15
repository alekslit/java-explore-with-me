package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
public final class UserShortDto {
    private final Long id;
    private final String name;
}