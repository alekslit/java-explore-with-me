package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
public final class Location {
    // широта:
    private final Double lat;
    // долгота:
    private final Double lon;
}