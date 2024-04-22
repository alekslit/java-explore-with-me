package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Location {
    // широта:
    private final Double lat;
    // долгота:
    private final Double lon;
}