package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Location {
    // широта:
    private Double lat;
    // долгота:
    private Double lon;
}